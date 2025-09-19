package com.hosbee.adminweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.hosbee.adminweb", "com.hosbee.common"})
@EnableJpaRepositories(basePackages = {"com.hosbee.common.repository"})
public class HosbeeAdminUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HosbeeAdminUiApplication.class, args);
    }

}
