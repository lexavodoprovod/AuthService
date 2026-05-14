package com.innowise.authservice.dto.request;

import com.innowise.authservice.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePaymentStatusRequest {
    @NotNull
    private PaymentStatus paymentStatus;
}
