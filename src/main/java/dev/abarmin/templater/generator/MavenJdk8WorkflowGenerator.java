package dev.abarmin.templater.generator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MavenJdk8WorkflowGenerator extends MavenWorkflowGenerator {
    public MavenJdk8WorkflowGenerator() {
        super(8);
    }

    @Override
    public boolean supports(String workflowType) {
        return StringUtils.equalsIgnoreCase(workflowType, "maven-jdk8");
    }
}
