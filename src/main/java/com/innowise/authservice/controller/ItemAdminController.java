package com.innowise.authservice.controller;

import com.innowise.authservice.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/items", produces = "application/json")
@RequiredArgsConstructor
public class ItemAdminController {

    /**
     * Adds a new item to the catalog.
     *
     * @param itemDto the data for the new item.
     * @return {@link ResponseEntity} containing the created {@link ItemDto}.
     */
    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(new ItemDto());
    }

    /**
     * Retrieves all items in the catalog.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link ItemDto}.
     */
    @GetMapping
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
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(new ItemDto());
    }

    /**
     * Removes an item from the catalog.
     *
     * @param id the unique identifier of the item.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
