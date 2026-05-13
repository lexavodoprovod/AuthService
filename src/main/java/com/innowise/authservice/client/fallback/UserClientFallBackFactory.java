package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.exception.callbackexceptions.UserServiceException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import static com.innowise.authservice.client.ThrowFeignException.*;

import java.util.List;
@Component
public class UserClientFallBackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public UserDto addUser(UserDto userDto) {
                throwFeignEx(cause);
                throw new UserServiceException();
            }

            @Override
            public UserDto getUserById(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();
            }



            @Override
            public Page<PaymentCardDto> getAllPaymentCardsByUserId(Long id, String name, Pageable pageable) {
                throwFeignEx(cause);
                throw new UserServiceException();
            }

            @Override
            public List<PaymentCardDto> getAllActiveCardsByUserId(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();
            }

            @Override
            public UserDto updateUser(Long id, UserDto userDto) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public ResponseEntity<Void> activateUser(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public ResponseEntity<Void> deactivateUser(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();            }
        };
    }
}
