package com.shoppoc.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.shoppoc", importOptions = ImportOption.DoNotIncludeTests.class)
class SharedAndAppRulesTest {

    @ArchTest
    static final ArchRule shared_must_not_depend_on_business_modules = noClasses()
            .that().resideInAPackage("com.shoppoc.shared..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.shoppoc.user..",
                    "com.shoppoc.catalog..",
                    "com.shoppoc.order..",
                    "com.shoppoc.payment.."
            );

    @ArchTest
    static final ArchRule shared_should_not_contain_workflow_or_adapter_types = noClasses()
            .that().resideInAPackage("com.shoppoc.shared..")
            .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("ApplicationService")
            .orShould().haveSimpleNameEndingWith("UseCase")
            .orShould().haveSimpleNameEndingWith("RepositoryAdapter")
            .orShould().haveSimpleNameEndingWith("JpaEntity");

    @ArchTest
    static final ArchRule app_must_not_depend_on_business_domain_application_or_persistence = noClasses()
            .that().resideInAPackage("com.shoppoc.app..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.shoppoc.user.domain..", "com.shoppoc.user.application..", "com.shoppoc.user.infrastructure.persistence..",
                    "com.shoppoc.catalog.domain..", "com.shoppoc.catalog.application..", "com.shoppoc.catalog.infrastructure.persistence..",
                    "com.shoppoc.order.domain..", "com.shoppoc.order.application..", "com.shoppoc.order.infrastructure.persistence..",
                    "com.shoppoc.payment.domain..", "com.shoppoc.payment.application..", "com.shoppoc.payment.infrastructure.persistence.."
            );

    @ArchTest
    static final ArchRule app_should_not_declare_business_type_names = classes()
            .that().resideInAPackage("com.shoppoc.app..")
            .should().haveSimpleNameNotEndingWith("ApplicationService")
            .andShould().haveSimpleNameNotEndingWith("UseCase")
            .andShould().haveSimpleNameNotEndingWith("RepositoryAdapter")
            .andShould().haveSimpleNameNotEndingWith("JpaEntity");
}
