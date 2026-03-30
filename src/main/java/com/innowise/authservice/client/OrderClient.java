package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.OrderClientFallBack;
import com.innowise.authservice.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "order-client",
        url = "${ORDER_SERVICE_URL}",
        path = "/orders",
        fallback = OrderClientFallBack.class)
public interface OrderClient {

    @PostMapping
    OrderDto addOrder(@RequestBody OrderDto orderDto);

    @GetMapping
    Page<OrderDto> getAllOrders();

    @PutMapping("/{id}")
    OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteOrder(@PathVariable Long id);
}
