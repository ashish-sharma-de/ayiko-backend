package com.ayiko.backend.repository.entity;

import com.ayiko.backend.dto.cart.CartPaymentConfirmationStatus;
import com.ayiko.backend.dto.cart.CartPaymentReceiptStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartPaymentEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private CartEntity cart;
    private CartPaymentConfirmationStatus confirmationStatus;
    private CartPaymentReceiptStatus receiptStatus;
    private LocalDate confirmationDate;
    private LocalDate receiptDate;

}
