package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.PackageEcosystem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependabotGeneratorTest {
    private DependabotGenerator dependabotGenerator = new DependabotGenerator();

    @Test
    void name() {
        final String content = dependabotGenerator.generate(new Dependabot(
                true,
                List.of(new PackageEcosystem(
                        "gradle",
                        "/",
                        "weekly"
                ))
        ));

        System.out.println(content);
    }
}