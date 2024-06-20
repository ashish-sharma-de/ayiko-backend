package com.ayiko.backend.dto.order;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrderPaymentDTO {
    private OrderPaymentMethod paymentMethod;
    private OrderPaymentStatus orderPaymentStatus;
    private LocalDate paymentDate;
}
