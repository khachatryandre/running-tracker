package am.runningtracker.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RunResponseDto {
    private Long runId;
    private Double startLatitude;
    private Double startLongitude;
    private Double finishLatitude;
    private Double finishLongitude;
    private Double distance;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private String status;
    private Double averageSpeed;
}