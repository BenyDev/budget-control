package com.benedykt.budget_control.imports.mbank;

import java.io.InputStream;
import java.util.List;

public interface BankTransactionImporter {
    List<MBankParsedTransaction> importTransactionsCSV(InputStream inputStream);
}
