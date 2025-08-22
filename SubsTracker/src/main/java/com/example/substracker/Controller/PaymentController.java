package com.example.substracker.Controller;

import com.example.substracker.API.ApiResponse;
import com.example.substracker.Model.PaymentRequest;
import com.example.substracker.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    //made By Hassan
    @PostMapping("/process-payment/{userId}")
    //send only the user.
    public ResponseEntity<?>
    processPayment(@PathVariable Integer userId){

        return ResponseEntity.status(200).body(paymentService.processPayment(userId));
    }
    //made BY Hassan
    @PutMapping("/check-payment/{userId}")
    public ResponseEntity<?> checkPayment(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(paymentService.checkPayment(userId));
    }

    //made By Hassan
    @GetMapping("/get-card/{userId}")
    public ResponseEntity<?> getPaymentCardByUserId(@PathVariable Integer userId){
        return ResponseEntity
                .status(200)
                .body(paymentService.getPaymentCardByUserId(userId));
    }


    @PostMapping("/add-card/{userId}")
    public ResponseEntity<?> addPaymentCard(@PathVariable Integer userId ,@RequestBody @Valid PaymentRequest paymentCard){
        paymentService.addPaymentCard(userId,paymentCard);
        return ResponseEntity
                .status(200)
                .body(new ApiResponse("card Added successfully"));
    }
}