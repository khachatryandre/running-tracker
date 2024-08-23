package am.runningtracker.controller;

import am.runningtracker.model.dto.RunRequestDto;
import am.runningtracker.model.dto.RunResponseDto;
import am.runningtracker.model.dto.UserStatisticsDto;
import am.runningtracker.service.RunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/runs")
public class RunController {
    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startRun(@RequestBody RunRequestDto runRequestDto) {
        return runService.startRun(runRequestDto);
    }

    @PostMapping("/finish")
    public ResponseEntity<String> finishRun(@RequestBody RunRequestDto runRequestDto) {
        return runService.finishRun(runRequestDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<RunResponseDto>> getRunsForUser(@PathVariable Long userId,
                                                               @RequestParam(required = false) LocalDateTime from,
                                                               @RequestParam(required = false) LocalDateTime to) {
        return ResponseEntity.ok(runService.getRunsForUser(userId, from, to));
    }

    @GetMapping("/{userId}/statistics")
    public ResponseEntity<UserStatisticsDto> getUserStatistics(@PathVariable Long userId,
                                                               @RequestParam(required = false) LocalDateTime from,
                                                               @RequestParam(required = false) LocalDateTime to) {
        UserStatisticsDto statistics = runService.getUserStatistics(userId, from, to);
        return ResponseEntity.ok(statistics);
    }
}