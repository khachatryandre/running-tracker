package am.runningtracker.service;

import am.runningtracker.exception.InvalidRunOperationException;
import am.runningtracker.exception.UserNotFoundException;
import am.runningtracker.model.Run;
import am.runningtracker.model.User;
import am.runningtracker.model.dto.RunRequestDto;
import am.runningtracker.model.dto.RunResponseDto;
import am.runningtracker.model.dto.UserStatisticsDto;
import am.runningtracker.repo.RunRepository;
import am.runningtracker.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RunServiceTest {

    @Mock
    private RunRepository runRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RunService runService;

    private User testUser;
    private Run ongoingRun;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Test User");

        ongoingRun = new Run();
        ongoingRun.setId(1L);
        ongoingRun.setUser(testUser);
        ongoingRun.setStartLatitude(40.0);
        ongoingRun.setStartLongitude(45.0);
        ongoingRun.setStartDateTime(LocalDateTime.now().minusMinutes(30));
        ongoingRun.setFinishLatitude(50.0);
        ongoingRun.setFinishLongitude(55.0);
        ongoingRun.setFinishDateTime(LocalDateTime.now());
        ongoingRun.setDistance(5000.0);
        ongoingRun.setStatus("completed");
    }

    @Test
    void testStartRunSuccessfully() {
        RunRequestDto runRequest = new RunRequestDto(1L, 40.0, 45.0, LocalDateTime.now(), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(runRepository.findByUserIdAndStatus(1L, "ongoing")).thenReturn(Optional.empty());

        ResponseEntity<String> response = runService.startRun(runRequest);

        assertEquals("Run started successfully.", response.getBody());
        verify(runRepository, times(1)).save(any(Run.class));
    }

    @Test
    void testStartRunUserNotFound() {
        RunRequestDto runRequest = new RunRequestDto(1L, 40.0, 45.0, LocalDateTime.now(), null);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> runService.startRun(runRequest));
    }

    @Test
    void testStartRunWithOngoingRun() {
        RunRequestDto runRequest = new RunRequestDto(1L, 40.0, 45.0, LocalDateTime.now(), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(runRepository.findByUserIdAndStatus(1L, "ongoing")).thenReturn(Optional.of(ongoingRun));

        assertThrows(InvalidRunOperationException.class, () -> runService.startRun(runRequest));
    }

    @Test
    void testFinishRunSuccessfully() {
        RunRequestDto runRequest = new RunRequestDto(1L, 50.0, 55.0, LocalDateTime.now().plusMinutes(30), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(runRepository.findByUserIdAndStatus(1L, "ongoing")).thenReturn(Optional.of(ongoingRun));

        ResponseEntity<String> response = runService.finishRun(runRequest);

        assertEquals("Run finished successfully.", response.getBody());
        verify(runRepository, times(1)).save(ongoingRun);
    }

    @Test
    void testFinishRunUserNotFound() {
        RunRequestDto runRequest = new RunRequestDto(1L, 50.0, 55.0, LocalDateTime.now().plusMinutes(30), null);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> runService.finishRun(runRequest));
    }

    @Test
    void testFinishRunNoOngoingRun() {
        RunRequestDto runRequest = new RunRequestDto(1L, 50.0, 55.0, LocalDateTime.now().plusMinutes(30), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(runRepository.findByUserIdAndStatus(1L, "ongoing")).thenReturn(Optional.empty());

        assertThrows(InvalidRunOperationException.class, () -> runService.finishRun(runRequest));
    }

    @Test
    void testGetRunsForUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(runRepository.findByUserId(1L)).thenReturn(List.of(ongoingRun));

        List<RunResponseDto> runs = runService.getRunsForUser(1L, null, null);

        assertEquals(1, runs.size());
    }

    @Test
    void testGetRunsForUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> runService.getRunsForUser(1L, null, null));
    }

    @Test
    void testGetUserStatisticsSuccessfully() {
        ongoingRun.setStatus("completed");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(runRepository.findByUserId(1L)).thenReturn(List.of(ongoingRun));

        UserStatisticsDto statistics = runService.getUserStatistics(1L, null, null);

        assertEquals(1, statistics.getNumberOfRuns());
        assertEquals(ongoingRun.getDistance(), statistics.getTotalMeters());
    }

    @Test
    void testGetUserStatisticsUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> runService.getUserStatistics(1L, null, null));
    }
}