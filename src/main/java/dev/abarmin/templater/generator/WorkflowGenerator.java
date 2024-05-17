package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;

public interface WorkflowGenerator {
    boolean supports(String workflowType);

    String generate(Repository repository);
}
