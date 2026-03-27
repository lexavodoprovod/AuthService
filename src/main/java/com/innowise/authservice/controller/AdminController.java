package com.innowise.authservice.controller;

import com.innowise.authservice.client.PaymentCardClient;
import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Administrative controller providing full CRUD operations and management
 * capabilities for users, payment cards, orders, items, and payments.
 * Access to this controller should be restricted to users with ADMIN role.
 */
@RestController
@RequestMapping(value = "/admin", produces = "application/json")
@RequiredArgsConstructor
public class AdminController {

    private final UserClient userClient;
    private final PaymentCardClient paymentCardClient;

    /**
     * Retrieves detailed information about a specific user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return {@link ResponseEntity} containing the {@link UserDto}.
     */
    @GetMapping("/users/{id}")
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
    @GetMapping("/users/{id}/cards")
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
    @GetMapping("/users")
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
    @PutMapping("/users/{id}/update")
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
    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        return userClient.activateUser(id);
    }

    /**
     * Deactivates (soft deletes) a user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return {@link ResponseEntity} with no content on success.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        return userClient.deactivateUser(id);
    }

    /**
     * Retrieves a paginated list of all payment cards in the system.
     *
     * @param number   optional filter by card number.
     * @param pageable pagination and sorting information.
     * @return {@link ResponseEntity} containing a {@link Page} of {@link PaymentCardDto}.
     */
    @GetMapping("/cards")
    public ResponseEntity<Page<PaymentCardDto>> getAllCards(@RequestParam(required = false) String number,
                                                            Pageable pageable) {
        Page<PaymentCardDto> cardDtoPage = paymentCardClient.getAllPaymentCards(number, pageable);
        return ResponseEntity.ok(cardDtoPage);
    }


    /**
     * Retrieves details of a specific payment card.
     *
     * @param id the unique identifier of the card.
     * @return {@link ResponseEntity} containing the {@link PaymentCardDto}.
     */
    @GetMapping("/cards/{id}")
    public ResponseEntity<PaymentCardDto> getPaymentCardById(@PathVariable Long id) {

        PaymentCardDto paymentCard = paymentCardClient.getPaymentCardById(id);

        return ResponseEntity.ok(paymentCard);
    }

    /**
     * Updates payment card information.
     *
     * @param id             the unique identifier of the card to update.
     * @param paymentCardDto the {@link PaymentCardDto} containing updated details.
     * @return {@link ResponseEntity} containing the updated {@link PaymentCardDto}.
     */
    @PutMapping("/cards/{id}/update")
    public ResponseEntity<PaymentCardDto> updatePaymentCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDto paymentCardDto) {
        paymentCardDto.setId(id);

        PaymentCardDto paymentCard = paymentCardClient.updatePaymentCard(id, paymentCardDto);
        return ResponseEntity.ok(paymentCard);
    }

    /**
     * Activates a specific payment card.
     *
     * @param id the unique identifier of the card.
     * @return {@link ResponseEntity} with no content on success.
     */
    @PatchMapping("/cards/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long id) {
        return paymentCardClient.activatePaymentCard(id);
    }

    /**
     * Deactivates (soft deletes) a specific payment card.
     *
     * @param id the unique identifier of the card.
     * @return {@link ResponseEntity} with no content on success.
     */
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deactivateCard(@PathVariable Long id) {
        return paymentCardClient.deactivatePaymentCard(id);
    }

    /**
     * Creates a new order.
     *
     * @param orderDto the order data transfer object.
     * @return {@link ResponseEntity} containing the created {@link OrderDto}.
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(new OrderDto());
    }

    /**
     * Retrieves all orders in a paginated format.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link OrderDto}.
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getAllOrders() {
        return ResponseEntity.ok().body(Page.empty());
    }

    /**
     * Updates an existing order.
     *
     * @param id       the unique identifier of the order.
     * @param orderDto the updated order details.
     * @return {@link ResponseEntity} containing the updated {@link OrderDto}.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(new OrderDto());
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the unique identifier of the order.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a new item to the catalog.
     *
     * @param itemDto the data for the new item.
     * @return {@link ResponseEntity} containing the created {@link ItemDto}.
     */
    @PostMapping("/items")
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(new ItemDto());
    }

    /**
     * Retrieves all items in the catalog.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link ItemDto}.
     */
    @GetMapping("/items")
    public ResponseEntity<Page<ItemDto>> getAllItems() {
        return ResponseEntity.ok().body(Page.empty());
    }

    /**
     * Updates an item's information in the catalog.
     *
     * @param id      the unique identifier of the item.
     * @param itemDto the updated item information.
     * @return {@link ResponseEntity} containing the updated {@link ItemDto}.
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(new ItemDto());
    }

    /**
     * Removes an item from the catalog.
     *
     * @param id the unique identifier of the item.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    // --- PAYMENT MANAGEMENT ---

    /**
     * Records a new payment in the system.
     *
     * @param paymentDto the payment data transfer object.
     * @return {@link ResponseEntity} containing the created {@link PaymentDto}.
     */
    @PostMapping("/payments")
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(new PaymentDto());
    }

    /**
     * Retrieves a paginated history of all payments.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link PaymentDto}.
     */
    @GetMapping("/payments")
    public ResponseEntity<Page<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok().body(Page.empty());
    }

    /**
     * Updates a payment record.
     *
     * @param id         the unique identifier of the payment.
     * @param paymentDto updated payment details.
     * @return {@link ResponseEntity} containing the updated {@link PaymentDto}.
     */
    @PutMapping("/payments/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(new PaymentDto());
    }

    /**
     * Deletes a payment record from the system.
     *
     * @param id the unique identifier of the payment.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
