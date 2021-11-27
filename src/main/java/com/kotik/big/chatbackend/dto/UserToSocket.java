package com.kotik.big.chatbackend.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserToSocket {
    @NotNull
    private Long userId;

    @NotNull
    @NotEmpty
    private String socketId;
}
