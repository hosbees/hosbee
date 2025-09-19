package com.hosbee.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hosbee.user", "com.hosbee.common"})
public class HosbeeUserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HosbeeUserApiApplication.class, args);
    }

}
