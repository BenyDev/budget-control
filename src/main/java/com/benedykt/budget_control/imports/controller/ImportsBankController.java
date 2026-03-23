package com.benedykt.budget_control.imports.controller;

import com.benedykt.budget_control.imports.mbank.BankTransactionImporter;
import com.benedykt.budget_control.imports.mbank.MBankParsedTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/imports")
@RequiredArgsConstructor
public class ImportsBankController {

    private final BankTransactionImporter bankTransactionImporter;

    @PostMapping("/mbank-csv")
    public List<MBankParsedTransaction> importMBankTransactionsCSV(
            @RequestParam("file")MultipartFile file
            ) throws IOException {

        return bankTransactionImporter.importTransactionsCSV(file.getInputStream());
    }
}
