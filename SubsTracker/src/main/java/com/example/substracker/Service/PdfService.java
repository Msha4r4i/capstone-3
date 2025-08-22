package com.example.substracker.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;

    //Mshari
    public PdfService(TemplateEngine templateEngine){
        this.templateEngine = templateEngine;
    }

    // Generic HTML -> PDF
    public byte[] generatePdf(String templateName, Map<String,Object> data){
        try {
            Context context = new Context();
            if (data != null) context.setVariables(data);

            String html = templateEngine.process(templateName, context);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html, null);
                builder.toStream(baos);
                builder.run();
                return baos.toByteArray();
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    // Build receipt for AI purchase (one-time)
    public byte[] buildAiPurchaseReceipt(String paymentId,
                                         String userName,
                                         String userEmail,
                                         double amount) {
        Map<String, Object> vars = Map.of(
                "subscriptionId", paymentId,            // نستخدم paymentId رقم إيصال
                "userName", userName,
                "userEmail", userEmail,
                "serviceName", "AI SubsTracker",
                "planName", "Lifetime",
                "period", "one-time",
                "priceFormatted", formatMoney(amount),
                "totalFormatted", formatMoney(amount),
                "date", LocalDate.now().toString()
        );
        return generatePdf("subscription-receipt", vars);
    }

    private static String formatMoney(double amount) {
        return String.format("%.2f SAR", amount);
    }
}
