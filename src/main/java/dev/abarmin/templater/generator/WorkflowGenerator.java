package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.model.Workflow;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowGenerator {
    private final GradleWorkflowGenerator gradleGenerator;
    private final MavenWorkflowGenerator mavenGenerator;
    private final MarkdownLintGenerator markdownLintGenerator;

    public String generate(Repository repository, Workflow workflow) {
        if (StringUtils.equals(workflow.type(), "gradle")) {
            return gradleGenerator.generate(repository, workflow);
        }
        if (StringUtils.equals(workflow.type(), "gradle")) {
            return mavenGenerator.generate(repository, workflow);
        }
        if (StringUtils.equals(workflow.type(), "markdown-lint")) {
            return markdownLintGenerator.generate(repository);
        }
        throw new UnsupportedOperationException("Unsupported workflow " + workflow.type());
    }
}
