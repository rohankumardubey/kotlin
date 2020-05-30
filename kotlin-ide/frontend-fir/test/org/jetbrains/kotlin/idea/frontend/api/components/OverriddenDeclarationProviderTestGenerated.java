/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.frontend.api.components;

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
@TestRoot("frontend-fir")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
@TestMetadata("testData/components/overridenDeclarations")
public class OverriddenDeclarationProviderTestGenerated extends AbstractOverriddenDeclarationProviderTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("inOtherFile.kt")
    public void testInOtherFile() throws Exception {
        runTest("testData/components/overridenDeclarations/inOtherFile.kt");
    }

    @TestMetadata("intersectionOverride.kt")
    public void testIntersectionOverride() throws Exception {
        runTest("testData/components/overridenDeclarations/intersectionOverride.kt");
    }

    @TestMetadata("intersectionOverride2.kt")
    public void testIntersectionOverride2() throws Exception {
        runTest("testData/components/overridenDeclarations/intersectionOverride2.kt");
    }

    @TestMetadata("javaAccessors.kt")
    public void testJavaAccessors() throws Exception {
        runTest("testData/components/overridenDeclarations/javaAccessors.kt");
    }

    @TestMetadata("multipleInterfaces.kt")
    public void testMultipleInterfaces() throws Exception {
        runTest("testData/components/overridenDeclarations/multipleInterfaces.kt");
    }

    @TestMetadata("sequenceOfOverrides.kt")
    public void testSequenceOfOverrides() throws Exception {
        runTest("testData/components/overridenDeclarations/sequenceOfOverrides.kt");
    }
}