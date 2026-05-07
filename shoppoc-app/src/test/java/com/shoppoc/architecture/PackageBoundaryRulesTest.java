package com.shoppoc.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.shoppoc", importOptions = ImportOption.DoNotIncludeTests.class)
class PackageBoundaryRulesTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure_or_web_or_spring_data = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "..infrastructure..",
                    "..web..",
                    "org.springframework.web..",
                    "org.springframework.data.."
            );

    @ArchTest
    static final ArchRule api_must_not_depend_on_infrastructure = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure..");
}
