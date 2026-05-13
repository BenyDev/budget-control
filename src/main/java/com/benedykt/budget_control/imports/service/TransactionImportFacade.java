package com.benedykt.budget_control.imports.service;

import com.benedykt.budget_control.imports.mbank.BankTransactionImporter;
import com.benedykt.budget_control.imports.mbank.MBankParsedTransaction;
import com.benedykt.budget_control.openAiApi.Service.TransactionCategorizerService;
import com.benedykt.budget_control.openAiApi.dtos.TransactionCategorizedByOpenAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionImportFacade {

    private final BankTransactionImporter bankTransactionImporter;
    private final TransactionCategorizerService transactionCategorizerService;

    public List<TransactionCategorizedByOpenAiResponse> importAndCategorize(InputStream inputStream){
        List<MBankParsedTransaction> transactions = bankTransactionImporter.importTransactionsCSV(inputStream);
        return transactionCategorizerService.categorizeTransaction(transactions);
    }
}
