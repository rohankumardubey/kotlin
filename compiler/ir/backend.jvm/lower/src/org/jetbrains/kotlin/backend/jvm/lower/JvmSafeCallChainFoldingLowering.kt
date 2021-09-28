/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.backend.jvm.lower

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.phaser.makeIrFilePhase
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.JvmLoweredStatementOrigin
import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid


val jvmSafeCallFoldingPhase = makeIrFilePhase(
    ::JvmSafeCallChainFoldingLowering,
    name = "JvmSafeCallChainFoldingLowering",
    description = "Fold safe call chains to more compact forms"
)


class JvmSafeCallChainFoldingLowering(val context: JvmBackendContext) : FileLoweringPass {
    // Overall idea here is to represent (possibly chained) safe calls as an if-expression in the form:
    //      if ( { val tmp = <safe_receiver>; tmp != null } )
    //          <safe_call>
    //      else
    //          null
    // This allows chaining safe calls like 'a?.foo()?.bar()?.qux()':
    //      if ( { val tmp1 = a; tmp1 != null } &&
    //           { val tmp2 = tmp1.foo(); tmp2 != null } &&
    //           { val tmp3 = tmp2.bar(); tmp3 != null }
    //      )
    //          tmp3.qux()
    //      else
    //          null
    // This also allows fusing safe calls with elvises (and some other operations).

    override fun lower(irFile: IrFile) {
        irFile.transformChildrenVoid(Transformer())
    }

    private val booleanNot = context.irBuiltIns.booleanNotSymbol

    private fun IrExpression.irNot() =
        IrCallImpl.fromSymbolOwner(startOffset, endOffset, booleanNot).apply {
            dispatchReceiver = this@irNot
        }

    private fun irAndAnd(left: IrExpression, right: IrExpression): IrExpression =
        IrCallImpl.fromSymbolOwner(right.startOffset, right.endOffset, context.irBuiltIns.andandSymbol).apply {
            putValueArgument(0, left)
            putValueArgument(1, right)
        }

    private fun IrExpression.irEqEqNull(): IrExpression =
        IrCallImpl.fromSymbolOwner(this.startOffset, this.endOffset, context.irBuiltIns.eqeqSymbol).apply {
            putValueArgument(0, this@irEqEqNull)
            putValueArgument(1, IrConstImpl.constNull(startOffset, endOffset, context.irBuiltIns.nothingNType))
        }

    private fun IrExpression.wrapWithBlock(origin: IrStatementOrigin?): IrBlock =
        IrBlockImpl(this.startOffset, this.endOffset, this.type, origin, listOf(this))

    private fun irTrue(startOffset: Int, endOffset: Int) =
        IrConstImpl.boolean(startOffset, endOffset, context.irBuiltIns.booleanType, true)

    private fun irFalse(startOffset: Int, endOffset: Int) =
        IrConstImpl.boolean(startOffset, endOffset, context.irBuiltIns.booleanType, false)

    private fun irValNotNull(startOffset: Int, endOffset: Int, irVariable: IrVariable): IrExpression =
        if (irVariable.type.isNullable())
            IrGetValueImpl(startOffset, endOffset, irVariable.symbol).irEqEqNull().irNot()
        else
            irTrue(startOffset, endOffset)

    private fun IrType.isJvmPrimitive(): Boolean =
        // TODO get rid of type mapper (take care of '@EnhancedNullability', maybe some other stuff).
        AsmUtil.isPrimitive(context.typeMapper.mapType(this))

    private inner class Transformer : IrElementTransformerVoid() {
        override fun visitBlock(expression: IrBlock): IrExpression {
            expression.transformChildrenVoid()

            val safeCallInfo = expression.parseSafeCall(context.irBuiltIns)
            if (safeCallInfo != null) {
                return foldSafeCall(safeCallInfo)
            }

            val elvisInfo = expression.parseElvis(context.irBuiltIns)
            if (elvisInfo != null) {
                return foldElvis(elvisInfo)
            }

            // TODO 'as?'

            return expression
        }

        private fun foldSafeCall(safeCallInfo: SafeCallInfo): IrExpression {
            // Rewrite a safe call in the form:
            //      {   // SAFE_CALL
            //          val tmp = <safe_receiver>
            //          if (tmp == null)
            //              null
            //          else
            //              <call[tmp]>
            //      }
            val safeCallBlock = safeCallInfo.block
            val startOffset = safeCallBlock.startOffset
            val endOffset = safeCallBlock.endOffset
            val safeCallType = safeCallBlock.type
            val safeCallTmpVal = safeCallInfo.tmpVal

            val tmpValInitializer = safeCallTmpVal.initializer
            if (tmpValInitializer is IrBlock && tmpValInitializer.origin == JvmLoweredStatementOrigin.FOLDED_SAFE_CALL) {
                // Chained safe call.
                // If <safe_receiver> is a FOLDED_SAFE_CALL form, rewrite safe call to:
                //      {   // FOLDED_SAFE_CALL
                //          if ( <safe_receiver_condition> && { val tmp = <safe_receiver_result>; tmp != null } )
                //              <call[tmp]>
                //          else
                //              null
                //      }
                //    where
                //      <safe_receiver> =
                //          {   // FOLDED_SAFE_CALL
                //              if ( <safe_receiver_condition> )
                //                  <safe_receiver_result>
                //              else
                //                  null
                //          }
                val foldedBlock: IrBlock = tmpValInitializer
                val foldedWhen = foldedBlock.statements[0] as IrWhen
                val safeReceiverCondition = foldedWhen.branches[0].condition
                val safeReceiverResult = foldedWhen.branches[0].result
                safeCallTmpVal.initializer = safeReceiverResult
                safeCallTmpVal.type = safeReceiverResult.type
                val foldedConditionPart =
                    IrCompositeImpl(
                        startOffset, endOffset, context.irBuiltIns.booleanType, null,
                        listOf<IrStatement>(
                            safeCallTmpVal,
                            irValNotNull(startOffset, endOffset, safeCallTmpVal)
                        )
                    )
                foldedBlock.type = safeCallType
                foldedWhen.type = safeCallType
                foldedWhen.branches[0].condition = irAndAnd(safeReceiverCondition, foldedConditionPart)
                foldedWhen.branches[0].result = safeCallInfo.ifNotNullBranch.result
                return foldedBlock
            } else {
                // Simple safe call.
                // If <safe_receiver> itself is not a FOLDED_SAFE_CALL form, rewrite safe call to:
                //      {   // FOLDED_SAFE_CALL
                //          if ( { val tmp = <safe_receiver>; tmp != null } )
                //              <call[tmp]>
                //          else
                //              null
                //      }

                val foldedCondition =
                    IrCompositeImpl(
                        startOffset, endOffset, context.irBuiltIns.booleanType, null,
                        listOf<IrStatement>(
                            safeCallTmpVal,
                            irValNotNull(startOffset, endOffset, safeCallTmpVal)
                        )
                    )
                val safeCallResult = safeCallInfo.ifNotNullBranch.result
                val nullResult = safeCallInfo.ifNullBranch.result
                val foldedWhen = IrWhenImpl(
                    startOffset, endOffset, safeCallType, JvmLoweredStatementOrigin.FOLDED_SAFE_CALL,
                    listOf(
                        IrBranchImpl(startOffset, endOffset, foldedCondition, safeCallResult),
                        IrBranchImpl(startOffset, endOffset, irTrue(startOffset, endOffset), nullResult)
                    )
                )
                return foldedWhen.wrapWithBlock(JvmLoweredStatementOrigin.FOLDED_SAFE_CALL)
            }
        }

        private fun foldElvis(elvisInfo: ElvisInfo): IrExpression {
            val elvisLhs = elvisInfo.elvisLhs
            val elvisBlock = elvisInfo.block
            val startOffset = elvisBlock.startOffset
            val endOffset = elvisBlock.endOffset
            val elvisType = elvisBlock.type
            val elvisTmpVal = elvisInfo.tmpVal

            when {
                elvisLhs is IrBlock && elvisLhs.origin == JvmLoweredStatementOrigin.FOLDED_SAFE_CALL -> {
                    // Fold elvis with safe call.
                    // Given elvis expression:
                    //      {   // ELVIS
                    //          val tmp = <elvis_lhs>
                    //          if (tmp == null)
                    //              <elvis_rhs>
                    //          else
                    //              null
                    //      }
                    // where <elvis_lhs> is a folded safe call in the form:
                    //      {   // FOLDED_SAFE_CALL
                    //          if ( <safe_call_condition> )
                    //              <safe_call_result>
                    //          else
                    //              null
                    //      }
                    // rewrite it to
                    //      {   // FOLDED_ELVIS
                    //          if ( <safe_call_condition> && { val tmp = <safe_call_result>; tmp != null } )
                    //              tmp
                    //          else
                    //              <elvis_rhs>
                    //      }

                    val safeCallWhen = elvisLhs.statements[0] as IrWhen
                    val safeCallCondition = safeCallWhen.branches[0].condition
                    val safeCallResult = safeCallWhen.branches[0].result
                    elvisTmpVal.initializer = safeCallResult
                    elvisTmpVal.type = safeCallResult.type
                    val foldedConditionPart =
                        IrCompositeImpl(
                            startOffset, endOffset, context.irBuiltIns.booleanType, null,
                            listOf<IrStatement>(
                                elvisTmpVal,
                                irValNotNull(startOffset, endOffset, elvisTmpVal)
                            )
                        )
                    val foldedWhen = IrWhenImpl(
                        startOffset, endOffset, elvisType, JvmLoweredStatementOrigin.FOLDED_ELVIS,
                        listOf(
                            IrBranchImpl(
                                startOffset, endOffset,
                                irAndAnd(safeCallCondition, foldedConditionPart),
                                IrGetValueImpl(startOffset, endOffset, elvisTmpVal.symbol)
                            ),
                            IrBranchImpl(
                                startOffset, endOffset,
                                irTrue(startOffset, endOffset),
                                elvisInfo.elvisRhs
                            )
                        )
                    )
                    return foldedWhen.wrapWithBlock(JvmLoweredStatementOrigin.FOLDED_ELVIS)
                }
                elvisLhs is IrBlock && elvisLhs.origin == JvmLoweredStatementOrigin.FOLDED_ELVIS -> {
                    // Append branches to the inner elvis:
                    //      val t = { // FOLDED_ELVIS
                    //          if (...) ...
                    //          else if ...
                    //          else <innerElvisRhs>
                    //      }
                    //      if (t != null) t else <outerElvisRhs>
                    //  =>
                    //      { // FOLDED_ELVIS
                    //          if (...) ...
                    //          else if ...
                    //          else if ( { val t = <innerElvisRhs>; t != null } )
                    //              t
                    //          else
                    //              <outerElvisRhs>
                    //      }
                    // TODO maybe we can do somewhat better if we analyze innerElvisRhs as well
                    val innerElvisWhen = elvisLhs.statements[0] as IrWhen
                    val innerElvisLastBranch = innerElvisWhen.branches.last()
                    val innerElvisRhs = innerElvisLastBranch.result
                    elvisTmpVal.initializer = innerElvisRhs
                    elvisTmpVal.type = innerElvisRhs.type
                    val newCondition = IrCompositeImpl(
                        startOffset, endOffset, context.irBuiltIns.booleanType, null,
                        listOf(
                            elvisTmpVal, irValNotNull(startOffset, endOffset, elvisTmpVal)
                        )
                    )
                    innerElvisLastBranch.condition = newCondition
                    innerElvisLastBranch.result = IrGetValueImpl(startOffset, endOffset, elvisTmpVal.symbol)
                    innerElvisWhen.branches.add(
                        IrBranchImpl(
                            startOffset, endOffset,
                            irTrue(startOffset, endOffset),
                            elvisInfo.elvisRhs
                        )
                    )
                    innerElvisWhen.type = elvisType
                    return innerElvisWhen.wrapWithBlock(JvmLoweredStatementOrigin.FOLDED_ELVIS)
                }
                else -> {
                    return elvisInfo.block
                }
            }
        }

        override fun visitCall(expression: IrCall): IrExpression {
            expression.transformChildrenVoid()

            if (expression.symbol == context.irBuiltIns.eqeqSymbol) {
                val startOffset = expression.startOffset
                val endOffset = expression.endOffset

                val left = expression.getValueArgument(0)
                    ?: throw AssertionError("No value argument #0: ${expression.dump()}")
                val right = expression.getValueArgument(1)
                    ?: throw AssertionError("No value argument #1: ${expression.dump()}")
                if (left is IrBlock && left.origin == JvmLoweredStatementOrigin.FOLDED_SAFE_CALL && right.type.isJvmPrimitive()) {
                    val safeCallWhen = left.statements[0] as IrWhen
                    val safeCallResult = safeCallWhen.branches[0].result
                    expression.putValueArgument(0, safeCallResult)
                    safeCallWhen.branches[0].result = expression
                    safeCallWhen.branches[1].result = irFalse(startOffset, endOffset)
                    safeCallWhen.type = expression.type
                    return safeCallWhen.wrapWithBlock(origin = null)
                }
                if (right is IrBlock && right.origin == JvmLoweredStatementOrigin.FOLDED_SAFE_CALL && left.type.isJvmPrimitive()) {
                    val safeCallWhen = right.statements[0] as IrWhen
                    val safeCallResult = safeCallWhen.branches[0].result
                    expression.putValueArgument(1, safeCallResult)
                    safeCallWhen.branches[0].result = expression
                    safeCallWhen.branches[1].result = irFalse(startOffset, endOffset)
                    safeCallWhen.type = expression.type
                    return safeCallWhen.wrapWithBlock(origin = null)
                }
            }

            return expression
        }

    }

}


internal class SafeCallInfo(
    val block: IrBlock,
    val tmpVal: IrVariable,
    val ifNullBranch: IrBranch,
    val ifNotNullBranch: IrBranch
)

internal fun IrBlock.parseSafeCall(irBuiltIns: IrBuiltIns): SafeCallInfo? {
    //  {
    //      val tmp = <safe_receiver>
    //      when {
    //          tmp == null -> null
    //          else -> <safe_call_result>
    //      }
    //  }

    if (this.statements.size != 2) return null
    val tmpVal = this.statements[0] as? IrVariable ?: return null
    val whenExpr = this.statements[1] as? IrWhen ?: return null
    if (whenExpr.branches.size != 2) return null

    val ifNullBranch = whenExpr.branches[0]
    val ifNullBranchCondition = ifNullBranch.condition
    if (ifNullBranchCondition !is IrCall) return null
    if (ifNullBranchCondition.symbol != irBuiltIns.eqeqSymbol) return null
    val arg0 = ifNullBranchCondition.getValueArgument(0)
    if (arg0 !is IrGetValue || arg0.symbol != tmpVal.symbol) return null
    val arg1 = ifNullBranchCondition.getValueArgument(1)
    if (arg1 !is IrConst<*> || arg1.value != null) return null
    val ifNullBranchResult = ifNullBranch.result
    if (ifNullBranchResult !is IrConst<*> || ifNullBranchResult.value != null) return null

    val ifNotNullBranch = whenExpr.branches[1]
    return SafeCallInfo(this, tmpVal, ifNullBranch, ifNotNullBranch)
}


internal class ElvisInfo(
    val block: IrBlock,
    val tmpVal: IrVariable,
    val elvisLhs: IrExpression,
    val elvisRhs: IrExpression
)

internal fun IrBlock.parseElvis(irBuiltIns: IrBuiltIns): ElvisInfo? {
    //  {
    //      val tmp = <elvis_lhs>
    //      when {
    //          tmp == null -> <elvis_rhs>
    //          else -> tmp
    //      }
    //  }

    if (this.statements.size != 2) return null
    val tmpVal = this.statements[0] as? IrVariable ?: return null
    val whenExpr = this.statements[1] as? IrWhen ?: return null
    if (whenExpr.branches.size != 2) return null

    val elvisLhs = tmpVal.initializer ?: return null
    val ifNullBranch = whenExpr.branches[0]
    val ifNullBranchCondition = ifNullBranch.condition
    if (ifNullBranchCondition !is IrCall) return null
    if (ifNullBranchCondition.symbol != irBuiltIns.eqeqSymbol) return null
    val arg0 = ifNullBranchCondition.getValueArgument(0)
    if (arg0 !is IrGetValue || arg0.symbol != tmpVal.symbol) return null
    val arg1 = ifNullBranchCondition.getValueArgument(1)
    if (arg1 !is IrConst<*> || arg1.value != null) return null
    val elvisRhs = ifNullBranch.result

    val ifNonNullBranch = whenExpr.branches[1]
    val ifNonNullBranchResult = ifNonNullBranch.result
    if (ifNonNullBranchResult !is IrGetValue || ifNonNullBranchResult.symbol != tmpVal.symbol) return null

    return ElvisInfo(this, tmpVal, elvisLhs, elvisRhs)
}
