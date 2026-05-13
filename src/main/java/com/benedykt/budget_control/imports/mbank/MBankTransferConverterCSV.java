package com.benedykt.budget_control.imports.mbank;

import com.benedykt.budget_control.exceptions.CsvImportException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

import static com.benedykt.budget_control.utils.TextNormalizer.normalizeSpaces;

@Slf4j
@Service
public class MBankTransferConverterCSV implements BankTransactionImporter {

    private static  boolean hasRequiredHeaders(Map<String, Integer> headers) {
        String[] lookingForHeaders = new String[]
                {"#Data operacji","#Opis operacji","#Tytuł","#Kwota"};

        for( int i = 0; i < lookingForHeaders.length; i++ ) {
            if(!headers.containsKey(lookingForHeaders[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidDate(String row) {

        try{
            LocalDate.parse(row);
            return true;
        } catch(DateTimeException e){
            return false;
        }
    }


    @Override
    public List<MBankParsedTransaction> importTransactionsCSV(InputStream inputStream) {
        List<MBankParsedTransaction> transactionsMbank = new ArrayList<>();

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(inputStream, Charset.forName("windows-1250"))
                ).withCSVParser(parser).build()) {


            List<String[]> entryCsvList = new ArrayList<>();
            String[] entryCsvLine;
            while ((entryCsvLine = reader.readNext()) != null) {
                if (entryCsvLine.length != 9) continue;

                for (int i = 0; i < entryCsvLine.length; i++) {
                    entryCsvLine[i] = entryCsvLine[i].replace("'", "");
                }
                entryCsvList.add(entryCsvLine);

            }


            String[] headers = entryCsvList.getFirst();

            Map<String, Integer> headersIndex = new HashMap<>();

            for (int i = 0; i < headers.length; i++) {
                headersIndex.put(headers[i], i);
            }

            if (!hasRequiredHeaders(headersIndex)) {
                throw new CsvImportException("Missing required headers in csv file");
            }

            entryCsvList.removeFirst(); // Removing row with headers

            for (String[] transactionCsvLine : entryCsvList) {

                LocalDate transactionDateCsv;
                if (!isValidDate(transactionCsvLine[headersIndex.get("#Data operacji")])) continue;

                transactionDateCsv = LocalDate.parse(transactionCsvLine[headersIndex.get("#Data operacji")]);

                String descriptionCsv = normalizeSpaces(transactionCsvLine[headersIndex.get("#Opis operacji")]);
                String transactionTitleCsv = normalizeSpaces(transactionCsvLine[headersIndex.get("#Tytuł")]);
                String amountCsv = normalizeSpaces(transactionCsvLine[headersIndex.get("#Kwota")]);

                amountCsv = amountCsv
                        .replaceAll("\u00A0", "")
                        .replace(" ", "")
                        .replace(",", ".")
                        .trim();
                BigDecimal transactionAmountCsv = new BigDecimal(amountCsv);

                MBankParsedTransaction transaction = new MBankParsedTransaction(
                        transactionDateCsv,
                        descriptionCsv,
                        transactionTitleCsv,
                        transactionAmountCsv
                );
                transactionsMbank.add(transaction);
            }
        }catch (CsvException | IOException e) {
            throw new CsvImportException("Failed to parse CSV file",e);
        }
        return transactionsMbank;
    }
}