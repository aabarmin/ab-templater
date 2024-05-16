package dev.abarmin.templater.model;

import lombok.NonNull;

public record PackageEcosystem(
        @NonNull String name,
        @NonNull String directory,
        @NonNull String interval
) {
}
