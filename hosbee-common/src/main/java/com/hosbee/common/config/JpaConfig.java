package com.hosbee.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// JpaConfig는 각 모듈별로 개별 설정하므로 common에서는 제거
// 각 API 모듈(admin-api, user-api)에서 자체 JpaConfig를 사용