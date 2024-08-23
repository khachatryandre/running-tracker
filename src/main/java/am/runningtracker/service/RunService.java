package am.runningtracker.service;

import am.runningtracker.exception.InvalidRunOperationException;
import am.runningtracker.exception.UserNotFoundException;
import am.runningtracker.model.Run;
import am.runningtracker.model.dto.RunRequestDto;
import am.runningtracker.model.dto.RunResponseDto;
import am.runningtracker.model.User;
import am.runningtracker.model.dto.UserStatisticsDto;
import am.runningtracker.repo.RunRepository;
import am.runningtracker.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RunService {
    private final RunRepository runRepository;
    private final UserRepository userRepository;

    public RunService(RunRepository runRepository, UserRepository userRepository) {
        this.runRepository = runRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> startRun(RunRequestDto runRequestDto) {
        User user = validateUserExists(runRequestDto.getUserId());

        if (runRepository.findByUserIdAndStatus(user.getId(), "ongoing").isPresent()) {
            throw new InvalidRunOperationException("You already have an ongoing run.");
        }

        validateRunRequest(runRequestDto);

        Run newRun = new Run();
        newRun.setUser(user);
        newRun.setStartLatitude(runRequestDto.getLatitude());
        newRun.setStartLongitude(runRequestDto.getLongitude());
        newRun.setStartDateTime(runRequestDto.getDateTime());
        newRun.setStatus("ongoing");

        runRepository.save(newRun);

        return ResponseEntity.ok("Run started successfully.");
    }

    public ResponseEntity<String> finishRun(RunRequestDto runRequestDto) {
        User user = validateUserExists(runRequestDto.getUserId());

        Run ongoingRun = runRepository.findByUserIdAndStatus(user.getId(), "ongoing").orElseThrow(() -> new InvalidRunOperationException("No ongoing run found for the user."));

        validateFinishRun(runRequestDto, ongoingRun);

        ongoingRun.setFinishLatitude(runRequestDto.getLatitude());
        ongoingRun.setFinishLongitude(runRequestDto.getLongitude());
        ongoingRun.setFinishDateTime(runRequestDto.getDateTime());

        if (runRequestDto.getDistance() == null) {
            double calculatedDistance = calculateDistance(ongoingRun.getStartLatitude(), ongoingRun.getStartLongitude(), runRequestDto.getLatitude(), runRequestDto.getLongitude());
            ongoingRun.setDistance(calculatedDistance);
        } else {
            ongoingRun.setDistance(runRequestDto.getDistance());
        }

        ongoingRun.setStatus("completed");
        runRepository.save(ongoingRun);

        return ResponseEntity.ok("Run finished successfully.");
    }

    public List<RunResponseDto> getRunsForUser(Long userId, LocalDateTime from, LocalDateTime to) {
        validateUserExists(userId);

        List<Run> runs = (from != null && to != null) ? runRepository.findByUserIdAndStartDateTimeBetween(userId, from, to) : runRepository.findByUserId(userId);

        return runs.stream().map(this::mapToRunResponseDto).collect(Collectors.toList());
    }

    public UserStatisticsDto getUserStatistics(Long userId, LocalDateTime from, LocalDateTime to) {
        validateUserExists(userId);

        List<Run> completedRuns = (from != null && to != null) ? runRepository.findByUserIdAndStartDateTimeBetween(userId, from, to).stream().filter(run -> "completed".equals(run.getStatus())).toList() : runRepository.findByUserId(userId).stream().filter(run -> "completed".equals(run.getStatus())).toList();

        return calculateUserStatistics(userId, completedRuns);
    }

    private User validateUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private void validateRunRequest(RunRequestDto runRequestDto) {
        if (runRequestDto.getLatitude() == 0.0 || runRequestDto.getLongitude() == 0.0) {
            throw new InvalidRunOperationException("Latitude or Longitude cannot be zero.");
        }
        if (runRequestDto.getDateTime() == null) {
            throw new InvalidRunOperationException("Start date and time are required.");
        }
    }

    private void validateFinishRun(RunRequestDto runRequestDto, Run ongoingRun) {
        if (runRequestDto.getLatitude() == 0.0 || runRequestDto.getLongitude() == 0.0) {
            throw new InvalidRunOperationException("Latitude or Longitude cannot be zero.");
        }
        if (runRequestDto.getDateTime().isBefore(ongoingRun.getStartDateTime())) {
            throw new InvalidRunOperationException("Finish datetime should be later than start datetime.");
        }
    }

    private double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        double earthRadius = 6371e3; // Earth's radius in meters
        double dLat = Math.toRadians(endLat - startLat);
        double dLng = Math.toRadians(endLng - startLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    private RunResponseDto mapToRunResponseDto(Run run) {
        RunResponseDto dto = new RunResponseDto();
        dto.setRunId(run.getId());
        dto.setStartLatitude(run.getStartLatitude());
        dto.setStartLongitude(run.getStartLongitude());
        dto.setFinishLatitude(run.getFinishLatitude());
        dto.setFinishLongitude(run.getFinishLongitude());
        dto.setDistance(run.getDistance());
        dto.setStartDateTime(run.getStartDateTime());
        dto.setFinishDateTime(run.getFinishDateTime());
        dto.setStatus(run.getStatus());

        if (run.getDistance() != null && run.getFinishDateTime() != null && run.getStartDateTime() != null) {
            long durationInSeconds = java.time.Duration.between(run.getStartDateTime(), run.getFinishDateTime()).getSeconds();
            double timeInHours = durationInSeconds / 3600.0;

            if (timeInHours > 0) {
                dto.setAverageSpeed(run.getDistance() / timeInHours);
            } else {
                dto.setAverageSpeed(0.0);
            }
        } else {
            dto.setAverageSpeed(0.0);
        }

        return dto;
    }

    private UserStatisticsDto calculateUserStatistics(Long userId, List<Run> completedRuns) {
        int numberOfRuns = completedRuns.size();
        double totalMeters = completedRuns.stream().mapToDouble(Run::getDistance).sum();
        double totalTimeInHours = completedRuns.stream().filter(run -> run.getFinishDateTime() != null && run.getStartDateTime() != null).mapToDouble(run -> {
            long durationInSeconds = java.time.Duration.between(run.getStartDateTime(), run.getFinishDateTime()).getSeconds();
            return durationInSeconds / 3600.0;
        }).sum();

        double averageSpeed = (totalTimeInHours > 0) ? (totalMeters / totalTimeInHours) : 0.0;

        UserStatisticsDto statistics = new UserStatisticsDto();
        statistics.setUserId(userId);
        statistics.setNumberOfRuns(numberOfRuns);
        statistics.setTotalMeters(totalMeters);
        statistics.setAverageSpeed(averageSpeed);

        return statistics;
    }
}