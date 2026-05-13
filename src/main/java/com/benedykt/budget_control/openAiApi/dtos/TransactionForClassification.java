package com.benedykt.budget_control.openAiApi.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TransactionForClassification {

    private int index;
    private LocalDate transactionDate;
    private String description;
    private String title;
    private BigDecimal amount;
}
