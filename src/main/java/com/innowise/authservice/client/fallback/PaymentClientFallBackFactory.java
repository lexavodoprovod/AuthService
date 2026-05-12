package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.PaymentClient;
import com.innowise.authservice.dto.request.PaymentRequestDto;
import com.innowise.authservice.dto.response.PaymentResponseDto;
import com.innowise.authservice.entity.PaymentStatus;
import com.innowise.authservice.exception.callbackexceptions.PaymentServiceException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import static com.innowise.authservice.client.ThrowFeignException.*;
@Component
public class PaymentClientFallBackFactory implements FallbackFactory<PaymentClient> {

    @Override
    public PaymentClient create(Throwable cause) {
        return new PaymentClient() {
            @Override
            public PaymentResponseDto addPayment(PaymentRequestDto paymentRequestDto) {
               throwFeignEx(cause);
               throw new PaymentServiceException();
            }

            @Override
            public PaymentResponseDto getPayment(String id) {
                throwFeignEx(cause);
                throw new PaymentServiceException();
            }

            @Override
            public Page<PaymentResponseDto> getPayments(Pageable pageable) {
                throwFeignEx(cause);
                throw new PaymentServiceException();
            }

            @Override
            public Page<PaymentResponseDto> getPaymentsByUserIdOrOrderIdOrStatus(Long userId, Long orderId, PaymentStatus status, Pageable pageable) {
                throwFeignEx(cause);
                throw new PaymentServiceException();
            }

            @Override
            public Long getSumByDateRange(LocalDateTime start, LocalDateTime end, Long userId) {
                throwFeignEx(cause);
                throw new PaymentServiceException();
            }

            @Override
            public PaymentResponseDto changePaymentStatus(String id, PaymentStatus status) {
                throwFeignEx(cause);
                throw new PaymentServiceException();
            }

            @Override
            public Void deletePayment(String id) {
                throwFeignEx(cause);
                throw new PaymentServiceException();
            }
        };
    }

}
