package com.shoppoc.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.shoppoc", importOptions = ImportOption.DoNotIncludeTests.class)
class ControllerRulesTest {

    @ArchTest
    static final ArchRule controllers_should_live_in_infrastructure_web = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..infrastructure.web..");

    @ArchTest
    static final ArchRule controllers_should_not_reside_outside_infrastructure = noClasses()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAnyPackage("..domain..", "..application..", "..api..");

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repositories = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should(notDependOnRepositories());

    private static ArchCondition<JavaClass> notDependOnRepositories() {
        return new ArchCondition<JavaClass>("not depend on repository types") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaClass dependency : javaClass.getDirectDependenciesFromSelf().stream()
                        .map(dep -> dep.getTargetClass())
                        .toArray(JavaClass[]::new)) {
                    String simpleName = dependency.getSimpleName();
                    boolean forbidden = simpleName.endsWith("Repository") || simpleName.startsWith("SpringData");
                    if (forbidden) {
                        String message = javaClass.getName() + " depends on repository " + dependency.getName();
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };
    }
}
