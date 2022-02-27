package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.Reservation;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ScheduleService.class)
public class ScheduleServiceTest {

    private static final long TENNIS_COURT_ID = 1L;
    private static final long SCHEDULE_ID = 2L;

    private ScheduleService service;

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private TennisCourtRepository tennisCourtRepository;

    @Before
    public void setUp() {
        service = new ScheduleService(scheduleRepository,scheduleMapper, tennisCourtRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void throwExceptionWhenAddingScheduleToNotExistingTennisCourt() {
        // setup
        when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.empty());

        CreateScheduleRequestDTO createScheduleRequestDTO = mock(CreateScheduleRequestDTO.class);
        when(createScheduleRequestDTO.getStartDateTime()).thenReturn(LocalDateTime.now());

        // execute
        service.addSchedule(TENNIS_COURT_ID, createScheduleRequestDTO);
    }

    @Test
    public void addScheduleToTennisCourt() {
        // setup
        TennisCourt dbTennisCourt = mock(TennisCourt.class);
        when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.of(dbTennisCourt));

        CreateScheduleRequestDTO createScheduleRequestDTO = mock(CreateScheduleRequestDTO.class);
        LocalDateTime startDateTime = LocalDateTime.of(2020, 10, 11, 20, 0, 0);
        when(createScheduleRequestDTO.getStartDateTime()).thenReturn(startDateTime);

        LocalDateTime endDateTime = startDateTime.plusHours(1);
        Schedule dbSchedule = Schedule.builder().tennisCourt(dbTennisCourt).startDateTime(startDateTime).endDateTime(endDateTime).build();
        when(scheduleRepository.save(any())).thenReturn(dbSchedule);

        ScheduleDTO expectedScheduleDTO = mock(ScheduleDTO.class);
        when(scheduleMapper.map(dbSchedule)).thenReturn(expectedScheduleDTO);

        // execute
        ScheduleDTO result = service.addSchedule(TENNIS_COURT_ID, createScheduleRequestDTO);

        // verify
        assertEquals(expectedScheduleDTO, result);
    }

    @Test
    public void returnNonBookedSchedulesBetweenDates() {
        // setup
        LocalDateTime startDateTime = LocalDateTime.of(2020,2, 1, 12, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2020,3, 1, 12, 0, 0);

        Schedule schedule1 = Schedule.builder().startDateTime(startDateTime.minusDays(1)).endDateTime(endDateTime).reservations(new ArrayList<>()).build();
        Schedule schedule2 = Schedule.builder().startDateTime(startDateTime.plusDays(1)).endDateTime(endDateTime.minusDays(1))
                .reservations(new ArrayList<>()).build();
        Schedule schedule3 = Schedule.builder().startDateTime(startDateTime.plusDays(2)).endDateTime(endDateTime.minusDays(2))
                .reservations(Arrays.asList(new Reservation(), new Reservation())).build();

        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule1, schedule2, schedule3));

        ScheduleDTO scheduleDTO2 = mock(ScheduleDTO.class);
        when(scheduleMapper.map(schedule2)).thenReturn(scheduleDTO2);

        // execute
        List<ScheduleDTO> result = service.findNonBookedSchedulesByDates(startDateTime, endDateTime);

        // verify
        assertEquals(Collections.singletonList(scheduleDTO2), result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNonExistingScheduleIdWhenGetByIdThenThrowException() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        service.findSchedule(1L);
    }

    @Test
    public void givenScheduleIdWhenGetByIdThenReturnCorrectScheduleDTO() {
        // setup
        Schedule dbSchedule = mock(Schedule.class);
        when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.of(dbSchedule));
        ScheduleDTO expectedScheduleDTO = mock(ScheduleDTO.class);
        when(scheduleMapper.map(dbSchedule)).thenReturn(expectedScheduleDTO);

        // execute
        ScheduleDTO result = service.findSchedule(SCHEDULE_ID);

        // verify
        assertEquals(expectedScheduleDTO, result);
    }

    @Test
    public void givenTennisCourtIdWhenGetThenReturnCorrectScheduleDTOs() {
        // setup
        List<Schedule> dbSchedules = Arrays.asList(mock(Schedule.class), mock(Schedule.class));
        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(TENNIS_COURT_ID)).thenReturn(dbSchedules);

        List<ScheduleDTO> expectedScheduleDTOs = Arrays.asList(mock(ScheduleDTO.class), mock(ScheduleDTO.class));
        when(scheduleMapper.map(dbSchedules)).thenReturn(expectedScheduleDTOs);

        // execute
        List<ScheduleDTO> result = service.findSchedulesByTennisCourtId(TENNIS_COURT_ID);

        // verify
        assertEquals(expectedScheduleDTOs, result);
    }
}