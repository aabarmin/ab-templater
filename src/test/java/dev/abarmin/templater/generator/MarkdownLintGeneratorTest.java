package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Dependabot;
import dev.abarmin.templater.model.Repository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownLintGeneratorTest {
    MarkdownLintGenerator generator = new MarkdownLintGenerator();

    @Test
    void name() {
        Repository repository = new Repository(
                "http://test.com",
                "main",
                new Dependabot(true, List.of()),
                List.of()
        );
        String content = generator.generate(repository);

        System.out.println(content);
    }
}