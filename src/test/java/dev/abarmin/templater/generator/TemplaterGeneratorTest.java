package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.Repository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemplaterGeneratorTest {
    TemplaterGenerator generator = new TemplaterGenerator();

    @Test
    void name() {
        Repository repository = new Repository(
                "URL",
                "main",
                new Dependabot(true, List.of()),
                List.of()
        );
        String generate = generator.generate(repository);

        System.out.println(generate);
    }
}