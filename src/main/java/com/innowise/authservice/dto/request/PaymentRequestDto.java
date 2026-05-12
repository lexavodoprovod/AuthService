package com.innowise.authservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto{
    @NotNull
    private Long userId;

    @NotNull
    private Long orderId;

    @NotNull
    private Long paymentAmount;
}
