package dev.abarmin.templater.model;

import lombok.NonNull;

import java.util.Collection;

public record Repository(
        @NonNull String remoteUrl,
        @NonNull String mainBranch,
        @NonNull Dependabot dependabot,
        @NonNull Collection<Workflow> workflows) {

    public boolean dependabotEnabled() {
        return dependabot.enabled();
    }
}
