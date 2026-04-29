package com.innowise.authservice.controller.admin;

import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.request.OrderRequestDto;
import com.innowise.authservice.dto.response.OrderResponseDto;
import com.innowise.authservice.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.innowise.authservice.constant.PaginationSettings.PAGINATION_SIZE;
import static com.innowise.authservice.constant.PaginationSettings.SORT_BY;

@RestController
@RequestMapping(value = "/admin/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderClient orderClient;

    /**
     * Creates a new order.
     *
     * @param orderDto the order data transfer object.
     * @return {@link ResponseEntity} containing the created {@link OrderRequestDto}.
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> addOrder(@RequestBody OrderRequestDto orderDto) {

        OrderResponseDto orderResponseDto = orderClient.addOrder(orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id){

        OrderResponseDto orderResponseDto = orderClient.getOrderById(id);

        return ResponseEntity.ok(orderResponseDto);
    };

    /**
     * Retrieves all orders in a paginated format.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link OrderRequestDto}.
     */
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) List<OrderStatus> statuses,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    ) {

        Page<OrderResponseDto> orderResponseDtoPage = orderClient.getAllOrders(from, to, statuses, pageable);

        return ResponseEntity.ok(orderResponseDtoPage);
    }

    /**
     * Updates an existing order.
     *
     * @param id       the unique identifier of the order.
     * @param orderDto the updated order details.
     * @return {@link ResponseEntity} containing the updated {@link OrderRequestDto}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderRequestDto orderDto) {

        OrderResponseDto orderResponseDto = orderClient.updateOrder(id, orderDto);

        return ResponseEntity.ok(orderResponseDto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatus status){

        OrderResponseDto orderResponseDto = orderClient.updateStatus(id, status);

        return ResponseEntity.ok(orderResponseDto);
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
