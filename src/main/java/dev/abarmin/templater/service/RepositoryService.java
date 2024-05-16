package dev.abarmin.templater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.abarmin.templater.model.Repository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Collection<Repository> getRepositories() {
        final FileSystemResource resource = new FileSystemResource("./config/repositories.json");
        checkArgument(resource.exists(), "./config/repositories.json file doesn't exist");

        try (final InputStream inputStream = resource.getInputStream()) {
            final Collection<Repository> repositories = objectMapper
                    .readerForListOf(Repository.class)
                    .readValue(inputStream);

            log.info("Found configuration for {} repos", repositories.size());

            return repositories;
        }
    }
}
