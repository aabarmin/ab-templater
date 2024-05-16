package dev.abarmin.templater.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("git.credentials")
public class Credentials {
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;
}
