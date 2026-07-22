package com.algomind.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteRequest {
    
    private String algorithm;
    
    private String code;
    
    @NotEmpty(message = "Array must not be empty")
    private int[] array;
    
    private Integer target;
    
    public boolean isValid() {
        return (algorithm != null && !algorithm.trim().isEmpty()) || 
               (code != null && !code.trim().isEmpty());
    }
}
