package com.innowise.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
