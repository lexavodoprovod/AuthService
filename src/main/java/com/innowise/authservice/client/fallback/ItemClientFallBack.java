package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.ItemClient;
import com.innowise.authservice.dto.request.ItemDto;
import com.innowise.authservice.exception.OrderServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemClientFallBack implements ItemClient {
    @Override
    public ItemDto addItem(ItemDto itemDto) {
        throw new OrderServiceException();
    }

    @Override
    public ItemDto getItemById(Long id) {
        throw new OrderServiceException();
    }

    @Override
    public Page<ItemDto> getAllItems(String name, Pageable pageable) {
        throw new OrderServiceException();
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        throw new OrderServiceException();
    }

    @Override
    public ResponseEntity<Void> deleteItemById(Long id) {
        throw new OrderServiceException();
    }
}
