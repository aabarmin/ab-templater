package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.Repository;
import org.junit.jupiter.api.Test;

import java.util.List;

class MavenWorkflowGeneratorTest {
    private MavenWorkflowGenerator generator = new MavenWorkflowGenerator();

    @Test
    void name() {
        final Repository repository = new Repository(
                "http://github",
                "main",
                new Dependabot(
                        false,
                        List.of()
                ),
                List.of()
        );

        final String generated = generator.generate(repository);

        System.out.println(generated);
    }
}