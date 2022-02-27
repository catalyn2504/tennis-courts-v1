package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = GuestService.class)
public class GuestServiceTest {

    private static final long GUEST_ID = 1L;
    private static final String GUEST_NAME = "guestName";
    private static final String UPDATED_GUEST_NAME = "updatedGuestName";

    private GuestService service;

    @Mock
    private GuestRepository guestRepository;
    @Mock
    private GuestMapper guestMapper;

    @Before
    public void setUp() {
        service = new GuestService(guestRepository, guestMapper);
    }

    @Test
    public void givenCreateGuestRequestDTOWhenCreateThenGuestDTOReturned() {
        // setup
        CreateGuestRequestDTO createGuestRequestDTO = CreateGuestRequestDTO.builder().name(GUEST_NAME).build();

        Guest guest = mock(Guest.class);
        when(guestMapper.map(createGuestRequestDTO)).thenReturn(guest);
        Guest dbGuest = mock(Guest.class);
        when(guestRepository.save(guest)).thenReturn(dbGuest);
        GuestDTO expectedGuestDTO = mock(GuestDTO.class);
        when(guestMapper.map(dbGuest)).thenReturn(expectedGuestDTO);

        // execute
        GuestDTO result = service.createGuest(createGuestRequestDTO);

        // verify
        assertEquals(expectedGuestDTO, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenUpdateGuestRequestDTOAndGuestNotFoundThenThrowException() {
        // setup
        UpdateGuestRequestDTO updateGuestRequestDTO = mock(UpdateGuestRequestDTO.class);

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        // execute
        service.updateGuest(GUEST_ID , updateGuestRequestDTO);
    }

    @Test
    public void givenUpdateGuestRequestDTOWhenUpdateThenCorrectGuestDTOReturned() {
        // setup
        UpdateGuestRequestDTO updateGuestRequestDTO = UpdateGuestRequestDTO.builder().name(UPDATED_GUEST_NAME).build();

        Guest existingDbGuest = mock(Guest.class);
        Optional dbGuestOptional = Optional.of(existingDbGuest);
        when(guestRepository.findById(GUEST_ID)).thenReturn(dbGuestOptional);

        Guest expectedUpdatedGuest = Guest.builder().name(UPDATED_GUEST_NAME).build();
        when(guestRepository.save(existingDbGuest)).thenReturn(expectedUpdatedGuest);

        GuestDTO expectedResult = GuestDTO.builder().name(UPDATED_GUEST_NAME).build();
        when(guestMapper.map(expectedUpdatedGuest)).thenReturn(expectedResult);

        // execute
        GuestDTO result = service.updateGuest(GUEST_ID, updateGuestRequestDTO);

        // verify
        assertEquals(expectedResult, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenGuestIdWhenDeleteAndGuestNotFoundThenThrowException() {
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        service.deleteGuest(GUEST_ID);
    }

    @Test
    public void givenExistingGuestIdWhenDeleteThenSuccess() {
        // setup
        Guest existingDbGuest = mock(Guest.class);
        when(existingDbGuest.getId()).thenReturn(GUEST_ID);
        Optional dbGuestOptional = Optional.of(existingDbGuest);
        when(guestRepository.findById(GUEST_ID)).thenReturn(dbGuestOptional);

        // execute
        service.deleteGuest(GUEST_ID);

        // verify
        verify(guestRepository).deleteById(GUEST_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNotExistingGuestIdWhenFindByIdThenThrowException() {
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        service.findGuest(GUEST_ID);
    }

    @Test
    public void givenExistingGuestIdWhenFindByIdThenCorrectGuestDTOReturned() {
        // setup
        Guest existingDbGuest = mock(Guest.class);
        Optional dbGuestOptional = Optional.of(existingDbGuest);
        when(guestRepository.findById(GUEST_ID)).thenReturn(dbGuestOptional);

        GuestDTO expectedResult = mock(GuestDTO.class);
        when(guestMapper.map(existingDbGuest)).thenReturn(expectedResult);

        // execute
        GuestDTO result = service.findGuest(GUEST_ID);

        // verify
        assertEquals(expectedResult, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNotExistingGuestNameWhenFindByNameThenThrowException() {
        when(guestRepository.findByName(GUEST_NAME)).thenReturn(Optional.empty());

        service.getByName(GUEST_NAME);
    }

    @Test
    public void givenExistingGuestNameWhenFindByNameThenCorrectGuestDTOReturned() {
        // setup
        Guest existingDbGuest = mock(Guest.class);
        Optional dbGuestOptional = Optional.of(existingDbGuest);
        when(guestRepository.findByName(GUEST_NAME)).thenReturn(dbGuestOptional);

        GuestDTO expectedResult = mock(GuestDTO.class);
        when(guestMapper.map(existingDbGuest)).thenReturn(expectedResult);

        // execute
        GuestDTO result = service.getByName(GUEST_NAME);

        // verify
        assertEquals(expectedResult, result);
    }

    @Test
    public void whenGetAllGuestsThenReturnCorrectGuestDTOs() {
        // setup
        Guest dbGuest1 = mock(Guest.class);
        Guest dbGuest2 = mock(Guest.class);
        List<Guest> dbGuests = Arrays.asList(dbGuest1, dbGuest2);
        when(guestRepository.findAll()).thenReturn(dbGuests);

        GuestDTO guestDTO1 = mock(GuestDTO.class);
        when(guestMapper.map(dbGuest1)).thenReturn(guestDTO1);
        GuestDTO guestDTO2 = mock(GuestDTO.class);
        when(guestMapper.map(dbGuest2)).thenReturn(guestDTO2);
        List<GuestDTO> expectedGuestDTOs = Arrays.asList(guestDTO1, guestDTO2);

        // execute
        List<GuestDTO> result = service.getAll();

        // verify
        assertEquals(expectedGuestDTOs, result);
    }
}