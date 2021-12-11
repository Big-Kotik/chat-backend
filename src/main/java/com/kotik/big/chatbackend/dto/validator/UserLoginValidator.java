package com.kotik.big.chatbackend.dto.validator;

import com.kotik.big.chatbackend.dto.UserLoginForm;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UserLoginValidator implements Validator {
    private static final String INCORRECT_CREDENTIALS_MESSAGE = "Incorrect username or password";
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserLoginValidator(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean supports(Class<?> clazz) {
        return UserLoginForm.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserLoginForm userLoginForm = (UserLoginForm) target;
            Optional<User> user = userService.findByUsername(userLoginForm.getUsername());
            if (user.isEmpty() || !passwordEncoder.matches(userLoginForm.getPassword(), user.get().getPassword())) {
                errors.rejectValue("password", "password.is-invalid",
                        INCORRECT_CREDENTIALS_MESSAGE);
                errors.rejectValue("username", "username.is-invalid",
                        INCORRECT_CREDENTIALS_MESSAGE);
            }
        }
    }
}
