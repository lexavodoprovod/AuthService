package com.innowise.authservice.client;

import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.exception.UserServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UserClientFallBack implements UserClient, PaymentCardClient{

    @Override
    public UserDto getUserById(Long id) {
        throw new UserServiceException();
    }

    @Override
    public ResponseEntity<Void> activateUser(Long id) {
        throw new UserServiceException();
    }

    @Override
    public ResponseEntity<Void> deactivateUser(Long id) {
        throw new UserServiceException();
    }

    @Override
    public List<PaymentCardDto> getAllPaymentCardsByUserId(Long id) {
        throw new UserServiceException();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        throw new UserServiceException();
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        throw new UserServiceException();
    }
    @Override
    public Page<UserDto> getAllUsers(String name, String surname, Pageable pageable) {
        throw new UserServiceException();
    }

    @Override
    public Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable) {
        throw new UserServiceException();
    }

    @Override
    public PaymentCardDto getPaymentCardById(Long id) {
        throw new UserServiceException();
    }

    @Override
    public PaymentCardDto updatePaymentCard(Long id, PaymentCardDto paymentCardDto) {
        throw new UserServiceException();
    }

    @Override
    public ResponseEntity<Void> activatePaymentCard(Long id) {
        throw new UserServiceException();
    }

    @Override
    public ResponseEntity<Void> deactivatePaymentCard(Long id) {
        throw new UserServiceException();
    }
}
