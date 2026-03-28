package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.OrderClientFallBack;
import com.innowise.authservice.dto.ItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "item-client",
        url = "${ORDER_SERVICE_URL}",
        path = "/items",
        fallback = OrderClientFallBack.class)
public interface ItemClient {

    @PostMapping
    ItemDto addItem(@RequestBody ItemDto itemDto);

    @GetMapping
    Page<ItemDto> getAllItems();

    @PutMapping("/{id}")
    ItemDto updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteItem(@PathVariable Long id);
}
