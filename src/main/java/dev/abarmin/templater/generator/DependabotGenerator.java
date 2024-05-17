package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.PackageEcosystem;
import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DependabotGenerator implements WorkflowGenerator {
    @Override
    public boolean supports(String workflowType) {
        return StringUtils.equalsIgnoreCase(workflowType, "dependabot");
    }

    @Override
    public String generate(Repository repository) {
        return generate(repository.dependabot());
    }

    private String generate(Dependabot dependabot) {
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
