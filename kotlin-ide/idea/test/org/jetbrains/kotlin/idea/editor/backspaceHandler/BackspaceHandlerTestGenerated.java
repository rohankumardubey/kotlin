/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.backspaceHandler;

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
@TestMetadata("testData/editor/backspaceHandler")
public class BackspaceHandlerTestGenerated extends AbstractBackspaceHandlerTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("rawStringDelete.kt")
    public void testRawStringDelete() throws Exception {
        runTest("testData/editor/backspaceHandler/rawStringDelete.kt");
    }

    @TestMetadata("rawStringInCloseQuote.kt")
    public void testRawStringInCloseQuote() throws Exception {
        runTest("testData/editor/backspaceHandler/rawStringInCloseQuote.kt");
    }

    @TestMetadata("rawStringInOpenQuote.kt")
    public void testRawStringInOpenQuote() throws Exception {
        runTest("testData/editor/backspaceHandler/rawStringInOpenQuote.kt");
    }

    @TestMetadata("typeArguments.kt")
    public void testTypeArguments() throws Exception {
        runTest("testData/editor/backspaceHandler/typeArguments.kt");
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/editor/backspaceHandler/stringTemplate")
    public static class StringTemplate extends AbstractBackspaceHandlerTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("escapedStringTemplate.kt")
        public void testEscapedStringTemplate() throws Exception {
            runTest("testData/editor/backspaceHandler/stringTemplate/escapedStringTemplate.kt");
        }

        @TestMetadata("stringTemplateBrackets.kt")
        public void testStringTemplateBrackets() throws Exception {
            runTest("testData/editor/backspaceHandler/stringTemplate/stringTemplateBrackets.kt");
        }
    }
}