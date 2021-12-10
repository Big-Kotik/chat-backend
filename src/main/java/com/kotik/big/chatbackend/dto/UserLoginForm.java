package com.kotik.big.chatbackend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserLoginForm {
    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9]+")
    @Size(min = 4, max = 16, message = "username size between 4 and 16")
    private String username;

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9#!_@]+")
    @Size(min = 8, max = 32, message = "password size between 8 and 32")
    private String password;
}
