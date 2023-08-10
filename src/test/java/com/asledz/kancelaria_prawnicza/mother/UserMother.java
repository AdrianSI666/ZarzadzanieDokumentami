package com.asledz.kancelaria_prawnicza.mother;

import com.asledz.kancelaria_prawnicza.domain.User;

import java.util.ArrayList;

public class UserMother {
    public static User.UserBuilder basic() {
        return User.builder()
                .name("Jan")
                .surname("Doe")
                .email("jane.doe@example.org")
                .password("iloveyou")
                .roles(new ArrayList<>())
                .documents(new ArrayList<>());
    }

    public static User.UserBuilder basic(Long id) {
        return User.builder()
                .id(id)
                .name("Jan")
                .surname("Doe")
                .email("jane.doe@example.org")
                .password("iloveyou")
                .roles(new ArrayList<>())
                .documents(new ArrayList<>());
    }
}
