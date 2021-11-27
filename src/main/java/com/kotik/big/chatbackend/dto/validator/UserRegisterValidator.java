package com.kotik.big.chatbackend.dto.validator;

import com.kotik.big.chatbackend.dto.UserRegisterForm;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserRegisterValidator implements Validator {
    private final UserService userService;

    public UserRegisterValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean supports(Class<?> clazz) {
        return UserRegisterForm.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserRegisterForm userRegisterForm = (UserRegisterForm) target;
            if (!userService.isVacant(userRegisterForm.getLogin())) {
                errors.rejectValue("login", "login.is-in-use", "login is in use already");
            } else if (!userRegisterForm.getPassword().equals(userRegisterForm.getPasswordConformation())) {
                errors.rejectValue("password", "password.not-confirms",
                        "password and passwordSha not equals");
            }
        }
    }
}
