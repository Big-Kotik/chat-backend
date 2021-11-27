package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.UserDTO;
import com.kotik.big.chatbackend.dto.UserRegisterForm;
import com.kotik.big.chatbackend.dto.UserToSocket;
import com.kotik.big.chatbackend.dto.validator.UserRegisterValidator;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserRegisterValidator userRegisterValidator;

    public UserController(UserService userService,
                          UserRegisterValidator userRegisterValidator) {
        this.userService = userService;
        this.userRegisterValidator = userRegisterValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && UserRegisterForm.class.equals(binder.getTarget().getClass())) {
            binder.addValidators(userRegisterValidator);
        }
    }

    @PostMapping
    public ResponseEntity<UserDTO> newUser(@RequestBody @Valid UserRegisterForm registerForm,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().header("errors", Stream.of(bindingResult.getAllErrors()).map(Object::toString).collect(Collectors.joining("\n"))).build();
        }
        return ResponseEntity.ok().body(new UserDTO(userService.save(registerForm)));
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
