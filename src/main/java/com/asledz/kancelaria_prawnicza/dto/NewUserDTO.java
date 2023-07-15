package com.asledz.kancelaria_prawnicza.dto;

import lombok.Builder;

@Builder
public record NewUserDTO(String name,
                         String surname,
                         String email,
                         String password) {
}
