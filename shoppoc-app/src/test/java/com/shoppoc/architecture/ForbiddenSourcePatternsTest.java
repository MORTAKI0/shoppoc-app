package com.shoppoc.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

class ForbiddenSourcePatternsTest {

    private static final Pattern RECORD_DECLARATION_PATTERN =
            Pattern.compile("\\brecord\\s+[A-Za-z_][A-Za-z0-9_]*\\s*\\(");

    private static final Pattern JAKARTA_IMPORT_PATTERN =
            Pattern.compile("^import\\s+jakarta\\.");

    private static final Pattern LEGACY_JAVA_EE_IMPORT_PATTERN = Pattern.compile(
            "^import\\s+javax\\.(persistence|validation|servlet|annotation|transaction|ws\\.rs|xml\\.bind|jms|mail)\\b"
    );

    @Test
    void source_must_not_use_forbidden_patterns() throws IOException {
        Path moduleDir = Path.of("").toAbsolutePath();
        Path repoRoot = moduleDir.getParent() == null ? moduleDir : moduleDir.getParent();

        int springBootMajor = SpringBootMajorVersion.get();

        List<String> violations = new ArrayList<>();

        for (String module : List.of(
                "shoppoc-app",
                "shoppoc-shared",
                "shoppoc-user",
                "shoppoc-catalog",
                "shoppoc-order",
                "shoppoc-payment"
        )) {
            scanMainJavaSources(
                    repoRoot.resolve(module).resolve("src").resolve("main").resolve("java"),
                    violations,
                    springBootMajor
            );

            scanTestJavaSources(
                    repoRoot.resolve(module).resolve("src").resolve("test").resolve("java"),
                    violations
            );
        }

        assertTrue(
                violations.isEmpty(),
                "Forbidden source patterns found:\n" + String.join("\n", violations)
        );
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

    private static void scanMainJavaSources(
            Path sourceRoot,
            List<String> violations,
            int springBootMajor
    ) throws IOException {
        if (!Files.exists(sourceRoot)) {
            return;
        }

        try (Stream<Path> stream = Files.walk(sourceRoot)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> checkMainPath(path, violations, springBootMajor));
        }
    }

    private static void scanTestJavaSources(Path sourceRoot, List<String> violations) throws IOException {
        if (!Files.exists(sourceRoot)) {
            return;
        }

        try (Stream<Path> stream = Files.walk(sourceRoot)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> checkTestPath(path, violations));
        }
    }

    private static void checkMainPath(Path path, List<String> violations, int springBootMajor) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (springBootMajor < 3 && JAKARTA_IMPORT_PATTERN.matcher(line).find()) {
                    violations.add(path + ":" + (i + 1) + " uses jakarta import in Spring Boot 2 code");
                }

                if (springBootMajor >= 3 && LEGACY_JAVA_EE_IMPORT_PATTERN.matcher(line).find()) {
                    violations.add(path + ":" + (i + 1) + " uses legacy Java EE javax import in Spring Boot 3 code");
                }

                if (line.startsWith("import lombok.") || line.startsWith("@lombok.")) {
                    violations.add(path + ":" + (i + 1) + " uses lombok");
                }

                if (line.contains("@Autowired") && looksLikeAutowiredField(lines, i + 1)) {
                    violations.add(path + ":" + (i + 1) + " uses @Autowired field injection");
                }

                if (RECORD_DECLARATION_PATTERN.matcher(line).find()) {
                    violations.add(path + ":" + (i + 1) + " uses Java record");
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read source file " + path, ex);
        }
    }

    private static void checkTestPath(Path path, List<String> violations) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.matches("^@Disabled(\\b|\\().*")) {
                    violations.add(path + ":" + (i + 1) + " uses @Disabled");
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read source file " + path, ex);
        }
    }

    private static boolean looksLikeAutowiredField(List<String> lines, int startIndexExclusive) {
        int checked = 0;

        for (int i = startIndexExclusive; i < lines.size() && checked < 3; i++, checked++) {
            String next = lines.get(i).trim();

            if (next.isEmpty()) {
                continue;
            }

            return next.endsWith(";") && !next.contains("(");
        }

        return false;
    }
}