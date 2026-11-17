package com.Kithlore.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kithlore.security")
public record KithloreProperties(@NotBlank @Size(min = 32) String jwtSecret) {}
