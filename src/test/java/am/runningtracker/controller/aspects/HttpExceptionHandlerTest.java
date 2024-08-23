package am.runningtracker.controller.aspects;

import am.runningtracker.exception.AppException;
import am.runningtracker.controller.RunController;
import am.runningtracker.service.RunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RunController.class)
public class HttpExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RunService runService;

    @BeforeEach
    public void setup() {
        Mockito.when(runService.startRun(any())).thenThrow(new AppException("RunException", "Unable to start run"));
    }

    @Test
    public void testAppExceptionHandlingForStartRun() throws Exception {
        mockMvc.perform(post("/runs/start").contentType(MediaType.APPLICATION_JSON).content("{ \"userId\": 1, \"latitude\": 40.1772, \"longitude\": 44.5034, \"dateTime\": \"2023-08-22T10:15:30\" }")).andExpect(status().isBadRequest()) // Expecting a bad request status
                .andExpect(jsonPath("$.errorType").value("RunException")).andExpect(jsonPath("$.errorMessage").value("Unable to start run"));
    }
}