package com.benedykt.budget_control.imports.controller;

import com.benedykt.budget_control.imports.mbank.BankTransactionImporter;
import com.benedykt.budget_control.imports.service.TransactionImportFacade;
import com.benedykt.budget_control.openAiApi.dtos.TransactionCategorizedByOpenAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/imports")
@RequiredArgsConstructor
public class ImportsBankController {

    private final BankTransactionImporter bankTransactionImporter;
    private final TransactionImportFacade transactionImportFacade;

    @PostMapping("/mbank-csv")
    public List<TransactionCategorizedByOpenAiResponse> importMBankTransactionsCSV(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return transactionImportFacade.importAndCategorize(file.getInputStream());

    }
}
