package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.script.Script;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static dev.abarmin.templater.generator.WorkflowHelper.checkoutStep;
import static dev.abarmin.templater.generator.WorkflowHelper.onSection;

@Component
public class MarkdownLintGenerator {
    public String generate(Repository repository) {
        Script script = new Script()
                .add("name", "Markdown Lint")
                .add("on", onSection(repository))
                .add("jobs", jobsSection());
        return script.asString();
    }

    private Consumer<Script> jobsSection() {
        return jobs -> {
            jobs.add("markdown-lint", markdownLintJobSection());
        };
    }

    private Consumer<Script> markdownLintJobSection() {
        return lint -> {
            lint
                    .add("runs-on", "ubuntu-latest")
                    .add("steps", steps -> {
                        checkoutStep().accept(steps);
                        performMarkdonwLint().accept(steps);
                    });
        };
    }

    private Consumer<Script> performMarkdonwLint() {
        return step -> {
            step.addItem("name", "Perform Markdown Lint", g -> {
                g.add("uses", "gaurav-nelson/github-action-markdown-link-check@v1");
            });
        };
    }
}
