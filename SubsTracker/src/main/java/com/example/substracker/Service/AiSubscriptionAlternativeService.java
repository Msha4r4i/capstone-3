package com.example.substracker.Service;

import com.example.substracker.API.ApiException;
import com.example.substracker.DTO.AiSubscriptionAlternativeDTOOut;
import com.example.substracker.Model.AiSubscriptionAlternative;
import com.example.substracker.Model.Subscription;
import com.example.substracker.Repository.AiSubscriptionAlternativeRepository;
import com.example.substracker.Repository.SubscriptionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AiSubscriptionAlternativeService {

    private final SubscriptionRepository subscriptionRepository;
    private final AiSubscriptionAlternativeRepository aiSubscriptionAlternativeRepository;
    private final AiService aiService; // خدمة محادثة AI لديك
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> ALLOWED_PERIODS = Set.of("monthly","3month","6month","yearly");

    // اسم الميثود باقٍ كما هو عندك (يستقبل subscriptionId)
    public AiSubscriptionAlternative getAiSubscriptionAlternativeBySubscriptionId(Integer subscriptionId){
        Subscription s = subscriptionRepository.findSubscriptionById(subscriptionId);
        if (s == null) throw new ApiException("Subscription not found");

        // 1) البرومبت
        String prompt = buildPrompt(s);

        // 2) اتصال الـAI
        String chatResponse = aiService.chat(prompt);
        if (chatResponse == null || chatResponse.isBlank()) {
            throw new ApiException("AI returned empty response");
        }

        // 3) Parsing JSON
        JsonNode node;
        try {
            node = objectMapper.readTree(chatResponse.trim());
        } catch (Exception e) {
            throw new ApiException("AI response is not a valid JSON: " + chatResponse);
        }

        String altName   = node.path("alternative_service_name").asText("");
        double altPrice  = node.path("alternative_price").asDouble(-1);
        String altPeriod = node.path("alternative_billing_period").asText("");
        String reason    = node.path("recommendation_reason").asText("");

        // 4) تحقق يطابق قيود المودل
        if (altName.isBlank())  throw new ApiException("AI: alternative_service_name is missing");
        if (altPrice < 0)       throw new ApiException("AI: alternative_price must be >= 0");
        altName = limitChars(altName, 100).trim(); // المودل ≤100

        // الفترات المسموحة فقط
        altPeriod = normalizePeriod(altPeriod, s.getBillingPeriod());

        // السبب إلزامي (NotEmpty) + قاعدة عمل ≤ 50 كلمة
        reason = limitWords(reason, 50).trim();
        if (reason.isBlank()) {
            reason = "Cost-effective alternative with similar features.";
        }

        // 5) بناء الكيان + حساب التوفير الشهري محليًا
        AiSubscriptionAlternative alt = new AiSubscriptionAlternative();
        alt.setAlternativeServiceName(altName);
        alt.setAlternativePrice(altPrice);
        alt.setAlternativeBillingPeriod(altPeriod);
        alt.setRecommendationReason(reason);

        double currentMonthly = toMonthly(s.getPrice(), s.getBillingPeriod());
        double altMonthly     = toMonthly(altPrice, altPeriod);
        double savings        = Math.max(0, round(currentMonthly - altMonthly, 2));
        alt.setPotentialMonthlySavings(savings);

        // 6) حفظ وإرجاع (createdAt يتعبّى تلقائيًا)
        return aiSubscriptionAlternativeRepository.save(alt);
    }

    // Method to return DTO instead of entity
    public AiSubscriptionAlternativeDTOOut getAiSubscriptionAlternativeDTOOutBySubscriptionId(Integer subscriptionId){
        AiSubscriptionAlternative alternative = getAiSubscriptionAlternativeBySubscriptionId(subscriptionId);

        return new AiSubscriptionAlternativeDTOOut(
                alternative.getAlternativeServiceName(),
                alternative.getAlternativePrice(),
                alternative.getAlternativeBillingPeriod(),
                alternative.getRecommendationReason(),
                alternative.getPotentialMonthlySavings()
        );
    }

    // Method to convert existing entity to DTO
    public AiSubscriptionAlternativeDTOOut convertToDTO(AiSubscriptionAlternative alternative) {
        if (alternative == null) {
            throw new ApiException("AiSubscriptionAlternative cannot be null");
        }

        return new AiSubscriptionAlternativeDTOOut(
                alternative.getAlternativeServiceName(),
                alternative.getAlternativePrice(),
                alternative.getAlternativeBillingPeriod(),
                alternative.getRecommendationReason(),
                alternative.getPotentialMonthlySavings()
        );
    }

    // Method to get DTO by alternative ID (if you have the ID directly)
    public AiSubscriptionAlternativeDTOOut getAiSubscriptionAlternativeDTOOutById(Integer alternativeId) {
        AiSubscriptionAlternative alternative = aiSubscriptionAlternativeRepository.findById(alternativeId)
                .orElseThrow(() -> new ApiException("AI Subscription Alternative not found"));

        return convertToDTO(alternative);
    }

    // ---------- Helpers ----------

    private String buildPrompt(Subscription s) {
        // JSON واحد فقط، أسماء الحقول snake_case مطابقة لجدولك
        return """
        You are a pricing assistant. Given the current subscription, propose ONE realistic alternative service with similar functionality and a better price/value if possible.

        STRICT OUTPUT:
        - Return ONLY a single JSON object (no prose, no markdown), with EXACTLY these fields:
          {
            "alternative_service_name": "<string>",
            "alternative_price": <number>,
            "alternative_billing_period": "<monthly|3month|6month|yearly>",
            "recommendation_reason": "<string up to 50 words>",
            "potential_monthly_savings": <number>
          }

        RULES:
        - Use current, realistic market prices (prefer SAR for KSA; otherwise USD-equivalent). Be conservative if unsure.
        - "alternative_billing_period" MUST be one of: "monthly","3month","6month","yearly".
        - Keep "recommendation_reason" under 50 words and mention key trade-off(s).
        - If the alternative is NOT cheaper on a monthly basis, set "potential_monthly_savings" to 0.

        CURRENT_SUBSCRIPTION:
        {
          "subscription_name": "%s",
          "category": "%s",
          "price": %s,
          "billing_period": "%s",
          "status": "%s",
          "description": "%s"
        }

        Monthly price math: monthly=1, 3month=3, 6month=6, yearly=12.
        Return only the JSON object.
        """.formatted(
                ns(s.getSubscriptionName()),
                ns(s.getCategory()),
                s.getPrice() == null ? "0" : s.getPrice().toString(),
                ns(s.getBillingPeriod()),
                ns(s.getStatus()),
                ns(s.getDescription())
        );
    }

    private static String ns(String v) { return v == null ? "" : v; }

    private static String normalizePeriod(String periodFromAi, String fallbackFromSub) {
        String p = periodFromAi == null ? "" : periodFromAi.trim().toLowerCase();
        if (ALLOWED_PERIODS.contains(p)) return p;
        String f = fallbackFromSub == null ? "" : fallbackFromSub.trim().toLowerCase();
        return ALLOWED_PERIODS.contains(f) ? f : "monthly";
    }

    private static double toMonthly(Double price, String period) {
        if (price == null) price = 0.0;
        String p = period == null ? "" : period.trim().toLowerCase();
        return switch (p) {
            case "monthly" -> price;
            case "3month"  -> price / 3.0;
            case "6month"  -> price / 6.0;
            case "yearly"  -> price / 12.0;
            default        -> price; // fallback
        };
    }

    private static String limitWords(String text, int maxWords) {
        if (text == null || text.isBlank()) return "";
        String[] words = text.trim().split("\\s+");
        if (words.length <= maxWords) return text.trim();
        return String.join(" ", java.util.Arrays.copyOfRange(words, 0, maxWords)).trim();
    }

    private static String limitChars(String text, int maxChars) {
        if (text == null) return "";
        return text.length() <= maxChars ? text : text.substring(0, maxChars);
    }

    private static double round(double val, int scale) {
        return new BigDecimal(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}