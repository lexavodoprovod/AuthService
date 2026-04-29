package com.innowise.authservice.controller.admin;

import com.innowise.authservice.client.ItemClient;
import com.innowise.authservice.dto.request.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.innowise.authservice.constant.PaginationSettings.PAGINATION_SIZE;
import static com.innowise.authservice.constant.PaginationSettings.SORT_BY;

@RestController
@RequestMapping(value = "/admin/items",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemAdminController {

    private final ItemClient itemClient;

    /**
     * Adds a new item to the catalog.
     *
     * @param itemRequestDto the data for the new item.
     * @return {@link ResponseEntity} containing the created {@link ItemDto}.
     */
    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemRequestDto) {

        ItemDto itemDto = itemClient.addItem(itemRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id){

        ItemDto itemDto = itemClient.getItemById(id);

        return ResponseEntity.ok(itemDto);
    };

    /**
     * Retrieves all items in the catalog.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link ItemDto}.
     */
    @GetMapping
    public ResponseEntity<Page<ItemDto>> getAllItems(
            @RequestParam(required = false) String name,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    ) {

        Page<ItemDto> itemDtoPage = itemClient.getAllItems(name, pageable);

        return ResponseEntity.ok(itemDtoPage);
    }

    /**
     * Updates an item's information in the catalog.
     *
     * @param itemRequestDto the updated item information.
     * @return {@link ResponseEntity} containing the updated {@link ItemDto}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemRequestDto) {

        ItemDto itemDto = itemClient.updateItem(itemRequestDto);

        return ResponseEntity.ok(itemDto);
    }

    /**
     * Removes an item from the catalog.
     *
     * @param id the unique identifier of the item.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        return itemClient.deleteItemById( id);
    }
}
