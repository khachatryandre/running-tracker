package am.runningtracker.controller;

import am.runningtracker.model.dto.RunRequestDto;
import am.runningtracker.model.dto.RunResponseDto;
import am.runningtracker.model.dto.UserStatisticsDto;
import am.runningtracker.service.RunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RunController.class)
class RunControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RunService runService;

    private RunResponseDto runResponseDto;

    @BeforeEach
    void setUp() {
        runResponseDto = new RunResponseDto();
    }

    @Test
    void testStartRun() throws Exception {
        when(runService.startRun(any(RunRequestDto.class))).thenReturn(ResponseEntity.ok("Run started successfully."));

        mockMvc.perform(post("/runs/start").contentType(MediaType.APPLICATION_JSON).content("{ \"userId\": 1, \"latitude\": 40.0, \"longitude\": 45.0, \"dateTime\": \"2024-08-23T14:00:00\" }")).andExpect(status().isOk()).andExpect(content().string("Run started successfully."));
    }

    @Test
    void testFinishRun() throws Exception {
        when(runService.finishRun(any(RunRequestDto.class))).thenReturn(ResponseEntity.ok("Run finished successfully."));

        mockMvc.perform(post("/runs/finish").contentType(MediaType.APPLICATION_JSON).content("{ \"userId\": 1, \"latitude\": 50.0, \"longitude\": 55.0, \"dateTime\": \"2024-08-23T15:00:00\" }")).andExpect(status().isOk()).andExpect(content().string("Run finished successfully."));
    }

    @Test
    void testGetRunsForUser() throws Exception {
        when(runService.getRunsForUser(1L, null, null)).thenReturn(List.of(runResponseDto));

        mockMvc.perform(get("/runs/1")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetUserStatistics() throws Exception {
        UserStatisticsDto userStatisticsDto = new UserStatisticsDto(1L, 5, 10000.0, 10.0);
        when(runService.getUserStatistics(1L, null, null)).thenReturn(userStatisticsDto);

        mockMvc.perform(get("/runs/1/statistics")).andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(1L)).andExpect(jsonPath("$.numberOfRuns").value(5)).andExpect(jsonPath("$.totalMeters").value(10000.0)).andExpect(jsonPath("$.averageSpeed").value(10.0));
    }
}