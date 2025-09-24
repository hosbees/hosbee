package com.hosbee.adminweb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {HosbeeAdminUiApplication.class})
@org.springframework.test.context.ActiveProfiles("test")
class HosbeeAdminUiApplicationTests {

    @Test
    void contextLoads() {
    }

}
