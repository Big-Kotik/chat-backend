package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.UserDTO;
import com.kotik.big.chatbackend.dto.UserLoginForm;
import com.kotik.big.chatbackend.dto.UserRegisterForm;
import com.kotik.big.chatbackend.dto.validator.UserLoginValidator;
import com.kotik.big.chatbackend.dto.validator.UserRegisterValidator;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.security.JwtUtil;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController extends ErrorHandlingAbstractController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRegisterValidator userRegisterValidator;
    private final UserLoginValidator userLoginValidator;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UserRegisterValidator userRegisterValidator, UserLoginValidator userLoginValidator) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRegisterValidator = userRegisterValidator;
        this.userLoginValidator = userLoginValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null) {
            if (UserRegisterForm.class.equals(binder.getTarget().getClass())) {
                binder.addValidators(userRegisterValidator);
            } else if (UserLoginForm.class.equals(binder.getTarget().getClass())) {
                binder.addValidators(userLoginValidator);
            }
        }
    }

    @PostMapping
    public ResponseEntity<?> newUser(@RequestBody @Valid UserRegisterForm registerForm,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        UserDTO userDTO = new UserDTO(userService.save(registerForm));
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginForm loginForm,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().
                header(HttpHeaders.AUTHORIZATION, jwtUtil.createToken(user.getUsername()))
                .body(new UserDTO(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDTO> logout(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        session.removeAttribute("user");
        return ResponseEntity.ok(user);
    } //TODO
}
