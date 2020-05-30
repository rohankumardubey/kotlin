/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.unwrap;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.jetbrains.kotlin.test.TestRoot;
import org.junit.runner.RunWith;

/*
 * This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}.
 * DO NOT MODIFY MANUALLY.
 */
@SuppressWarnings("all")
@TestRoot("idea")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class UnwrapRemoveTestGenerated extends AbstractUnwrapRemoveTest {
    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/removeExpression")
    public static class RemoveExpression extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestExpressionRemover, this, testDataFilePath);
        }

        @TestMetadata("ifInBlock.kt")
        public void testIfInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeExpression/ifInBlock.kt");
        }

        @TestMetadata("ifInExpressionInReturn.kt")
        public void testIfInExpressionInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeExpression/ifInExpressionInReturn.kt");
        }

        @TestMetadata("ifInReturn.kt")
        public void testIfInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeExpression/ifInReturn.kt");
        }

        @TestMetadata("tryInBlock.kt")
        public void testTryInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeExpression/tryInBlock.kt");
        }

        @TestMetadata("tryInReturn.kt")
        public void testTryInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeExpression/tryInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapThen")
    public static class UnwrapThen extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestThenUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("thenCompoundInBlock.kt")
        public void testThenCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapThen/thenCompoundInBlock.kt");
        }

        @TestMetadata("thenCompoundInReturn.kt")
        public void testThenCompoundInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapThen/thenCompoundInReturn.kt");
        }

        @TestMetadata("thenSimpleInReturn.kt")
        public void testThenSimpleInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapThen/thenSimpleInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapElse")
    public static class UnwrapElse extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestElseUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("elseCompoundInBlock.kt")
        public void testElseCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapElse/elseCompoundInBlock.kt");
        }

        @TestMetadata("elseCompoundInReturn.kt")
        public void testElseCompoundInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapElse/elseCompoundInReturn.kt");
        }

        @TestMetadata("elseSimpleInReturn.kt")
        public void testElseSimpleInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapElse/elseSimpleInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/removeElse")
    public static class RemoveElse extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestElseRemover, this, testDataFilePath);
        }

        @TestMetadata("else.kt")
        public void testElse() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeElse/else.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapLoop")
    public static class UnwrapLoop extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestLoopUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("doWhile.kt")
        public void testDoWhile() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLoop/doWhile.kt");
        }

        @TestMetadata("for.kt")
        public void testFor() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLoop/for.kt");
        }

        @TestMetadata("while.kt")
        public void testWhile() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLoop/while.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapTry")
    public static class UnwrapTry extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestTryUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("tryCompoundInBlock.kt")
        public void testTryCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapTry/tryCompoundInBlock.kt");
        }

        @TestMetadata("tryCompoundInReturn.kt")
        public void testTryCompoundInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapTry/tryCompoundInReturn.kt");
        }

        @TestMetadata("trySimpleInReturn.kt")
        public void testTrySimpleInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapTry/trySimpleInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapCatch")
    public static class UnwrapCatch extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestCatchUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("catchCompoundInBlock.kt")
        public void testCatchCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapCatch/catchCompoundInBlock.kt");
        }

        @TestMetadata("catchCompoundInReturn.kt")
        public void testCatchCompoundInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapCatch/catchCompoundInReturn.kt");
        }

        @TestMetadata("catchSimpleInReturn.kt")
        public void testCatchSimpleInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapCatch/catchSimpleInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/removeCatch")
    public static class RemoveCatch extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestCatchRemover, this, testDataFilePath);
        }

        @TestMetadata("catch.kt")
        public void testCatch() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeCatch/catch.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapFinally")
    public static class UnwrapFinally extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestFinallyUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("finallyCompoundInBlock.kt")
        public void testFinallyCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapFinally/finallyCompoundInBlock.kt");
        }

        @TestMetadata("finallyCompoundInReturn.kt")
        public void testFinallyCompoundInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapFinally/finallyCompoundInReturn.kt");
        }

        @TestMetadata("finallySimpleInReturn.kt")
        public void testFinallySimpleInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapFinally/finallySimpleInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/removeFinally")
    public static class RemoveFinally extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestFinallyRemover, this, testDataFilePath);
        }

        @TestMetadata("finallyInBlock.kt")
        public void testFinallyInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeFinally/finallyInBlock.kt");
        }

        @TestMetadata("finallyInReturn.kt")
        public void testFinallyInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/removeFinally/finallyInReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapLambda")
    public static class UnwrapLambda extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestLambdaUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("lambdaCallCompoundInBlock.kt")
        public void testLambdaCallCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaCallCompoundInBlock.kt");
        }

        @TestMetadata("lambdaCallCompoundInReturn.kt")
        public void testLambdaCallCompoundInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaCallCompoundInReturn.kt");
        }

        @TestMetadata("lambdaCallInBlock.kt")
        public void testLambdaCallInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaCallInBlock.kt");
        }

        @TestMetadata("lambdaCallInBlock2.kt")
        public void testLambdaCallInBlock2() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaCallInBlock2.kt");
        }

        @TestMetadata("lambdaCallSimpleInReturn.kt")
        public void testLambdaCallSimpleInReturn() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaCallSimpleInReturn.kt");
        }

        @TestMetadata("lambdaInBlock.kt")
        public void testLambdaInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaInBlock.kt");
        }

        @TestMetadata("lambdaNonLocalPropertyCompoundInBlock.kt")
        public void testLambdaNonLocalPropertyCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaNonLocalPropertyCompoundInBlock.kt");
        }

        @TestMetadata("lambdaNonLocalPropertyInBlock.kt")
        public void testLambdaNonLocalPropertyInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaNonLocalPropertyInBlock.kt");
        }

        @TestMetadata("lambdaPropertyCompoundInBlock.kt")
        public void testLambdaPropertyCompoundInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaPropertyCompoundInBlock.kt");
        }

        @TestMetadata("lambdaPropertyInBlock.kt")
        public void testLambdaPropertyInBlock() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapLambda/lambdaPropertyInBlock.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/codeInsight/unwrapAndRemove/unwrapFunctionParameter")
    public static class UnwrapFunctionParameter extends AbstractUnwrapRemoveTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTestFunctionParameterUnwrapper, this, testDataFilePath);
        }

        @TestMetadata("functionHasMultiParam.kt")
        public void testFunctionHasMultiParam() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapFunctionParameter/functionHasMultiParam.kt");
        }

        @TestMetadata("functionHasSingleParam.kt")
        public void testFunctionHasSingleParam() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapFunctionParameter/functionHasSingleParam.kt");
        }

        @TestMetadata("functionWithReceiver.kt")
        public void testFunctionWithReceiver() throws Exception {
            runTest("testData/codeInsight/unwrapAndRemove/unwrapFunctionParameter/functionWithReceiver.kt");
        }
    }
}