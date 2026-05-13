package com.benedykt.budget_control.openAiApi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategorizationBatchResult {

    private List<CategorizationItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CategorizationItem {
        private Integer index;
        private String category;
    }
}
