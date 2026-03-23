package com.benedykt.budget_control.imports.mbank;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
public class MBankParsedTransaction {

    private LocalDate transactionDate;
    private String description;
    private String title;
    private BigDecimal amount;

    public MBankParsedTransaction(LocalDate transactionDate, String description, String title, BigDecimal amount) {
        this.transactionDate = transactionDate;
        this.description = description;
        this.title = title;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

}
