package dev.abarmin.templater.config;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AccessConfig {

    @Bean
    public GitHub gitHub(Credentials credentials) throws Exception {
        return new GitHubBuilder()
                .withOAuthToken(credentials.getPassword(), credentials.getLogin())
                .build();
    }

    @Bean
    public CredentialsProvider credentialsProvider(Credentials credentials) {
        return new UsernamePasswordCredentialsProvider(credentials.getLogin(), credentials.getPassword());
    }
}
