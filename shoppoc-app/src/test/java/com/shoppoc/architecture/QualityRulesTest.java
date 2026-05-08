package com.shoppoc.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.shoppoc", importOptions = ImportOption.DoNotIncludeTests.class)
class QualityRulesTest {

    @ArchTest
    static final ArchRule no_jakarta_imports = noClasses()
            .that().resideInAPackage("com.shoppoc..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("jakarta..");

    @ArchTest
    static final ArchRule no_lombok_imports = noClasses()
            .that().resideInAPackage("com.shoppoc..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("lombok..");
}
