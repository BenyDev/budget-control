package com.benedykt.budget_control.openAiApi.Service;


import com.benedykt.budget_control.imports.mbank.MBankParsedTransaction;
import com.benedykt.budget_control.openAiApi.dtos.TransactionCategorizedByOpenAiResponse;

import java.util.List;

public interface TransactionCategorizerService {

    List<TransactionCategorizedByOpenAiResponse> categorizeTransaction(List<MBankParsedTransaction> transactions);
}
