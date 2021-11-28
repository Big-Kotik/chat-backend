package com.kotik.big.chatbackend.dto;

import com.kotik.big.chatbackend.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String login;
    private Date creationTime;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.creationTime = user.getCreationTime();
    }
}
