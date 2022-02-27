package com.tenniscourts.reservations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.TennisCourtApplication;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TennisCourtApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @Test
    public void bookReservation() throws Exception {
        // setup
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO(1L, 1L);

        when(reservationService.bookReservation(createReservationRequestDTO))
                .thenReturn(ReservationDTO.builder().scheduledId(1L).guestId(1L).build());

        // execute
        mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void findReservationById() throws Exception {
        // setup
        long reservationId = 1L;
        when(reservationService.findReservation(reservationId)).thenReturn(ReservationDTO.builder().id(reservationId).build());

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/reservations/" + reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        ReservationDTO actualReservationDTO = objectMapper.readValue(response.getContentAsString(), ReservationDTO.class);
        assertEquals(actualReservationDTO.getId(), actualReservationDTO.getId());
    }

    @Test
    public void cancelReservationById() throws Exception {
        // setup
        long reservationId = 1L;
        when(reservationService.cancelReservation(reservationId)).thenReturn(ReservationDTO.builder().id(reservationId).build());

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                delete("/reservations/" + reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        ReservationDTO actualReservationDTO = objectMapper.readValue(response.getContentAsString(), ReservationDTO.class);
        assertEquals(actualReservationDTO.getId(), actualReservationDTO.getId());
    }

    @Test
    public void rescheduleReservation() throws Exception {
        // setup
        long previousReservationId = 1L;
        long scheduleId = 1L;
        long newScheduleId = 3L;
        when(reservationService.rescheduleReservation(previousReservationId, scheduleId)).thenReturn(ReservationDTO.builder().id(2L).scheduledId(newScheduleId).build());

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                put("/reservations/" + previousReservationId + "/" + scheduleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        ReservationDTO actualReservationDTO = objectMapper.readValue(response.getContentAsString(), ReservationDTO.class);
        assertEquals(actualReservationDTO.getId(), actualReservationDTO.getId());
        assertEquals(actualReservationDTO.getScheduledId().longValue(), newScheduleId);
    }

}