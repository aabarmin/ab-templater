package dev.abarmin.templater.script;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Script {
    private final StringBuilder builder = new StringBuilder();
    private int indentation = 0;

    private Script add(String line) {
        if (indentation > 0) {
            builder.append(StringUtils.repeat("  ", indentation));
        }
        builder.append(line);
        builder.append(System.lineSeparator());
        return this;
    }

    public Script add(String key, Collection<String> values) {
        final String value = values.stream()
                .map(v -> String.format("\"%s\"", v))
                .collect(Collectors.joining(", "));
        add(String.format(
                "%s: [ %s ]",
                key, value));
        return this;
    }

    public Script add(String key, Consumer<Script> next) {
        add(key + ":");
        add(next);
        return this;
    }

    public Script addItem(String key, String value, Consumer<Script> props) {
        add("- " + key, value);
        indent();
        props.accept(this);
        unindent();
        return this;
    }

    public Script add(String key, String value) {
        add(String.format(
                "%s: \"%s\"",
                key, value
        ));
        return this;
    }

    public Script add(String key, int value) {
        add(String.format(
                "%s: %s",
                key, value
        ));
        return this;
    }

    public void indent(String line) {
        indent();
        builder.append(line);
        unindent();
    }

    public Script add(Consumer<Script> next) {
        indent();
        next.accept(this);
        unindent();
        return this;
    }

    private Script indent() {
        indentation++;
        return this;
    }

    private void unindent() {
        indentation--;
    }

    public String asString() {
        return builder.toString();
    }
}
