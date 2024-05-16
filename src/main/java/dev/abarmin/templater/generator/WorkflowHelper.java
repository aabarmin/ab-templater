package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class WorkflowHelper {
    public static Consumer<Script> checkoutStep() {
        return step -> {
            step.addItem("name", "Checkout", c -> {
                c.add("uses", "actions/checkout@v4");
            });
        };
    }

    public static Consumer<Script> installJava21() {
        return step -> {
            step.addItem("name", "Install JDK 21", c -> {
                c.add("uses", "actions/setup-java@v4");
                c.add("with", with -> {
                    with.add("java-version", "21");
                    with.add("distribution", "corretto");
                });
            });
        };
    }

    public static Consumer<Script> onSection(Repository repository) {
        return on -> {
            on.add("push", push -> {
                push.add("branches", List.of(repository.mainBranch()));
            });
            on.add("pull_request", pr -> {
                pr.add("branches", List.of(repository.mainBranch()));
            });
        };
    }
}
