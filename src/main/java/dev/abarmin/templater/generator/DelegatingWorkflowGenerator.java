package dev.abarmin.templater.generator;

import dev.abarmin.templater.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class DelegatingWorkflowGenerator {
    private final Collection<WorkflowGenerator> generators;

    public String generate(Repository repository, String workflow) {
        return generators.stream()
                .filter(generator -> generator.supports(workflow))
                .findFirst()
                .map(generator -> generator.generate(repository))
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported workflow " + workflow));
    }
}
