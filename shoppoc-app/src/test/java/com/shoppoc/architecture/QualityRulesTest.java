package com.shoppoc.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.boot.SpringBootVersion;

@AnalyzeClasses(packages = "com.shoppoc", importOptions = ImportOption.DoNotIncludeTests.class)
class QualityRulesTest {

    @ArchTest
    static final ArchRule no_wrong_ee_namespace =
            isSpringBoot3OrLater()
                    ? noClasses()
                      .that().resideInAPackage("com.shoppoc..")
                      .should().dependOnClassesThat().resideInAnyPackage(
                    "javax.persistence..",
                    "javax.validation..",
                    "javax.servlet..",
                    "javax.annotation..",
                    "javax.transaction..",
                    "javax.ws.rs..",
                    "javax.xml.bind..",
                    "javax.jms..",
                    "javax.mail.."
            )
                    : noClasses()
                      .that().resideInAPackage("com.shoppoc..")
                      .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.."
            );

    @ArchTest
    static final ArchRule no_lombok_imports =
            noClasses()
                    .that().resideInAPackage("com.shoppoc..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("lombok..");

    private static boolean isSpringBoot3OrLater() {
        return detectSpringBootMajorVersion() >= 3;
    }

    private static int detectSpringBootMajorVersion() {
        String override = System.getProperty("springBootMajor");

        if (override != null && !override.isBlank()) {
            return Integer.parseInt(override);
        }

        String version = SpringBootVersion.getVersion();

        if (version == null || version.isBlank()) {
            throw new IllegalStateException(
                    "Cannot detect Spring Boot version. " +
                            "Pass -DspringBootMajor=2 or -DspringBootMajor=3."
            );
        }

        int dotIndex = version.indexOf('.');
        String major = dotIndex == -1 ? version : version.substring(0, dotIndex);

        return Integer.parseInt(major);
    }
}