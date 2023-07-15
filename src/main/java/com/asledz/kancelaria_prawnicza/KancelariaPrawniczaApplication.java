package com.asledz.kancelaria_prawnicza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.Clock;

@SpringBootApplication
public class KancelariaPrawniczaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KancelariaPrawniczaApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        int strength = 10;
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }
    //Command runner to add user/role
    /*@Bean
    CommandLineRunner runner(UserService userService,
                             RoleService roleService) {
        return args -> {
            userService.addUser(NewUserDTO.builder()
                            .name("test")
                            .surname("test")
                            .email("test@test.pkp")
                            .password("testPassword")
                            .build());
        };
    }*/
}
