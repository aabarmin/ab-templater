package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static dev.abarmin.templater.generator.WorkflowHelper.checkoutStep;
import static dev.abarmin.templater.generator.WorkflowHelper.installJava;
import static dev.abarmin.templater.generator.WorkflowHelper.onSection;

@RequiredArgsConstructor
public abstract class MavenWorkflowGenerator implements WorkflowGenerator {
    private final int jdkVersion;

    @Override
    public String generate(Repository repository) {
        final Script script = new Script()
                .add("name", "Java CI with Maven")
                .add("on", onSection(repository))
                .add("jobs", jobsSection());

        return script.asString();
    }

    private Consumer<Script> jobsSection() {
        return script -> {
            script.add("build", buildSection());
        };
    }

    private Consumer<Script> buildSection() {
        return job -> {
            job.add("runs-on", "ubuntu-latest");
            job.add("steps", steps -> {
                checkoutStep().accept(steps);
                installJava(jdkVersion).accept(steps);
                cacheMavenDependencies().accept(steps);
                buildWithMaven().accept(steps);
            });
        };
    }

    private Consumer<Script> cacheMavenDependencies() {
        return step -> {
            step.addItem("name", "Cache Maven Dependencies", s -> {
                s.add("uses", "actions/cache@v2");
                s.add("with", with -> {
                    with.add("path", "~/.m2/repository");
                    with.add("key", "${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}");
                    with.add("restore-keys", "${{ runner.os }}-maven-");
                });
            });
        };
    }

    private Consumer<Script> buildWithMaven() {
        return step -> {
            step.addItem("name", "Build with Maven", s -> {
                s.add("run", "mvn -B clean verify --file pom.xml");
            });
        };
    }
}
