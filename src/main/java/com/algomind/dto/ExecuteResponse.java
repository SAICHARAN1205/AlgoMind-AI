package com.algomind.dto;

import com.algomind.timeline.ExecutionTimeline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteResponse {
    private ExecutionTimeline timeline;
}
