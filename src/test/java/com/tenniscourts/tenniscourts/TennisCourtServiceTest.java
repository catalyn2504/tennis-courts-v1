package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TennisCourtService.class)
public class TennisCourtServiceTest {

    private static final long TENNIS_COURT_ID = 1L;

    private TennisCourtService service;

    @Mock
    private TennisCourtRepository tennisCourtRepository;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private TennisCourtMapper tennisCourtMapper;

    @Before
    public void setUp() {
        service = new TennisCourtService(tennisCourtRepository, scheduleService, tennisCourtMapper);
    }

    @Test
    public void givenTennisCourtDTOWhenAddThenCorrectDTOReturned() {
        // setup
        TennisCourtDTO inputDTO = mock(TennisCourtDTO.class);

        TennisCourt mappedTennisCourt = mock(TennisCourt.class);
        when(tennisCourtMapper.map(inputDTO)).thenReturn(mappedTennisCourt);
        TennisCourt dbTennisCourt = mock(TennisCourt.class);
        when(tennisCourtRepository.saveAndFlush(mappedTennisCourt)).thenReturn(dbTennisCourt);
        TennisCourtDTO expectedTennisCourtDTO = mock(TennisCourtDTO.class);
        when(tennisCourtMapper.map(dbTennisCourt)).thenReturn(expectedTennisCourtDTO);

        // execute
        TennisCourtDTO result = service.addTennisCourt(inputDTO);

        // verify
        assertEquals(expectedTennisCourtDTO, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNotExistingTennisCourtIdWhenGetByIdThenThrowException() {
        when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.empty());

        service.findTennisCourtById(TENNIS_COURT_ID);
    }

    @Test
    public void givenTennisCourtIdWhenGetByIdThenReturnCorrectDTO() {
        // setup
        TennisCourt dbTennisCourt = mock(TennisCourt.class);
        when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.of(dbTennisCourt));

        TennisCourtDTO expectedTennisCourtDTO = mock(TennisCourtDTO.class);
        when(tennisCourtMapper.map(dbTennisCourt)).thenReturn(expectedTennisCourtDTO);

        // execute
        TennisCourtDTO result = service.findTennisCourtById(TENNIS_COURT_ID);

        // verify
        assertEquals(expectedTennisCourtDTO, result);
    }

    @Test
    public void givenTennisCourtIdWhenGetWithSchedulesThenReturnCorrectDTO() {
        // setup
        TennisCourt dbTennisCourt =  mock(TennisCourt.class);
        when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.of(dbTennisCourt));

        TennisCourtDTO expectedTennisCourtDTO = mock(TennisCourtDTO.class);
        when(tennisCourtMapper.map(dbTennisCourt)).thenReturn(expectedTennisCourtDTO);

        List<ScheduleDTO> scheduleDTOs = Arrays.asList(mock(ScheduleDTO.class), mock(ScheduleDTO.class));
        when(scheduleService.findSchedulesByTennisCourtId(TENNIS_COURT_ID)).thenReturn(scheduleDTOs);

        // execute
        TennisCourtDTO result = service.findTennisCourtWithSchedulesById(TENNIS_COURT_ID);

        // verify
        assertEquals(expectedTennisCourtDTO, result);
        verify(expectedTennisCourtDTO).setTennisCourtSchedules(scheduleDTOs);
    }
}