package com.innowise.authservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service interface for managing user-specific data.
 * Extends {@link UserDetailsService} to integrate with Spring Security
 * for authentication purposes.
 */
public interface UserService extends UserDetailsService {

    /**
     * Locates the user based on the username.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never {@code null}).
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
