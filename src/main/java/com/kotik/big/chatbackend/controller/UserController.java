package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.*;
import com.kotik.big.chatbackend.dto.validator.UserLoginValidator;
import com.kotik.big.chatbackend.dto.validator.UserRegisterValidator;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.security.JwtUtil;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final UserRegisterValidator userRegisterValidator;
    private final UserLoginValidator userLoginValidator;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService,
                          UserRegisterValidator userRegisterValidator,
                          UserLoginValidator userLoginValidator, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRegisterValidator = userRegisterValidator;
        this.userLoginValidator = userLoginValidator;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

    private ResponseEntity<?> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(bindingResult.getFieldErrors().stream().map(MyFieldError::new));
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
                header("Authorization", jwtUtil.createToken(user.getUsername()))
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
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.of(userService.findById(id).map(UserDTO::new));
    }

    @PostMapping("/connect")
    public ResponseEntity<String> connectToSocket(@RequestBody @Valid SocketId socketId,
                                                  BindingResult bindingResult,
                                                  HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (bindingResult.hasErrors() || userDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.updateSocketId(socketId, userDTO.getId()) ?
                ResponseEntity.ok("Socket added") :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}/socketId")
    public ResponseEntity<String> getSocketId(@PathVariable Long id,
                                              HttpSession session) {
        return ResponseEntity.of(userService.findSocketId(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public HttpStatus illegalArgumentHandler() {
        return HttpStatus.BAD_REQUEST;
    }
}
