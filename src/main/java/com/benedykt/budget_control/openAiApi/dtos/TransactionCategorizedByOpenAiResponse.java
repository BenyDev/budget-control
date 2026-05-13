package com.benedykt.budget_control.openAiApi.dtos;

import com.benedykt.budget_control.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TransactionCategorizedByOpenAiResponse {

    private LocalDate transactionDate;
    private String description;
    private String title;
    private BigDecimal amount;
    private Category category;
}
