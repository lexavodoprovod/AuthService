package com.innowise.authservice.handler;

import com.innowise.authservice.entity.Token;
import com.innowise.authservice.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import static com.innowise.authservice.constant.TokenInfo.*;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        String authHeader = request.getHeader(JWT_HEADER_NAME);

        if (authHeader == null || !authHeader.startsWith(JWT_HEADER_PREFIX)) {
            return;
        }

        String token = authHeader.substring(JWT_HEADER_PREFIX.length());

        Token tokenEntity = tokenRepository.findTokenByAccessToken(token)
                .orElse(null);

        if (tokenEntity != null) {
            tokenEntity.setLoggedOut(true);
            tokenRepository.save(tokenEntity);
        }
    }
}
