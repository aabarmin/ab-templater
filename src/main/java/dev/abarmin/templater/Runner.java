package dev.abarmin.templater;

import dev.abarmin.templater.generator.DependabotGenerator;
import dev.abarmin.templater.generator.WorkflowGenerator;
import dev.abarmin.templater.model.Change;
import dev.abarmin.templater.model.Repository;
import dev.abarmin.templater.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class Runner implements ApplicationRunner {
    private final RepositoryService repositoryService;
    private final DependabotGenerator dependabotGenerator;
    private final WorkflowGenerator workflowGenerator;
    private final CredentialsProvider credentialsProvider;
    private final GitHub gitHub;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        prepareWorkingDirectory();

        final Collection<Repository> repositories = repositoryService.getRepositories();
        for (Repository repository : repositories) {
            clone(repository, getWorkingDirectory(repository));
            if (requiresUpdate(repository)) {
                Change change = Change.empty();
                createBranchAndSwitch(repository, "templates-update");
                if (!dependabotMatches(repository)) {
                    change.join(createDependabotChanges(repository));
                }
                if (!workflowsMatch(repository)) {
                    change.join(createWorkflowChanges(repository));
                }
                commitChanges(repository, change);
                pushChanges(repository, "templates-update");
                createPR(repository, "templates-update");
            }
        }
    }

    private boolean requiresUpdate(Repository repository) {
        if (repository.dependabotEnabled()) {
            if (!dependabotMatches(repository)) {
                return true;
            }
        }
        return !workflowsMatch(repository);
    }

    private boolean workflowsMatch(Repository repository) {
        return repository.workflows()
                .stream()
                .allMatch(wf -> workflowMatches(repository, wf));
    }

    private Change createWorkflowChanges(Repository repository) {
        Change change = Change.empty();
        change.join(deleteExistingWorkflows(repository));
        createWorkflowsFolder(repository);
        for (String workflow : repository.workflows()) {
            change.join(createWorkflow(repository, workflow));
        }
        return change;
    }

    @SneakyThrows
    private void createWorkflowsFolder(Repository repository) {
        final Path workflowsFolder = getWorkingDirectory(repository)
                .resolve(".github")
                .resolve("workflows");
        if (!Files.exists(workflowsFolder)) {
            Files.createDirectories(workflowsFolder);
        }
    }

    @SneakyThrows
    private Change createWorkflow(Repository repository, String workflow) {
        final Path workflowFile = getWorkingDirectory(repository)
                .resolve(".github")
                .resolve("workflows")
                .resolve("build-with-" + workflow + ".yml");

        Files.createFile(workflowFile);
        Files.writeString(workflowFile, workflowGenerator.generate(repository, workflow));

        return Change.singleAdd(".github/workflows/build-with-" + workflow + ".yml");
    }

    @SneakyThrows
    private Change deleteExistingWorkflows(Repository repository) {
        final Path workflows = getWorkingDirectory(repository)
                .resolve(".github")
                .resolve("workflows");

        Change change = Change.empty();
        if (Files.exists(workflows)) {
            Files.list(workflows)
                    .map(p -> ".github/workflows/" + p.getFileName().toString())
                    .forEach(change::rm);

            FileUtils.deleteDirectory(workflows.toFile());
        }
        return change;
    }

    private boolean workflowMatches(Repository repository, String workflow) {
        return contentMatches(
                repository,
                workflowGenerator.generate(repository, workflow),
                "./github/workflows/build-with-" + workflow + ".yml"
        );
    }

    @SneakyThrows
    private void createPR(Repository repository, String targetBranch) {
        final String ghRepositoryName = getRepositoryOwner(repository) + "/" + getRepositoryName(repository);
        final GHRepository ghRepository = gitHub.getRepository(ghRepositoryName);
        ghRepository.createPullRequest(
                "Update from Templater",
                targetBranch,
                "main",
                "Automatic update of templates by Templater"
        );
    }

    @SneakyThrows
    private void pushChanges(Repository repository, String targetBranch) {
        final Git repo = Git.open(getWorkingDirectory(repository).toFile());
        boolean branchExists = repo.branchList()
                .setListMode(ListBranchCommand.ListMode.REMOTE)
                .call()
                .stream()
                .map(Ref::getName)
                .map(ref -> StringUtils.substringAfterLast(ref, "/"))
                .anyMatch(ref -> StringUtils.equals(ref, targetBranch));
        if (branchExists) {
            repo.push()
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec()
                            .setSource(null)
                            .setDestination("refs/heads/" + targetBranch))
                    .setCredentialsProvider(credentialsProvider)
                    .call();
        }
        // push changes
        repo.push()
                .setRemote("origin")
                .setRefSpecs(new RefSpec(targetBranch + ":" + targetBranch))
                .setCredentialsProvider(credentialsProvider)
                .call();
    }

    @SneakyThrows
    private void commitChanges(Repository repository, Change change) {
        final Git repo = Git.open(getWorkingDirectory(repository).toFile());
        for (String path : change.deleted()) {
            repo.rm()
                    .addFilepattern(path)
                    .call();
        }
        for (String path : change.added()) {
            repo.add()
                    .addFilepattern(path)
                    .call();
        }
        repo.commit()
                .setMessage("Update from Templater")
                .setAuthor("robot@abarmin.me", "robot@abarmin.me")
                .call();
    }

    @SneakyThrows
    private Change createDependabotChanges(Repository repository) {
        final Path githubFolder = getWorkingDirectory(repository).resolve(".github");
        if (!Files.exists(githubFolder)) {
            Files.createDirectories(githubFolder);
        }
        final Path dependabotPath = githubFolder.resolve("dependabot.yml");
        Files.deleteIfExists(dependabotPath);
        Files.createFile(dependabotPath);
        Files.writeString(dependabotPath, dependabotGenerator.generate(repository.dependabot()));
        return Change.singleAdd(".github/dependabot.yml");
    }

    @SneakyThrows
    private void createBranchAndSwitch(Repository repository, String targetBranch) {
        final Git repo = Git.open(getWorkingDirectory(repository).toFile());
        final String currentBranch = repo.getRepository().getBranch();
        // check if repo is on the main branch
        if (!StringUtils.equals(currentBranch, repository.mainBranch())) {
            repo.reset();
            repo.checkout().setName(repository.mainBranch()).call();
        }
        // check if dependabot branch exists
        final boolean exists = repo.branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .stream()
                .map(Ref::getName)
                .map(ref -> StringUtils.substringAfterLast(ref, "/"))
                .anyMatch(branch -> StringUtils.equals(branch, targetBranch));
        if (exists) {
            repo.branchDelete()
                    .setBranchNames(targetBranch)
                    .setForce(true)
                    .call();
        }
        // create a new branch and checkout to it
        repo.checkout()
                .setName(targetBranch)
                .setCreateBranch(true)
                .call();
    }

    @SneakyThrows
    private boolean contentMatches(Repository repository,
                                   String expectedContent,
                                   String expectedFile) {

        final Path expectedFilePath = getWorkingDirectory(repository)
                .resolve(expectedFile);

        if (!Files.exists(expectedFilePath)) {
            return false;
        }

        try (final InputStream stream = Files.newInputStream(expectedFilePath, StandardOpenOption.READ)) {
            final String fromFile = IOUtils.toString(stream, StandardCharsets.UTF_8);
            return StringUtils.equals(expectedContent, fromFile);
        }
    }

    @SneakyThrows
    private boolean dependabotMatches(Repository repository) {
        return contentMatches(
                repository,
                dependabotGenerator.generate(repository.dependabot()),
                ".github/dependabot.yml");
    }

    @SneakyThrows
    private void clone(Repository repository, Path workingDirectory) {
        log.info("Cloning repository {}", repository.remoteUrl());

        Git.cloneRepository()
                .setURI(repository.remoteUrl())
                .setBranch(repository.mainBranch())
                .setDirectory(workingDirectory.toFile())
                .call();

        log.info("Repository cloned");
    }

    @SneakyThrows
    private Path getWorkingDirectory(Repository repository) {
        final Path targetDirectory = Path.of("./working").resolve(getRepositoryName(repository));
        Files.createDirectories(targetDirectory);

        return targetDirectory;
    }

    private String getRepositoryName(Repository repository) {
        String repositoryName = StringUtils.substringAfterLast(repository.remoteUrl(), "/");
        repositoryName = StringUtils.remove(repositoryName, ".git");
        return repositoryName;
    }

    private String getRepositoryOwner(Repository repository) {
        String repositoryOwner = StringUtils.substringBeforeLast(repository.remoteUrl(), "/");
        repositoryOwner = StringUtils.substringAfterLast(repositoryOwner, "/");
        return repositoryOwner;
    }

    @SneakyThrows
    private void prepareWorkingDirectory() {
        final Path workingDirectory = Path.of("./working");
        if (Files.exists(workingDirectory)) {
            log.info("Working directory exists, cleaning");
            FileUtils.deleteDirectory(workingDirectory.toFile());
        }
        Files.createDirectories(workingDirectory);
    }
}
