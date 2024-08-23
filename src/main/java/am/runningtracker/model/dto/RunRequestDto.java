package am.runningtracker.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    @NotNull
    private LocalDateTime dateTime;

    private Double distance;
}