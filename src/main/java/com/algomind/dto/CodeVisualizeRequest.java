package com.algomind.dto;

import lombok.Data;

@Data
public class CodeVisualizeRequest {
    private String language;
    private String code;
    private String manualAlgorithm;
}
