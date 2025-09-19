package com.hosbee.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EntityScan("com.hosbee.common.entity")
@EnableJpaAuditing
public class JpaConfig {
}