package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.UserDTO;
import com.kotik.big.chatbackend.dto.UserLoginForm;
import com.kotik.big.chatbackend.dto.UserRegisterForm;
import com.kotik.big.chatbackend.dto.UserToSocket;
import com.kotik.big.chatbackend.dto.validator.UserLoginValidator;
import com.kotik.big.chatbackend.dto.validator.UserRegisterValidator;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.service.UserService;
import com.sun.source.tree.OpensTree;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/users")
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
    public ResponseEntity<UserDTO> newUser(@RequestBody @Valid UserRegisterForm registerForm,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        return ResponseEntity.ok().body(new UserDTO(userService.save(registerForm)));
    }

    private ResponseEntity<UserDTO> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest().header("errors",
                        Stream.of(bindingResult.getAllErrors())
                                .map(Object::toString)
                                .collect(Collectors.joining("\n")))
                .build();
    }

    @PostMapping
    public ResponseEntity<UserDTO> login(@RequestBody @Valid UserLoginForm loginForm,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        Optional<User> user = userService.findByLoginAndPassword(loginForm);
        return user.map(value -> ResponseEntity.ok().body(new UserDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id).map(value -> ResponseEntity.ok().body(new UserDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/connect")
    public HttpStatus connectToSocket(@RequestBody @Valid UserToSocket userToSocket,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return HttpStatus.BAD_REQUEST;
        }
        return userService.updateSocketId(userToSocket) ? HttpStatus.OK : HttpStatus.NOT_FOUND;
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}/socketId")
    public ResponseEntity<String> getSocketId(@PathVariable Long id) {
        return userService.findSocketId(id).map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
