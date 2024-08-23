package am.runningtracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDto {

    private Long userId;
    private int numberOfRuns;
    private double totalMeters;
    private double averageSpeed;
}
