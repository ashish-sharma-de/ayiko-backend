package com.ayiko.backend.controller.payment;

import com.ayiko.backend.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> createPayment() {
        System.out.println("createPayment aya");
        String response = paymentService.createPaymentUrl();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/success")
    public ResponseEntity<String> paymentSuccessCallBack(@RequestBody Map<String, Object> payload) {
        System.out.println("Received Payment Request: " + payload);
        payload.entrySet().forEach(stringObjectEntry -> {
            System.out.println(stringObjectEntry.getKey() +"->"+ stringObjectEntry.getValue());
        });
        return ResponseEntity.ok("");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> paymentCancelCallBack(@RequestBody Map<String, Object> payload) {
        System.out.println("Received Payment Request: " + payload);
        payload.entrySet().forEach(stringObjectEntry -> {
            System.out.println(stringObjectEntry.getKey() +"->"+ stringObjectEntry.getValue());
        });
        return ResponseEntity.ok("");
    }
}
