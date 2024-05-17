package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.PackageEcosystem;
import dev.abarmin.templater.model.Repository;
import org.junit.jupiter.api.Test;

import java.util.List;

class DependabotGeneratorTest {
    private DependabotGenerator dependabotGenerator = new DependabotGenerator();

    @Test
    void name() {
        Dependabot dependabot = new Dependabot(
                true,
                List.of(new PackageEcosystem(
                        "gradle",
                        "/",
                        "weekly"
                ))
        );
        final String content = dependabotGenerator.generate(new Repository(
                "",
                "",
                dependabot,
                List.of()
        ));

        System.out.println(content);
    }
}