package com.tenniscourts.schedules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.TennisCourtApplication;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TennisCourtApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    public void findNonBookedSchedulesByDate() throws Exception {
        // setup
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2020, 3, 1, 0, 0);

        ScheduleDTO scheduleDTO1 = new ScheduleDTO();
        ScheduleDTO scheduleDTO2 = new ScheduleDTO();
        when(scheduleService.findNonBookedSchedulesByDates(startDateTime, endDateTime)).thenReturn(Arrays.asList(scheduleDTO1, scheduleDTO2));

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/schedules?startDate=2022-03-20&&endDate=2022-03-22")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    @Test
    public void findScheduleById() throws Exception {
        // setup
        long scheduleId = 1L;
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(scheduleId);
        when(scheduleService.findSchedule(scheduleId)).thenReturn(scheduleDTO);

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/schedules/" + scheduleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        ScheduleDTO actualScheduleDTO = objectMapper.readValue(response.getContentAsString(), ScheduleDTO.class);
        assertEquals(actualScheduleDTO.getId(), actualScheduleDTO.getId());
    }

}