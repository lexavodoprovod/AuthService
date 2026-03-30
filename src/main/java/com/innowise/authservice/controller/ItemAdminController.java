package com.innowise.authservice.controller;

import com.innowise.authservice.client.ItemClient;
import com.innowise.authservice.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/items",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemAdminController {

    private final ItemClient itemClient;

    /**
     * Adds a new item to the catalog.
     *
     * @param itemDto the data for the new item.
     * @return {@link ResponseEntity} containing the created {@link ItemDto}.
     */
    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemDto) {

        ItemDto itemDto1 = itemClient.addItem(itemDto);

        return ResponseEntity.ok(itemDto1);
    }

    /**
     * Retrieves all items in the catalog.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link ItemDto}.
     */
    @GetMapping
    public ResponseEntity<Page<ItemDto>> getAllItems() {
        Page<ItemDto> itemDtoPage = itemClient.getAllItems();
        return ResponseEntity.ok(itemDtoPage);
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

        ItemDto updatedItemDto = itemClient.updateItem(id, itemDto);

        return ResponseEntity.ok(updatedItemDto);
    }

    /**
     * Removes an item from the catalog.
     *
     * @param id the unique identifier of the item.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        return itemClient.deleteItem(id);
    }
}
