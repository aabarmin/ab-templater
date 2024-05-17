package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static dev.abarmin.templater.generator.WorkflowHelper.checkoutStep;
import static dev.abarmin.templater.generator.WorkflowHelper.installGradle;
import static dev.abarmin.templater.generator.WorkflowHelper.installJava;
import static dev.abarmin.templater.generator.WorkflowHelper.onSection;

@Component
public class GradleWorkflowGenerator implements WorkflowGenerator {
    @Override
    public boolean supports(String workflowType) {
        return StringUtils.equalsIgnoreCase(workflowType, "gradle");
    }

    @Override
    public String generate(Repository repository) {
        final Script script = new Script()
                .add("name", "Java CI with Gradle")
                .add("on", onSection(repository))
                .add("jobs", jobsSection());

        return script.asString();
    }

    private Consumer<Script> jobsSection() {
        return jobs -> {
            jobs.add("build", buildJobSection());
            jobs.add("dependency-submission", buildDependencySection());
        };
    }

    private Consumer<Script> buildJobSection() {
        return build -> {
            build
                    .add("runs-on", "ubuntu-latest")
                    .add("permissions", p -> {
                        p.add("contents", "read");
                    })
                    .add("steps", steps -> {
                        checkoutStep().accept(steps);
                        installJava().accept(steps);
                        installGradle().accept(steps);
                        buildGradle().accept(steps);
                    });
        };
    }

    private Consumer<Script> buildGradle() {
        return step -> {
            step.addItem("name", "Build with Gradle", g -> {
                g.add("run", "./gradlew build");
            });
        };
    }

    private Consumer<Script> buildDependencySection() {
        return job -> {
            job
                    .add("runs-on", "ubuntu-latest")
                    .add("permissions", p -> {
                        p.add("contents", "write");
                    })
                    .add("if", "github.event_name == 'push'")
                    .add("steps", steps -> {
                        checkoutStep().accept(steps);
                        installJava().accept(steps);
                        submitDependencies().accept(steps);
                    });
        };
    }

    private Consumer<Script> submitDependencies() {
        return step -> {
            step.addItem("name", "Generate and submit dependency graph", s -> {
                s.add("uses", "gradle/actions/dependency-submission@417ae3ccd767c252f5661f1ace9f835f9654f2b5");
            });
        };
    }
}
