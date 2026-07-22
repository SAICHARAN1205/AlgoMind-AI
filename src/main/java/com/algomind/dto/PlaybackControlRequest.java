package com.algomind.dto;

import com.algomind.model.PlaybackAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackControlRequest {
    
    private String sessionId;
    private PlaybackAction action;
    private Double speed;
}
