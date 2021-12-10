package com.kotik.big.chatbackend.service;

import com.kotik.big.chatbackend.dto.UserLoginForm;
import com.kotik.big.chatbackend.dto.UserRegisterForm;
import com.kotik.big.chatbackend.dto.SocketId;
import com.kotik.big.chatbackend.model.Role;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User save(UserRegisterForm registerForm) {
        return userRepository.save(toUser(registerForm));
    }

    private User toUser(UserRegisterForm registerForm) {
        User user = new User();

        user.setUsername(registerForm.getUsername());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setRole(Role.USER);

        return user;
    }

    public boolean isVacant(String login) {
        return userRepository.findByUsername(login).isEmpty();
    }

    public Optional<User> findByUsername(String login) {
        return userRepository.findByUsername(login);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean updateSocketId(SocketId socketId, Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setSocketId(socketId.getSocketId());
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    public Optional<User> findByUsernameAndPassword(UserLoginForm loginForm) {
        return userRepository.findByUsernameAndPassword(loginForm.getUsername(), passwordEncoder.encode(loginForm.getPassword()));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<String> findSocketId(Long id) {
        Optional<User> user = findById(id);
        if (user.isEmpty() || user.get().getSocketId() == null) {
            return Optional.empty();
        }
        return Optional.of(user.get().getSocketId());
    }
}
