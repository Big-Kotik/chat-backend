package com.kotik.big.chatbackend.dto.validator;

import com.kotik.big.chatbackend.dto.UserLoginForm;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UserLoginValidator implements Validator {
    private final UserService userService;

    public UserLoginValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean supports(Class<?> clazz) {
        return UserLoginForm.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserLoginForm userLoginForm = (UserLoginForm) target;
            Optional<User> user = userService.findByLoginAndPassword(userLoginForm);
            if (user.isEmpty()) {
                errors.rejectValue("password", "password.is-invalid", "User not found");
            }
        }
    }
}
