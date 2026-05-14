package com.shoppoc.architecture;

import org.springframework.boot.SpringBootVersion;

final class SpringBootMajorVersion {

    private SpringBootMajorVersion() {
    }

    static int get() {
        String override = System.getProperty("springBootMajor");

        if (override != null && !override.isBlank()) {
            return Integer.parseInt(override);
        }

        String version = SpringBootVersion.getVersion();

        if (version == null || version.isBlank()) {
            throw new IllegalStateException(
                    "Cannot detect Spring Boot version. Pass -DspringBootMajor=2 or -DspringBootMajor=3."
            );
        }

        int dotIndex = version.indexOf('.');
        String major = dotIndex == -1 ? version : version.substring(0, dotIndex);

        return Integer.parseInt(major);
    }

    static boolean isSpringBoot3OrLater() {
        return get() >= 3;
    }
}