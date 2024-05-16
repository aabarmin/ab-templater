package dev.abarmin.templater.model;

import lombok.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record Change(@NonNull Collection<String> deleted,
                     @NonNull Collection<String> added) {

    public static Change empty() {
        return new Change(new HashSet<>(), new HashSet<>());
    }

    public static Change singleAdd(@NonNull String path) {
        return empty().add(path);
    }

    public Change rm(@NonNull String path) {
        deleted.add(path);
        return this;
    }

    public Change add(@NonNull String path) {
        added.add(path);
        return this;
    }

    public Change join(@NonNull Change change) {
        added.addAll(change.added());
        deleted.addAll(change.deleted());
        return this;
    }

    public Collection<String> deleted() {
        return deleted.stream()
                .filter(Predicate.not(added::contains))
                .toList();
    }
}
