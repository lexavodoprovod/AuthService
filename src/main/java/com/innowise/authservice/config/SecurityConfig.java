package com.innowise.authservice.config;

import com.innowise.authservice.entity.Role;
import com.innowise.authservice.filter.JwtFilter;
import com.innowise.authservice.handler.CustomAccessDeniedHandler;
import com.innowise.authservice.handler.CustomAuthenticationEntryPointHandler;
import com.innowise.authservice.handler.CustomLogoutHandler;
import com.innowise.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String BASIC_URL = "/";
    private static final String SAVE_URL = "/auth/save";
    private static final String LOGIN_URL = "/auth/login";
    private static final String REGISTRATION_URL = "/auth/registration";
    private static final String REFRESH_TOKEN_URL = "/auth/refresh_token";
    private static final String ADMIN_URL = "/admin/**";
    private static final String USER_URL = "/user/**";
    private static final String CARD_URL = "/cards/**";
    private static final String LOGOUT_URL = "/auth/logout";


    private final JwtFilter jwtFilter;

    private final UserService userService;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final CustomAuthenticationEntryPointHandler authenticationEntryPointHandler;

    private final CustomLogoutHandler customLogoutHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(LOGIN_URL,REGISTRATION_URL,REFRESH_TOKEN_URL, BASIC_URL, SAVE_URL)
                            .permitAll();
                    auth.requestMatchers(USER_URL, CARD_URL).authenticated();
                    auth.requestMatchers(ADMIN_URL).hasAuthority(Role.ADMIN.name());
                    auth.anyRequest().authenticated();
                 })

                .userDetailsService(userService)

                .exceptionHandling(e -> {
                    e.accessDeniedHandler(accessDeniedHandler);
                    e.authenticationEntryPoint(authenticationEntryPointHandler);
                })

                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(log -> {
                    log.logoutUrl(LOGOUT_URL);
                    log.addLogoutHandler(customLogoutHandler);
                    log.logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext());
                });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
