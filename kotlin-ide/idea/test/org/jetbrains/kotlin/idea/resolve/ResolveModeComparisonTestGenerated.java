/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.resolve;

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
@TestMetadata("testData/resolve/resolveModeComparison")
public class ResolveModeComparisonTestGenerated extends AbstractResolveModeComparisonTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("Classes.kt")
    public void testClasses() throws Exception {
        runTest("testData/resolve/resolveModeComparison/Classes.kt");
    }

    @TestMetadata("FileAnnotations.kt")
    public void testFileAnnotations() throws Exception {
        runTest("testData/resolve/resolveModeComparison/FileAnnotations.kt");
    }

    @TestMetadata("Functions.kt")
    public void testFunctions() throws Exception {
        runTest("testData/resolve/resolveModeComparison/Functions.kt");
    }

    @TestMetadata("NestedFunctions.kt")
    public void testNestedFunctions() throws Exception {
        runTest("testData/resolve/resolveModeComparison/NestedFunctions.kt");
    }
}