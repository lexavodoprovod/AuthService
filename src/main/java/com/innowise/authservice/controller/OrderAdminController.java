package com.innowise.authservice.controller;

import com.innowise.authservice.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/orders", produces = "application/json")
@RequiredArgsConstructor
public class OrderAdminController {

    /**
     * Creates a new order.
     *
     * @param orderDto the order data transfer object.
     * @return {@link ResponseEntity} containing the created {@link OrderDto}.
     */
    @PostMapping
    public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(new OrderDto());
    }

    /**
     * Retrieves all orders in a paginated format.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link OrderDto}.
     */
    @GetMapping
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
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(new OrderDto());
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the unique identifier of the order.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
