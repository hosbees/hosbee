package com.hosbee.user.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.hosbee.common.entity")
@EnableJpaRepositories(basePackages = {
    "com.hosbee.common.repository"
})
@EnableJpaAuditing
public class JpaConfig {
}