package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static dev.abarmin.templater.generator.WorkflowHelper.checkoutStep;
import static dev.abarmin.templater.generator.WorkflowHelper.installGradle;
import static dev.abarmin.templater.generator.WorkflowHelper.installJava21;
import static dev.abarmin.templater.generator.WorkflowHelper.onSection;

@Component
public class TemplaterGenerator {
    public String generate(Repository repository) {
        Script script = new Script()
                .add("name", "Markdown Lint")
                .add("on", onSection())
                .add("jobs", jobsSection());
        return script.asString();
    }

    private Consumer<Script> jobsSection() {
        return jobs -> {
            jobs.add("execute:", ex -> {
                ex
                        .add("runs-on", "ubuntu-latest")
                        .add("steps", steps -> {
                            checkoutStep().accept(steps);
                            installJava21().accept(steps);
                            installGradle().accept(steps);
                            runTemplater().accept(steps);
                        });
            });
        };
    }

    private Consumer<Script> runTemplater() {
        return step -> {
            step.addItem("name", "Run Templater", g -> {
                g.add("run", "gradle bootRun --args='--git.credentials.login=${GIT_LOGIN} --git.credentials.password=${GIT_PASSWORD}'");
            });
        };
    }

    private Consumer<Script> onSection() {
        return on -> {
            on.add("workflow_dispatch", d -> {
            });
        };
    }
}
