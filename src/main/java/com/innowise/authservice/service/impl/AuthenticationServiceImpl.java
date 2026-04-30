package com.innowise.authservice.service.impl;

import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.AuthenticationResponseDto;
import com.innowise.authservice.dto.LoginDto;
import com.innowise.authservice.dto.RegistrationDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.Token;
import com.innowise.authservice.entity.User;
import com.innowise.authservice.exception.ExistUserException;
import com.innowise.authservice.exception.NullParameterException;
import com.innowise.authservice.exception.UserNotFoundException;
import com.innowise.authservice.repository.TokenRepository;
import com.innowise.authservice.repository.UserRepository;
import com.innowise.authservice.service.AuthenticationService;
import com.innowise.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.innowise.authservice.constant.TokenInfo.*;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String USERNAME_NOT_FOUND_MESSAGE = "No user found";

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;

    private final UserClient userClient;

    @Override
    public Long save(RegistrationDto registrationDto) {

        if(registrationDto == null){
            throw new NullParameterException();
        }

        String username = registrationDto.getUsername();

        if(userRepository.existsByUsername(username)){
            throw new ExistUserException(username);
        }

        User user = User.builder()
                .id(registrationDto.getId())
                .username(username)
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    @Override
    @Transactional
    public Long register(RegistrationDto registrationDto) {

        if(registrationDto == null){
            throw new NullParameterException();
        }

        String username = registrationDto.getUsername();

        if(userRepository.existsByUsername(username)){
            throw new ExistUserException(username);
        }

        UserDto userDto = UserDto.builder()
                .id(registrationDto.getId())
                .name(registrationDto.getName())
                .surname(registrationDto.getSurname())
                .email(registrationDto.getEmail())
                .birthDate(registrationDto.getBirthDate())
                .build();

        UserDto userResponseDto = userClient.addUser(userDto);

        User user = User.builder()
                .id(userResponseDto.getId())
                .username(username)
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .build();

        userRepository.save(user);

        return user.getId();

    }

    @Override
    @Transactional
    public AuthenticationResponseDto authenticate(LoginDto loginDto) {

        if(loginDto == null){
            throw new NullParameterException();
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        String username = loginDto.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllToken(user);
        saveUserToken(accessToken,  refreshToken, user);


        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JWT_HEADER_NAME);

        if (authorizationHeader == null || !authorizationHeader.startsWith(JWT_HEADER_PREFIX)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(JWT_HEADER_PREFIX.length());
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_MESSAGE));

        if (!jwtService.isValidRefreshToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);

    }

    private void revokeAllToken(User user) {

        Long userId = user.getId();

        List<Token> validTokens = tokenRepository.findAllAccessTokensByUserId(userId);

        if(!validTokens.isEmpty()){
            validTokens.forEach(t -> t.setLoggedOut(true));
        }

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }
}
