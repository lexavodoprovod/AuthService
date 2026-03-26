package com.innowise.authservice.service.impl;

import com.innowise.authservice.entity.User;
import com.innowise.authservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Nested
    @DisplayName("Load user by username tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should successfully return UserDetails when user exists")
        void shouldReturnUserDetailsWhenUserExists() {
            String username = "nikita_dev";
            User user = new User();
            user.setUsername(username);


            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            UserDetails result = userService.loadUserByUsername(username);

            assertNotNull(result);
            assertEquals(username, result.getUsername());
            verify(userRepository, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFound() {
            String username = "unknown_user";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userService.loadUserByUsername(username),
                    "Expected loadUserByUsername to throw, but it didn't"
            );

            assertTrue(exception.getMessage().contains(username));
            verify(userRepository, times(1)).findByUsername(username);
        }
    }

}