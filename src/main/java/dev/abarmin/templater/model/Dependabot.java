package dev.abarmin.templater.model;

import lombok.NonNull;

import java.util.Collection;

public record Dependabot(
        boolean enabled,
        @NonNull Collection<PackageEcosystem> ecosystems
) {
}
