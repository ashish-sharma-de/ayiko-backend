package com.ayiko.backend.controller.payment;

import com.ayiko.backend.service.payment.PaymentService;
import com.ayiko.backend.service.payment.dto.PaymentRequest;
import com.ayiko.backend.service.payment.dto.PaymentRequestResponse;
import com.ayiko.backend.service.payment.dto.PaymentUrlResponse;
import com.ayiko.backend.service.payment.impl.MobileMoneyPaymentService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentController.class);


    @PostMapping()
    public ResponseEntity<String> generatePaymentLink(@RequestBody PaymentRequest paymentRequest) {
        try {
            //Check in temp order if it is present.
            //If not present create payment url
            //Check for time stamp in the temp order if it's older than 15 mins and still not completed or cancelled then delete it.
            if(!paymentService.isPaymentInProcessForCustomer(paymentRequest)) {
                PaymentUrlResponse paymentUrlResponse = paymentService.generatePaymentUrl(paymentRequest);
                return ResponseEntity.ok(paymentUrlResponse.getPaymentUrl());
            }else{
                //TODO: Throw error or ask user to wait.
                return ResponseEntity.ok("Wait for 15 mins");
            }
        } catch (Exception e) {
            logger.error("Error while generating payment link for payment request ", paymentRequest, e);
            return ResponseEntity.status(500).body("Error while generating payment link for payment request ");
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccessCallBack(@RequestBody PaymentRequestResponse paymentResponse) {
        logger.info("Payment Successful for paymentRequestResponse {}", paymentResponse);
        paymentService.updatePaymentRequest(paymentResponse);
        return ResponseEntity.ok("");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancelCallBack(@RequestBody PaymentRequestResponse paymentResponse) {
        logger.info("Payment cancelled for paymentRequestResponse {}", paymentResponse);
        paymentService.updatePaymentRequest(paymentResponse);
        return ResponseEntity.ok("");
    }

    @PostMapping("/webhook")
    public ResponseEntity webhook(@RequestBody PaymentRequestResponse paymentResponse){
        logger.info("Webhook came for paymentRequestResponse {}", paymentResponse);
        paymentService.updatePaymentRequest(paymentResponse);
        return ResponseEntity.ok().build();
    }


}
