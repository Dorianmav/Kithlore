package com.Kithlore.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.Kithlore.api.config.KithloreProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

class ConfigurationValidationTests {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
          .withUserConfiguration(RequiredProperties.class);

  @Test
  void missingJwtSecretFailsStartupWithPropertyName() {
    contextRunner
        .withPropertyValues("kithlore.security.jwt-secret=")
        .run(
            context -> {
              assertThat(context).hasFailed();
              assertThat(context.getStartupFailure())
                  .hasStackTraceContaining("kithlore.security.jwt-secret");
            });
  }

  @EnableConfigurationProperties(KithloreProperties.class)
  static class RequiredProperties {}
}
