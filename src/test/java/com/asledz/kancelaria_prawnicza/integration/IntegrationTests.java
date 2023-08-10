package com.asledz.kancelaria_prawnicza.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.jpa.database=h2"
})
public class IntegrationTests {

}
