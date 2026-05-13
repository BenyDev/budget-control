package com.benedykt.budget_control.openAiApi.Service;

import com.benedykt.budget_control.config.OpenAiProperties;
import com.benedykt.budget_control.enums.Category;
import com.benedykt.budget_control.imports.mbank.MBankParsedTransaction;
import com.benedykt.budget_control.openAiApi.dtos.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCategorizerServiceImpl implements TransactionCategorizerService {

    private static final int BATCH_SIZE = 100;
    private static final int MAX_TRANSACTIONS = 100;

    private final OpenAiClassificationGateway openAiGateway;
    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public List<TransactionCategorizedByOpenAiResponse> categorizeTransaction(List<MBankParsedTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            log.info("No transactions to categorize.");
            return List.of();
        }
        if (transactions.size() > MAX_TRANSACTIONS) {
            throw new RuntimeException("Too many transactions in one request");
        }

        int totalTransactions = transactions.size();
        int totalBatches = (totalTransactions + BATCH_SIZE - 1) / BATCH_SIZE;

        log.info("Starting categorization for {} transactions in {} batches. Model={}",
                totalTransactions, totalBatches, properties.getModel());

        List<TransactionCategorizedByOpenAiResponse> result = new ArrayList<>();

        for (int batchStartIndex = 0; batchStartIndex < totalTransactions; batchStartIndex += BATCH_SIZE) {
            int batchEndIndex = Math.min(batchStartIndex + BATCH_SIZE, totalTransactions);
            int batchNumber = (batchStartIndex / BATCH_SIZE) + 1;

            List<MBankParsedTransaction> transactionBatch = transactions.subList(batchStartIndex, batchEndIndex);

            log.info("Sending batch {}/{} to OpenAI. Global indexes: {}-{}. Batch size={}",
                    batchNumber, totalBatches, batchStartIndex, batchEndIndex - 1, transactionBatch.size());

            List<TransactionCategorizedByOpenAiResponse> categorizedBatch =
                    categorizeBatch(transactionBatch, batchStartIndex, batchNumber, totalBatches);

            result.addAll(categorizedBatch);

            log.info("Finished batch {}/{}. Categorized {} transactions.",
                    batchNumber, totalBatches, categorizedBatch.size());
        }

        log.info("Categorization finished. Total categorized transactions={}", result.size());
        return result;
    }

    private List<TransactionCategorizedByOpenAiResponse> categorizeBatch(
            List<MBankParsedTransaction> batch,
            int globalStartIndex,
            int batchNumber,
            int totalBatches
    ) {
        List<TransactionForClassification> indexedBatch = new ArrayList<>();

        for (int localIndex = 0; localIndex < batch.size(); localIndex++) {
            MBankParsedTransaction tx = batch.get(localIndex);
            int globalIndex = globalStartIndex + localIndex;

            indexedBatch.add(new TransactionForClassification(
                    globalIndex,
                    tx.getTransactionDate(),
                    tx.getDescription(),
                    tx.getTitle(),
                    tx.getAmount()
            ));
        }

        String developerPrompt = """
                Jesteś klasyfikatorem transakcji bankowych użytkownika w Polsce.
                
                Masz przypisać każdej transakcji dokładnie jedną kategorię:
                SHOPPING, HOUSING, FOOD_DRINKS, TRANSPORT, ENTERTAINMENT, PETS, OTHER.
                
                Zasady klasyfikacji:
                
                FOOD_DRINKS:
                - supermarkety, sklepy spożywcze, piekarnie, cukiernie, restauracje, fast food, delivery jedzenia, warzywniaki
                - przykłady:
                  CARREFOUR, CARREFOUR EXPRESS, CARREFOUR MARKET
                  LIDL, BIEDRONKA, AUCHAN, ŻABKA
                  CUKIERNIA SOWA, PIEKARNIA, MOJA PIEKARNIA
                  KUCHNIA MARCHE, McDonalds, KFC, BURGER KING
                  GLOVO, UBER EATS, PYSZNE.PL
                  WARZYWA, HANDEL WARZYWA I OW
                  LUCA BAKERY, BECZKA WINA
                
                SHOPPING:
                - zakupy detaliczne, odzież, sklepy internetowe, marketplace, kwiaciarnie
                - przykłady:
                  OLX.PL, ALLEGRO, AMAZON
                  HALF PRICE, ZARA, H&M
                  SKLEP.KFD.PL
                  KWIACIARNIA, FLOWER SHOP
                  ELECTRONICS, RTV EURO AGD
                
                TRANSPORT:
                - paliwo, bilety komunikacji miejskiej, parkingi, myjnie
                - przykłady:
                  ORLEN, SHELL, BP
                  BILETOMAT, ZTM, MPK
                  MYJNIA SAMOOBSŁUGOWA
                  PARKING, AUTOSTRADA
                
                ENTERTAINMENT:
                - rozrywka, subskrypcje, wydarzenia, fitness
                - przykłady:
                  APPLE.COM/BILL, SPOTIFY, NETFLIX
                  GOINGAPP.PL
                  BOOKBEAT
                  ZDROFIT, SIŁOWNIA, FITNESS
                  CENTRUM SPORTU
                
                HOUSING:
                - mieszkanie, rachunki, czynsz, internet domowy
                - przykłady:
                  VECTRA, UPC, ORANGE (internet domowy)
                  CZYNSZ, OPŁATY MIESZKANIOWE
                  MEDIA, PRĄD, GAZ
                
                PETS:
                - wydatki na zwierzęta
                - przykłady:
                  WETERYNARZ, ZOOLOGICZNY
                  KARMA DLA PSA, KARMA DLA KOTA
                
                OTHER:
                - przelewy, BLIK P2P, wpływy, zwroty, niejednoznaczne operacje
                - przykłady:
                  PRZELEW ŚRODKÓW
                  BLIK P2P
                  PREZENCIK
                  WYNAGRODZENIE
                  WPŁATA
                  ZWROT
                
                Dodatkowe zasady:
                - jeśli nazwa zawiera znany sklep spożywczy → FOOD_DRINKS
                - jeśli nazwa zawiera znany brand sklepu spożywczego lub restauracji, zawsze wybierz FOOD_DRINKS, nawet jeśli opis jest ogólny.
                - jeśli nie jesteś pewny → OTHER
                - zawsze przypisz kategorię
                - nie pomijaj żadnego indeksu
                
                Zwróć wyłącznie JSON zgodny ze schematem.
                """;

        String userPrompt;
        try {
            userPrompt = """
                    Sklasyfikuj poniższe transakcje.
                    Wejście JSON:
                    %s
                    """.formatted(objectMapper.writeValueAsString(indexedBatch));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize batch {} of {} for OpenAI request.", batchNumber, totalBatches, e);
            throw new RuntimeException("Cannot serialize transactions for OpenAI request", e);
        }

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model(properties.getModel())
                .messages(List.of(
                        OpenAiChatRequest.Message.builder()
                                .role("developer")
                                .content(developerPrompt)
                                .build(),
                        OpenAiChatRequest.Message.builder()
                                .role("user")
                                .content(userPrompt)
                                .build()
                ))
                .response_format(buildResponseFormat())
                .build();

        log.debug("OpenAI request for batch {}/{} prepared successfully.", batchNumber, totalBatches);

        OpenAiChatResponse response = openAiGateway.execute(request);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            log.error("Empty response from OpenAI for batch {}/{}", batchNumber, totalBatches);
            throw new RuntimeException("Empty response from OpenAI");
        }

        String content = response.getChoices().get(0).getMessage().getContent();
        log.debug("Raw OpenAI response for batch {}/{}: {}", batchNumber, totalBatches, content);

        CategorizationBatchResult batchResult;
        try {
            batchResult = objectMapper.readValue(content, CategorizationBatchResult.class);
        } catch (JsonProcessingException e) {
            log.error("Cannot parse OpenAI categorization response for batch {}/{}", batchNumber, totalBatches, e);
            throw new RuntimeException("Cannot parse OpenAI categorization response", e);
        }

        if (batchResult.getItems() == null || batchResult.getItems().isEmpty()) {
            log.error("OpenAI returned empty items list for batch {}/{}", batchNumber, totalBatches);
            throw new RuntimeException("OpenAI returned empty categorization items");
        }
        if (batchResult.getItems().size() != batch.size()) {
            log.warn("OpenAI returned {} categorized items for batch size {} (batch {}/{}).",
                    batchResult.getItems().size(),
                    batch.size(),
                    batchNumber,
                    totalBatches);
        }


        Map<Integer, Category> categoryByGlobalIndex = new LinkedHashMap<>();

        for (CategorizationBatchResult.CategorizationItem item : batchResult.getItems()) {
            if (item.getIndex() == null || item.getCategory() == null) {
                log.warn("Skipping invalid categorization item in batch {}/{}: index={}, category={}",
                        batchNumber, totalBatches, item.getIndex(), item.getCategory());
                continue;
            }

            Category parsedCategory;
            try {
                parsedCategory = Category.valueOf(item.getCategory());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown category '{}' returned by OpenAI for index {}. Falling back to OTHER.",
                        item.getCategory(), item.getIndex());
                parsedCategory = Category.OTHER;
            }

            if (categoryByGlobalIndex.containsKey(item.getIndex())) {
                log.warn("Duplicate category for global index {} in batch {}/{}. Keeping the first value.",
                        item.getIndex(), batchNumber, totalBatches);
                continue;
            }

            categoryByGlobalIndex.put(item.getIndex(), parsedCategory);
        }

        List<TransactionCategorizedByOpenAiResponse> categorized = new ArrayList<>();

        for (int localIndex = 0; localIndex < batch.size(); localIndex++) {
            MBankParsedTransaction tx = batch.get(localIndex);
            int globalIndex = globalStartIndex + localIndex;

            Category category = categoryByGlobalIndex.getOrDefault(globalIndex, Category.OTHER);

            if (!categoryByGlobalIndex.containsKey(globalIndex)) {
                log.warn("No category returned for global index {}. Falling back to OTHER.", globalIndex);
            }

            categorized.add(TransactionCategorizedByOpenAiResponse.builder()
                    .transactionDate(tx.getTransactionDate())
                    .description(tx.getDescription())
                    .title(tx.getTitle())
                    .amount(tx.getAmount())
                    .category(category)
                    .build());
        }

        return categorized;
    }

    private List<String> getCategoryEnumValues(){
        return Arrays.stream(Category.values()).map(Enum::name).toList();
    }

    private Map<String, Object> buildResponseFormat() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "items", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "index", Map.of("type", "integer"),
                                                "category", Map.of(
                                                        "type", "string",
                                                        "enum", getCategoryEnumValues()
                                                )
                                        ),
                                        "required", List.of("index", "category"),
                                        "additionalProperties", false
                                )
                        )
                ),
                "required", List.of("items"),
                "additionalProperties", false
        );

        return Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                        "name", "transaction_categorization",
                        "strict", true,
                        "schema", schema
                )
        );
    }
}