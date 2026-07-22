package com.algomind.ai.dto;

import lombok.Data;

@Data
public class AIRequest {
    private String algorithmType;
    private String userCode;
    private String errorMessage;
}
