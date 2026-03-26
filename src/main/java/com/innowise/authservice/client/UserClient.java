package com.innowise.authservice.client;

import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-client", url = "http://user-service-app:8080/users", fallback = UserClientFallBack.class)
@Component
public interface UserClient {

    int PAGINATION_SIZE = 15;
    String SORT_BY = "id";

    @PostMapping
    UserDto addUser(@Valid @RequestBody UserDto userDto);

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable Long id);

    @GetMapping("/{id}/payment-cards")
    List<PaymentCardDto> getAllPaymentCardsByUserId(@PathVariable Long id);

    @PutMapping("/{id}")
    UserDto updateUser(@PathVariable Long id,@Valid @RequestBody UserDto userDto);

    @GetMapping
    Page<UserDto> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY ) Pageable pageable);

    @PatchMapping("/{id}/activate")
    ResponseEntity<Void> activateUser(@PathVariable Long id);

    @PatchMapping("/{id}/deactivate")
    ResponseEntity<Void> deactivateUser(@PathVariable Long id);

}
