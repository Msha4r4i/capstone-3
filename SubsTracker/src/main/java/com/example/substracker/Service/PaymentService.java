package com.example.substracker.Service;

import com.example.substracker.API.ApiException;
import com.example.substracker.Model.PaymentRequest;
import com.example.substracker.Model.User;
import com.example.substracker.Repository.PaymentRequestRepository;
import com.example.substracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final UserRepository userRepository;

    private final PdfService pdfService;
    private final PdfMailService pdfMailService;

    @Value("${moyasar.api.key}")
    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    public void addPaymentCard(Integer userId, PaymentRequest paymentRequest){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("user not found");
        }
        paymentRequest.setUser(user);
        paymentRequestRepository.save(paymentRequest);
        user.setPaymentRequest(paymentRequest);
        userRepository.save(user);
    }

    public ResponseEntity<?> processPayment(Integer userId){
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("user not found");
        }
        if (user.getPaymentRequest() == null) {
            throw new ApiException("user card Info is Empty (Add Card First)");
        }
        if(user.getPaymentRequest().getAmount() < 30){
            throw new ApiException("you dont have enough Money 30$ Required");
        }
        if(Boolean.TRUE.equals(user.getIsSubscribed())){
            throw new ApiException("you are already Subscribed Before");
        }

        String url = "https://api.moyasar.com/v1/payments/";
        String callbackUrl = "http://localhost:8080/api/v1/payment/process-payment";

        String requestBody = String.format(
                "source[type]=card" +
                        "&source[name]=%s" +
                        "&source[number]=%s" +
                        "&source[cvc]=%s" +
                        "&source[month]=%s" +
                        "&source[year]=%s" +
                        "&amount=%d" +
                        "&currency=%s" +
                        "&callback_url=%s",
                user.getPaymentRequest().getName(),
                user.getPaymentRequest().getNumber(),
                user.getPaymentRequest().getCvc(),
                user.getPaymentRequest().getMonth(),
                user.getPaymentRequest().getYear(),
                (int) (user.getPaymentRequest().getAmount() * 100),
                user.getPaymentRequest().getCurrency(),
                callbackUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.getBody());

            String paymentId = root.path("id").asText(null);
            String transactionUrl = root.path("source").path("transaction_url").asText(null);

            if (paymentId == null || transactionUrl == null) {
                throw new ApiException("Payment response missing required fields (id / transaction_url)");
            }

            PaymentRequest pr = user.getPaymentRequest();
            pr.setPaymentUserId(paymentId);
            pr.setRedirectToCompletePayment(transactionUrl);
            paymentRequestRepository.save(pr);

            java.util.Map<String, String> result = new java.util.HashMap<>();
            result.put("payment_user_id", paymentId);
            result.put("transaction_url", transactionUrl);

            return ResponseEntity.status(response.getStatusCode()).body(result);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ApiException("Failed to parse payment response JSON");
        }
    }

    // When paid -> email PDF receipt to the user (no controller needed)
    // Mshari
    public String checkPayment(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("user not found");
        }
        if (user.getPaymentRequest() == null) {
            throw new ApiException("user dose not have card and the payment Process is Not Started yet");
        }
        if (user.getPaymentRequest().getPaymentUserId() == null) {
            throw new ApiException("Pay first before Changing the status");
        }
        if(user.getIsSubscribed() == true){
            throw new ApiException("you are subscriber");
        }

        // local debit
        user.getPaymentRequest().setAmount(user.getPaymentRequest().getAmount() - 30);

        String paymentId = user.getPaymentRequest().getPaymentUserId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + paymentId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.getBody());

            String status = root.path("status").asText("");
            boolean paid = "paid".equalsIgnoreCase(status);

            user.setIsSubscribed(paid);
            userRepository.save(user);

            if (paid) {
                // 1) Build PDF receipt
                // Mshari
                double charged = 30.0;
                byte[] pdf = pdfService.buildAiPurchaseReceipt(paymentId, user.getName(), user.getEmail(), charged);

                // 2) Email it to the user
                String subject = "Your AI SubsTracker receipt";
                String html = """
                        <div style="font-family:Arial,Helvetica,sans-serif">
                          <h2 style="margin:0 0 8px 0">Thanks for your purchase!</h2>
                          <p style="margin:0 0 12px 0">Your subscription to <b>AI SubsTracker</b> is now active.</p>
                          <p style="margin:0 0 12px 0">We've attached your receipt as a PDF.</p>
                          <p style="color:#6b7280;font-size:12px;margin:16px 0 0 0">If you didn't authorize this payment, please contact support.</p>
                        </div>
                        """;
                String filename = "AI-Receipt-" + paymentId + ".pdf";

                pdfMailService.sendHtmlEmailWithAttachment(
                        user.getEmail(),
                        subject,
                        html,
                        filename,
                        pdf
                );

                return "successfully subscribe (receipt emailed)";
            } else {
                String txUrl = root.path("source").path("transaction_url").asText(null);
                if (txUrl == null || "null".equals(txUrl)) {
                    txUrl = user.getPaymentRequest().getRedirectToCompletePayment();
                }
                return "submit the request from this Url: " + txUrl;
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ApiException("Failed to parse payment JSON");
        }
    }

    public PaymentRequest getPaymentCardByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user ==  null){
            throw new ApiException("user not found");
        }
        if(user.getPaymentRequest() == null){
            throw new ApiException("user dose not add his Payment Card");
        }
        return user.getPaymentRequest();
    }
}
