package com.tenniscourts.tenniscourts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.TennisCourtApplication;
import com.tenniscourts.schedules.ScheduleDTO;
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

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TennisCourtApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TennisCourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TennisCourtService tennisCourtService;

    @Test
    public void findTennisCourtById() throws Exception {
        // setup
        long tennisCourtId = 1L;
        TennisCourtDTO tennisCourtDTO = TennisCourtDTO.builder().id(tennisCourtId).build();

        when(tennisCourtService.findTennisCourtById(tennisCourtId)).thenReturn(tennisCourtDTO);

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/tenniscourts/" + tennisCourtId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        TennisCourtDTO actualTennisCourt = objectMapper.readValue(response.getContentAsString(), TennisCourtDTO.class);
        assertEquals(actualTennisCourt.getId(), actualTennisCourt.getId());
    }

    @Test
    public void findTennisCourtWithSchedulesById() throws Exception {
        // setup
        long tennisCourtId = 1L;
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(2L);
        TennisCourtDTO tennisCourtDTO = TennisCourtDTO.builder().id(tennisCourtId).tennisCourtSchedules(singletonList(scheduleDTO)).build();

        when(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId)).thenReturn(tennisCourtDTO);

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/tenniscourts/" + tennisCourtId + "/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        TennisCourtDTO actualTennisCourt = objectMapper.readValue(response.getContentAsString(), TennisCourtDTO.class);
        assertEquals(actualTennisCourt.getId(), actualTennisCourt.getId());
    }
}