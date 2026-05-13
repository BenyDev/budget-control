package com.benedykt.budget_control.payment.services;

import com.benedykt.budget_control.openAiApi.dtos.TransactionCategorizedByOpenAiResponse;
import com.benedykt.budget_control.payment.dtos.PaymentTransactionCategorizedResponse;

import java.util.List;

public interface PaymentService {

    List<PaymentTransactionCategorizedResponse> addCategorizedTransactionsByOpenAI(List<TransactionCategorizedByOpenAiResponse> categorizedTransaction);
    List<PaymentTransactionCategorizedResponse> showCategorizedTransactionsByOpenAI(List<TransactionCategorizedByOpenAiResponse> categorizedTransaction);
}
