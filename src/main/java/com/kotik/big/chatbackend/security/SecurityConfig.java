package com.kotik.big.chatbackend.security;


import com.kotik.big.chatbackend.service.UserService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

/*    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> new UserDetailsImpl(
                userService
                        .findByLogin(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(String.format("Username %s not found", username)))
        ));
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }
}
