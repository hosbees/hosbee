package com.hosbee.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hosbee.admin", "com.hosbee.common"})
public class HosbeeAdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HosbeeAdminApiApplication.class, args);
    }

}
