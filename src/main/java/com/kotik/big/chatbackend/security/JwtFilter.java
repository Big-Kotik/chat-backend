package com.kotik.big.chatbackend.security;

import com.kotik.big.chatbackend.model.User;
import com.kotik.big.chatbackend.security.exception.JwtAuthenticationException;
import com.kotik.big.chatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

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
        UserDetails userDetails;
        Optional<User> user = userService.findByLogin(login);
        if (user.isPresent()) {
            userDetails = new UserDetailsImpl(user.get());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities()));
        }
    }
}
