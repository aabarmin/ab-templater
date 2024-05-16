package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.PackageEcosystem;
import dev.abarmin.templater.script.Script;
import org.springframework.stereotype.Component;

@Component
public class DependabotGenerator {
    public String generate(Dependabot dependabot) {
        final Script mainPart = new Script()
                .add("version", 2)
                .add("updates", updates -> {
                    for (PackageEcosystem ecosystem : dependabot.ecosystems()) {
                        updates.addItem("package-ecosystem", ecosystem.name(), params -> {
                            params.add("directory", ecosystem.directory());
                            params.add("schedule", schedule -> {
                                schedule.add("interval", ecosystem.interval());
                            });
                        });
                    }
                });

        return mainPart.asString();
    }
}
