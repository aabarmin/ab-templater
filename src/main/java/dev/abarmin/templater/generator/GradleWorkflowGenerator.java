package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static dev.abarmin.templater.generator.WorkflowHelper.checkoutStep;
import static dev.abarmin.templater.generator.WorkflowHelper.installJava21;
import static dev.abarmin.templater.generator.WorkflowHelper.onSection;

@Component
public class GradleWorkflowGenerator {
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
                        installJava21().accept(steps);
                        installGradle().accept(steps);
                        buildGradle().accept(steps);
                    });
        };
    }

    private Consumer<Script> installGradle() {
        return step -> {
            step.addItem("name", "Install Gradle", g -> {
                g.add("uses", "gradle/actions/setup-gradle@417ae3ccd767c252f5661f1ace9f835f9654f2b5");
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
                    .add("steps", steps -> {
                        checkoutStep().accept(steps);
                        installJava21().accept(steps);
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
