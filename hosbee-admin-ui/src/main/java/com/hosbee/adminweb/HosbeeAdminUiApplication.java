package com.hosbee.adminweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hosbee.adminweb"})
public class HosbeeAdminUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HosbeeAdminUiApplication.class, args);
    }

}
