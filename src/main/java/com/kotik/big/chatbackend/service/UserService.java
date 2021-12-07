package com.kotik.big.chatbackend.service;

import com.google.common.hash.Hashing;
import com.kotik.big.chatbackend.dto.UserLoginForm;
import com.kotik.big.chatbackend.dto.UserRegisterForm;
import com.kotik.big.chatbackend.dto.SocketId;
import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private static final String PASSWORD_SALT = "177d4b5f2e4f4edafa7404533973c04c513ac619";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User save(UserRegisterForm registerForm) {
        return userRepository.save(toUser(registerForm));
    }

    private User toUser(UserRegisterForm registerForm) {
        User user = new User();

        user.setLogin(registerForm.getLogin());
        user.setPasswordSha(getPasswordSha(registerForm.getPassword()));

        return user;
    }

    private String getPasswordSha(String password) {
        return Hashing.sha256().hashBytes((PASSWORD_SALT + password).getBytes(StandardCharsets.UTF_8)).toString();
    }

    public boolean isVacant(String login) {
        return userRepository.findByLogin(login).isEmpty();
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
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

    public Optional<User> findByLoginAndPassword(UserLoginForm loginForm) {
        return userRepository.findByLoginAndPasswordSha(loginForm.getLogin(), getPasswordSha(loginForm.getPassword()));
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
