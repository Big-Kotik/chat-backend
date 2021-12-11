package com.kotik.big.chatbackend.security;

import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.security.exception.JwtAuthenticationException;
import com.kotik.big.chatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String login;
        try {
            login = jwtUtil.getUsername(jwtUtil.getToken(request));
        } catch (JwtAuthenticationException e) {
            filterChain.doFilter(request, response);
            return;
        }
        Optional<User> user = userService.findByUsername(login);
        user.ifPresent(value -> SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(value,
                null, value.getAuthorities())));
        filterChain.doFilter(request, response);
    }
}
