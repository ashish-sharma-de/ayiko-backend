package com.ayiko.backend.controller.payment;

import com.ayiko.backend.service.payment.PaymentService;
import com.ayiko.backend.service.payment.dto.PaymentRequestResponse;
import com.ayiko.backend.service.payment.dto.PaymentUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/{customerId}")
    public ResponseEntity<String> createPayment(@PathVariable UUID customerId) {
        //Check in temp order if it is present.
        //If not present create payment url
        //Check for time stamp in the temp order if it's older than 15 mins and still not completed or cancelled then delete it.
        if(paymentService.isPaymentInProcessForCustomer(customerId)){
            //Return error and ask user to wait
        }
        PaymentUrlResponse paymentUrlResponse = paymentService.createPaymentUrl(customerId);
        paymentService.addPaymentRequest(paymentUrlResponse);
        return ResponseEntity.ok(paymentUrlResponse.getPaymentUrl());
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccessCallBack(@RequestBody PaymentRequestResponse paymentResponse) {
        System.out.println("Received Payment Request: " + paymentResponse);
        return ResponseEntity.ok("");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancelCallBack(@RequestBody PaymentRequestResponse paymentResponse) {
        System.out.println("Received Payment Request: " + paymentResponse);
        return ResponseEntity.ok("");
    }
}
