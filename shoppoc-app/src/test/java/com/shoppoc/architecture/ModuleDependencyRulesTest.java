package com.shoppoc.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.shoppoc", importOptions = ImportOption.DoNotIncludeTests.class)
class ModuleDependencyRulesTest {

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
    static final ArchRule user_must_not_access_other_modules_domain_or_infrastructure = noClasses()
            .that().resideInAPackage("com.shoppoc.user..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.shoppoc.catalog.domain..", "com.shoppoc.catalog.infrastructure..",
                    "com.shoppoc.order.domain..", "com.shoppoc.order.infrastructure..",
                    "com.shoppoc.payment.domain..", "com.shoppoc.payment.infrastructure.."
            )
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule catalog_must_not_access_other_modules_domain_or_infrastructure = noClasses()
            .that().resideInAPackage("com.shoppoc.catalog..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.shoppoc.user.domain..", "com.shoppoc.user.infrastructure..",
                    "com.shoppoc.order.domain..", "com.shoppoc.order.infrastructure..",
                    "com.shoppoc.payment.domain..", "com.shoppoc.payment.infrastructure.."
            );

    @ArchTest
    static final ArchRule order_must_not_access_other_modules_domain_or_infrastructure = noClasses()
            .that().resideInAPackage("com.shoppoc.order..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.shoppoc.user.domain..", "com.shoppoc.user.infrastructure..",
                    "com.shoppoc.catalog.domain..", "com.shoppoc.catalog.infrastructure..",
                    "com.shoppoc.payment.domain..", "com.shoppoc.payment.infrastructure.."
            )
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule payment_must_not_access_other_modules_domain_or_infrastructure = noClasses()
            .that().resideInAPackage("com.shoppoc.payment..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.shoppoc.user.domain..", "com.shoppoc.user.infrastructure..",
                    "com.shoppoc.catalog.domain..", "com.shoppoc.catalog.infrastructure..",
                    "com.shoppoc.order.domain..", "com.shoppoc.order.infrastructure.."
            )
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule top_level_modules_should_be_cycle_free = slices()
            .matching("com.shoppoc.(*)..")
            .should().beFreeOfCycles();
}
