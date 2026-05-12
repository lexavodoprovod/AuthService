package com.innowise.authservice.dto.response;

import com.innowise.authservice.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto{
    String id;

    Long userId;

    Long orderId;

    PaymentStatus status;

    Long paymentAmount;
}
