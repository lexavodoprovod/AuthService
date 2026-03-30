package com.innowise.authservice.controller;

import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderClient orderClient;

    /**
     * Creates a new order.
     *
     * @param orderDto the order data transfer object.
     * @return {@link ResponseEntity} containing the created {@link OrderDto}.
     */
    @PostMapping
    public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {

        OrderDto addedOrderDto = orderClient.addOrder(orderDto);

        return ResponseEntity.ok(addedOrderDto);
    }

    /**
     * Retrieves all orders in a paginated format.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link OrderDto}.
     */
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders() {

        Page<OrderDto> orderDtoPage = orderClient.getAllOrders();

        return ResponseEntity.ok(orderDtoPage);
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

        OrderDto updatedOrderDto = orderClient.updateOrder(id, orderDto);

        return ResponseEntity.ok(updatedOrderDto);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the unique identifier of the order.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return orderClient.deleteOrder(id);
    }
}
