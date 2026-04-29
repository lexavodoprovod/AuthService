package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.ItemClientFallBack;
import com.innowise.authservice.dto.request.ItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.innowise.authservice.constant.PaginationSettings.*;

@FeignClient(name = "order-service",
        contextId = "itemClient",
        path = "/items",
        fallback = ItemClientFallBack.class)
public interface ItemClient {

    @PostMapping
    ItemDto addItem(@RequestBody ItemDto itemDto);

    @GetMapping("/{id}")
    ItemDto getItemById(@PathVariable Long id);

    @GetMapping
    Page<ItemDto> getAllItems(
            @RequestParam(required = false) String name,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    );

    @PutMapping
    ItemDto updateItem(@RequestBody ItemDto itemDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteItemById(@PathVariable Long id);
}
