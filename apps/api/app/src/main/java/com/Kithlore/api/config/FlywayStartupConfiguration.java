package com.Kithlore.api.config;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class FlywayStartupConfiguration {

  @Bean
  FlywayMigrationStrategy flywayMigrationStrategy(KithloreProperties properties) {
    return flyway -> flyway.migrate();
  }
}
