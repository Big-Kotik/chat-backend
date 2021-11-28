package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.*;
import com.kotik.big.chatbackend.dto.validator.UserLoginValidator;
import com.kotik.big.chatbackend.dto.validator.UserRegisterValidator;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final UserRegisterValidator userRegisterValidator;
    private final UserLoginValidator userLoginValidator;

    public UserController(UserService userService,
                          UserRegisterValidator userRegisterValidator,
                          UserLoginValidator userLoginValidator) {
        this.userService = userService;
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
                                           BindingResult bindingResult,
                                           HttpSession session) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        UserDTO userDTO = new UserDTO(userService.save(registerForm));
        session.setAttribute("user", userDTO);
        return ResponseEntity.ok(userDTO);
    }

    private ResponseEntity<?> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(bindingResult.getFieldErrors().stream().map(MyFieldError::new));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginForm loginForm,
                                         BindingResult bindingResult,
                                         HttpSession session) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        Optional<User> user = userService.findByLoginAndPassword(loginForm);
        user.ifPresent(value -> session.setAttribute("user", new UserDTO(value)));
        return ResponseEntity.of(user.map(UserDTO::new));

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

    @GetMapping("/current")
    public ResponseEntity<UserDTO> current(HttpSession session) {
        return ResponseEntity.of(Optional.ofNullable((UserDTO) session.getAttribute("user")));
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
        if (session.getAttribute("user") == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.of(userService.findSocketId(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public HttpStatus illegalArgumentHandler() {
        return HttpStatus.BAD_REQUEST;
    }
}
