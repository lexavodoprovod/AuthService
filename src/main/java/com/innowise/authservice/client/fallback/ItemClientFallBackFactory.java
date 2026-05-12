package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.ItemClient;
import com.innowise.authservice.dto.request.ItemDto;
import com.innowise.authservice.exception.callbackexceptions.OrderServiceException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import static com.innowise.authservice.client.ThrowFeignException.*;

@Component
public class ItemClientFallBackFactory implements FallbackFactory<ItemClient> {
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            @Override
            public ItemDto addItem(ItemDto itemDto) {
                throwFeignEx(cause);
                throw new OrderServiceException();
            }

            @Override
            public ItemDto getItemById(Long id) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public Page<ItemDto> getAllItems(String name, Pageable pageable) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public ItemDto updateItem(ItemDto itemDto) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public ResponseEntity<Void> deleteItemById(Long id) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }
        };
    }
}
