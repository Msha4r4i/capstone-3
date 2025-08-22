package com.example.substracker.Service;

import com.example.substracker.API.ApiException;
import com.example.substracker.DTO.AiAnalysisDTOOut;
import com.example.substracker.Model.AiAnalysis;
import com.example.substracker.Model.SpendingAnalysis;
import com.example.substracker.Model.User;
import com.example.substracker.Repository.AiAnalysisRepository;
import com.example.substracker.Repository.SpendingAnalysisRepository;
import com.example.substracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AiAnalysisService {
    private final SpendingAnalysisRepository spendingAnalysisRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    //create update delete To spending Analysis will affect the AI Analysis directly.
    public void addOrUpdateRecommendation(Integer userId, Integer spendingAnalysisId){

        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }

        SpendingAnalysis spendingAnalysis = spendingAnalysisRepository.findSpendingAnalysisById(spendingAnalysisId);
        if(spendingAnalysis == null){
            //change from runtime exception to ApiException
            throw new ApiException("spending AnalysisId not found");
        }

        AiAnalysis aiAnalysis = spendingAnalysis.getAiAnalysis();
        if(aiAnalysis == null){
            throw new ApiException("AI Analysis not found");
        }

        String prompt = """
        You are an assistant that generates a short financial recommendation for a user.
        You have access to the following spending analysis attributes:
        - Digital subscriptions total price: %s
        - Service subscriptions total price: %s
        - Total spending price: %s
        - Average subscription cost: %s
        - Spending to income ratio: %s
        - Total subscriptions count: %s
        - Digital subscriptions count: %s
        - Service subscriptions count: %s
        - User monthly salary: %s

        Your task:
        - Start the message with a friendly greeting to the user.
        - Give one clear and concise general recommendation based on the provided spending analysis
          and the user's salary.
        - The advice should be practical and easy to understand.
        - Keep the response short (no more than 100 words).
        - Avoid technical or complex financial jargon.
        """.formatted(
                spendingAnalysis.getDigitalSubscriptionsTotalPrice(),
                spendingAnalysis.getServiceSubscriptionsTotalPrice(),
                spendingAnalysis.getTotalSpendingPrice(),
                spendingAnalysis.getAverageSubscriptionCost(),
                spendingAnalysis.getSpendingToIncomeRatio(),
                spendingAnalysis.getTotalSubscriptionsCount(),
                spendingAnalysis.getDigitalSubscriptionsCount(),
                spendingAnalysis.getServiceSubscriptionsCount(),
                user.getMonthlySalary()
        );

        String chatResponse = aiService.chat(prompt);
        if(chatResponse != null){
            aiAnalysis.setGeneralRecommendations(chatResponse);
        }
        aiAnalysis.setSpendingAnalysis(spendingAnalysis);
        aiAnalysisRepository.save(aiAnalysis);
    }

    public AiAnalysis getAiAnalysisByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        if(user.getSpendingAnalysis() == null){
            throw new ApiException("User spending analysis not found");
        }
        if(user.getIsSubscribed() == false){
            throw new ApiException("User is not subscribed");
        }
        if(user.getSpendingAnalysis().getAiAnalysis() == null){
            throw new ApiException("user have not subscription ");
        }
        return user.getSpendingAnalysis().getAiAnalysis();
    }

    public AiAnalysisDTOOut getAiAnalysisDTOOutByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        if(user.getSpendingAnalysis() == null){
            throw new ApiException("User spending analysis not found");
        }
        if(user.getSpendingAnalysis().getAiAnalysis() == null){
            throw new ApiException("AI Analysis not found for this user");
        }

        AiAnalysis aiAnalysis = user.getSpendingAnalysis().getAiAnalysis();

        return new AiAnalysisDTOOut(
                aiAnalysis.getGeneralRecommendations()
        );
    }

}