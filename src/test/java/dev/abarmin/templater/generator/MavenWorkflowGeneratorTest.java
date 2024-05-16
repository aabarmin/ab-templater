package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.model.Workflow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MavenWorkflowGeneratorTest {
    private MavenWorkflowGenerator generator = new MavenWorkflowGenerator();

    @Test
    void name() {
        final Workflow workflow = new Workflow("maven");
        final Repository repository = new Repository(
                "http://github",
                "main",
                new Dependabot(
                        false,
                        List.of()
                ),
                List.of(workflow)
        );

        final String generated = generator.generate(repository, workflow);

        System.out.println(generated);
    }
}