package com.example.substracker.Repository;

import com.example.substracker.Model.SpendingAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpendingAnalysisRepository extends JpaRepository<SpendingAnalysis,Integer> {
    SpendingAnalysis findSpendingAnalysisById(Integer id);

    SpendingAnalysis findSpendingANalysisByUserId(Integer userId);

}
