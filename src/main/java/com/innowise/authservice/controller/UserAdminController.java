package com.innowise.authservice.controller;

import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserAdminController {

    private final UserClient userClient;

    /**
     * Retrieves detailed information about a specific user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return {@link ResponseEntity} containing the {@link UserDto}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {

        UserDto userDto = userClient.getUserById(id);

        return ResponseEntity.ok(userDto);
    }

    /**
     * Retrieves all payment cards belonging to a specific user.
     *
     * @param id the unique identifier of the user.
     * @return {@link ResponseEntity} containing a list of {@link PaymentCardDto}.
     */
    @GetMapping("/{id}/cards")
    public ResponseEntity<List<PaymentCardDto>> getMyCards(@PathVariable Long id) {
        List<PaymentCardDto> cards = userClient.getAllPaymentCardsByUserId(id);
        return ResponseEntity.ok(cards);
    }

    /**
     * Retrieves a paginated list of all users, optionally filtered by name or surname.
     *
     * @param name     optional filter by user's first name.
     * @param surname  optional filter by user's last name.
     * @param pageable pagination and sorting information.
     * @return {@link ResponseEntity} containing a {@link Page} of {@link UserDto}.
     */
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String surname,
                                                     Pageable pageable) {
        Page<UserDto> userDtoPage = userClient.getAllUsers(name, surname, pageable);
       return ResponseEntity.ok(userDtoPage);
    }

    /**
     * Updates user information by their ID.
     *
     * @param id      the unique identifier of the user to update.
     * @param userDto the data transfer object containing updated information.
     * @return {@link ResponseEntity} containing the updated {@link UserDto}.
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedUserDto = userClient.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    /**
     * Activates a previously deactivated or blocked user.
     *
     * @param id the unique identifier of the user.
     * @return {@link ResponseEntity} with no content on success.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        return userClient.activateUser(id);
    }

    /**
     * Deactivates (soft deletes) a user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return {@link ResponseEntity} with no content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        return userClient.deactivateUser(id);
    }
}
