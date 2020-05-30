/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.test;

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
@TestRoot("jvm-debugger/test")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
@TestMetadata("testData/xcoroutines")
public class XCoroutinesStackTraceTestGenerated extends AbstractXCoroutinesStackTraceTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("coroutineSuspendFun.kt")
    public void testCoroutineSuspendFun() throws Exception {
        runTest("testData/xcoroutines/coroutineSuspendFun.kt");
    }

    @TestMetadata("coroutineSuspendFun136.kt")
    public void testCoroutineSuspendFun136() throws Exception {
        runTest("testData/xcoroutines/coroutineSuspendFun136.kt");
    }

    @TestMetadata("suspendMain.kt")
    public void testSuspendMain() throws Exception {
        runTest("testData/xcoroutines/suspendMain.kt");
    }
}