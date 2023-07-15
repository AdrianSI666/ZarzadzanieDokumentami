package com.asledz.kancelaria_prawnicza.dto;

import com.asledz.kancelaria_prawnicza.domain.Role;

import java.util.Collection;

public record UserAuthorities(String email,
                              Collection<Role> roles) {
}
