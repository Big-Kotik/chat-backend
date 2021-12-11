package com.kotik.big.chatbackend.dto;

import com.kotik.big.chatbackend.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTokenDTO {
    private UserDTO user;
    private String token;

    public UserTokenDTO(User user, String token) {
        this.user = new UserDTO(user);
        this.token = token;
    }
}
