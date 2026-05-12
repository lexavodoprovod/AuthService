package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.CardClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.exception.callbackexceptions.UserServiceException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import static com.innowise.authservice.client.ThrowFeignException.*;


@Component
public class CardClientFallBackFactory implements FallbackFactory<CardClient> {

    @Override
    public CardClient create(Throwable cause) {
        return new CardClient() {
            @Override
            public PaymentCardDto addPaymentCard(PaymentCardDto paymentCardDto) {
                throwFeignEx(cause);
                throw new UserServiceException();
            }

            @Override
            public Page<PaymentCardDto> getAllPaymentCards(String number, Pageable pageable) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public PaymentCardDto getPaymentCardById(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public PaymentCardDto updatePaymentCard(Long id, PaymentCardDto paymentCardDto) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public ResponseEntity<Void> activatePaymentCard(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();            }

            @Override
            public ResponseEntity<Void> deactivatePaymentCard(Long id) {
                throwFeignEx(cause);
                throw new UserServiceException();            }
        };
    }
}
