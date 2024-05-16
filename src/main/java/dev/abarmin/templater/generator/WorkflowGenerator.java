package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowGenerator {
    private final GradleWorkflowGenerator gradleGenerator;
    private final MavenWorkflowGenerator mavenGenerator;
    private final MarkdownLintGenerator markdownLintGenerator;

    public String generate(Repository repository, String workflow) {
        if (StringUtils.equals(workflow, "gradle")) {
            return gradleGenerator.generate(repository);
        }
        if (StringUtils.equals(workflow, "gradle")) {
            return mavenGenerator.generate(repository);
        }
        if (StringUtils.equals(workflow, "markdown-lint")) {
            return markdownLintGenerator.generate(repository);
        }
        throw new UnsupportedOperationException("Unsupported workflow " + workflow);
    }
}
