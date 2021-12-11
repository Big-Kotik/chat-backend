package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.SocketId;
import com.kotik.big.chatbackend.dto.UserDTO;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/users")
public class UserController extends ErrorHandlingAbstractController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.of(userService.findById(id).map(UserDTO::new));
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connectToSocket(@RequestBody @Valid SocketId socketId,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            return getErrors(bindingResult);
        }
        userService.updateSocketId(socketId, user);
        return ResponseEntity.ok("Socked added");
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}/socketId")
    public ResponseEntity<String> getSocketId(@PathVariable Long id) {
        return ResponseEntity.of(userService.findSocketId(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public HttpStatus illegalArgumentHandler() {
        return HttpStatus.BAD_REQUEST;
    }
}
