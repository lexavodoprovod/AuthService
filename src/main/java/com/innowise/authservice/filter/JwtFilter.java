package com.innowise.authservice.filter;

import com.innowise.authservice.service.JwtService;
import com.innowise.authservice.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.innowise.authservice.constant.TokenInfo.*;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserService userService;

    private final HandlerExceptionResolver resolver;

    public JwtFilter(JwtService jwtService, UserService userService, @Qualifier("handlerExceptionResolver")HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(JWT_HEADER_NAME);

        if(authHeader == null || !authHeader.startsWith(JWT_HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(JWT_HEADER_PREFIX.length());

            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userService.loadUserByUsername(username);

                boolean isTokenValid = jwtService.isValidAccessToken(token, userDetails);

                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        }catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}
