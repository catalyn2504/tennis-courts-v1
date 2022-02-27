package com.tenniscourts.guests;

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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TennisCourtApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GuestService guestService;

    @Test
    public void createGuest() throws Exception {
        // setup
        CreateGuestRequestDTO createGuestRequestDTO = new CreateGuestRequestDTO();
        String inputName = "testName";
        createGuestRequestDTO.setName(inputName);

        when(guestService.createGuest(createGuestRequestDTO)).thenReturn(GuestDTO.builder().id(1L).name(inputName).build());

        // execute
        mockMvc.perform(
                post("/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGuestRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
    }

    @Test
    public void updateGuest() throws Exception {
        // setup
        UpdateGuestRequestDTO updateGuestRequestDTO = new UpdateGuestRequestDTO();
        String newName = "newName";
        updateGuestRequestDTO.setName(newName);

        long guestId = 1L;
        when(guestService.updateGuest(guestId, updateGuestRequestDTO)).thenReturn(GuestDTO.builder().id(guestId).name(newName).build());

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                patch("/guests/" + guestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGuestRequestDTO))).andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        GuestDTO actualGuestDTO = objectMapper.readValue(response.getContentAsString(), GuestDTO.class);
        assertEquals(newName, actualGuestDTO.getName());
    }

    @Test
    public void deleteGuest() throws Exception {
        long guestId = 1L;
        mockMvc.perform(
                delete("/guests/" + guestId)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent())
                .andReturn().getResponse();
    }

    @Test
    public void findGuestById() throws Exception {
        // setup
        long guestId = 1L;
        String guestName = "testName";
        when(guestService.findGuest(guestId)).thenReturn(GuestDTO.builder().id(guestId).name(guestName).build());

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/guests/" + guestId)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        GuestDTO actualGuestDTO = objectMapper.readValue(response.getContentAsString(), GuestDTO.class);
        assertEquals(guestName, actualGuestDTO.getName());
    }

    @Test
    public void findGuestByName() throws Exception {
        // setup
        long guestId = 1L;
        String searchedName = "testName";
        when(guestService.getByName(searchedName)).thenReturn(GuestDTO.builder().id(guestId).name(searchedName).build());

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/guests/filter?name=" + searchedName)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        GuestDTO actualGuestDTO = objectMapper.readValue(response.getContentAsString(), GuestDTO.class);
        assertEquals(searchedName, actualGuestDTO.getName());
    }

    @Test
    public void findAllGuests() throws Exception {
        // setup
        GuestDTO guestDTO1 = GuestDTO.builder().id(1L).name("name1").build();
        GuestDTO guestDTO2 = GuestDTO.builder().id(2L).name("name2").build();
        List<GuestDTO> guestDTOs = Arrays.asList(guestDTO1, guestDTO2);
        when(guestService.getAll()).thenReturn(guestDTOs);

        // execute
        MockHttpServletResponse response = mockMvc.perform(
                get("/guests")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse();

        // verify
        List actualResponseObjects = objectMapper.readValue(response.getContentAsString(), List.class);
        assertEquals(2, actualResponseObjects.size());
    }
}