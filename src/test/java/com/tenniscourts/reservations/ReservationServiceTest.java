package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {

    private static final long GUEST_ID = 1L;
    private static final long SCHEDULE_ID = 2L;
    private static final long RESERVATION_ID = 3L;

    private ReservationService service;

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private GuestRepository guestRepository;

    @Before
    public void setUp() {
        service = new ReservationService(reservationRepository, reservationMapper, scheduleRepository, guestRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenCreateReservationRequestDTOAndNotExistingGuestThenThrowException() {
        // setup
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(GUEST_ID).scheduleId(SCHEDULE_ID).build();

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        // execute
        service.bookReservation(createReservationRequestDTO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenCreateReservationRequestDTOAndNotExistingScheduleThenThrowException() {
        // setup
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(GUEST_ID).scheduleId(SCHEDULE_ID).build();

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(mock(Guest.class)));
        when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.empty());

        // execute
        service.bookReservation(createReservationRequestDTO);
    }

    @Test(expected = AlreadyExistsEntityException.class)
    public void givenCreateReservationRequestDTOAndScheduleIsAlreadyBookedThenThrowException() {
        // setup
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(GUEST_ID).scheduleId(SCHEDULE_ID).build();

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(mock(Guest.class)));
        Schedule dbSchedule = mock(Schedule.class);
        when(dbSchedule.getReservations()).thenReturn(Collections.singletonList(mock(Reservation.class)));
        when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.of(dbSchedule));

        // execute
        service.bookReservation(createReservationRequestDTO);
    }

    @Test
    public void givenCreateReservationRequestDTOWhenBookReservationThenReservationDTOReturned() {
        // setup
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(GUEST_ID).scheduleId(SCHEDULE_ID).build();

        ReservationDTO expectedReservationDTO = setupSuccessfullyBookedReservation();

        // execute
        ReservationDTO result = service.bookReservation(createReservationRequestDTO);

        // verify
        assertEquals(expectedReservationDTO, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNotExistingReservationIdWhenFindByIdThenThrowException() {
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        service.findReservation(RESERVATION_ID);
    }

    @Test
    public void givenExistingReservationIdWhenFindByIdThenCorrectReservationDTOReturned() {
        // setup
        Reservation dbReservation = mock(Reservation.class);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        ReservationDTO expectedReservationDTO = mock(ReservationDTO.class);
        when(reservationMapper.map(dbReservation)).thenReturn(expectedReservationDTO);

        // execute
        ReservationDTO result = service.findReservation(RESERVATION_ID);

        // verify
        assertEquals(expectedReservationDTO, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNotExistingReservationIdWhenCancelThenThrowException() {
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        service.cancelReservation(RESERVATION_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenExistingReservationIdAndReservationNotInReadyToPlayStateWhenCancelThenThrowException() {
        Reservation dbReservation = mock(Reservation.class);
        when(dbReservation.getReservationStatus()).thenReturn(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        service.cancelReservation(RESERVATION_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenExistingReservationIdAndReservationStartDateInThePastWhenCancelThenThrowException() {
        // setup
        Reservation dbReservation = mock(Reservation.class);
        when(dbReservation.getReservationStatus()).thenReturn(ReservationStatus.READY_TO_PLAY);

        Schedule dbSchedule = mock(Schedule.class);
        when(dbReservation.getSchedule()).thenReturn(dbSchedule);
        when(dbSchedule.getStartDateTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        // execute
        service.cancelReservation(RESERVATION_ID);
    }

    @Test
    public void givenExistingReservationIdWhenCancelThenCorrectReservationDTOReturned() {
        // setup
        Reservation dbReservation = mock(Reservation.class);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        when(dbReservation.getReservationStatus()).thenReturn(ReservationStatus.READY_TO_PLAY);
        when(dbReservation.getValue()).thenReturn(BigDecimal.TEN);
        Schedule dbSchedule = mock(Schedule.class);
        when(dbReservation.getSchedule()).thenReturn(dbSchedule);
        when(dbSchedule.getStartDateTime()).thenReturn(LocalDateTime.now().plusDays(1));

        Reservation actualSavedReservation = mock(Reservation.class);
        when(reservationRepository.save(dbReservation)).thenReturn(actualSavedReservation);

        ReservationDTO expectedReservationDTO = mock(ReservationDTO.class);
        when(reservationMapper.map(actualSavedReservation)).thenReturn(expectedReservationDTO);

        // execute
        ReservationDTO result = service.cancelReservation(RESERVATION_ID);

        // verify
        verify(dbReservation).setReservationStatus(ReservationStatus.CANCELLED);
        assertEquals(expectedReservationDTO, result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void givenNotExistingReservationIdWhenRescheduleThenThrowException() {
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        service.rescheduleReservation(RESERVATION_ID, SCHEDULE_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenExistingReservationIdAndReservationNotInReadyToPlayStateWhenRescheduleThenThrowException() {
        Reservation dbReservation = mock(Reservation.class);
        when(dbReservation.getReservationStatus()).thenReturn(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        service.rescheduleReservation(RESERVATION_ID, SCHEDULE_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenExistingReservationIdAndReservationStartDateInThePastWhenRescheduleThenThrowException() {
        // setup
        Reservation dbReservation = mock(Reservation.class);
        when(dbReservation.getReservationStatus()).thenReturn(ReservationStatus.READY_TO_PLAY);

        Schedule dbSchedule = mock(Schedule.class);
        when(dbReservation.getSchedule()).thenReturn(dbSchedule);
        when(dbSchedule.getStartDateTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        // execute
        service.rescheduleReservation(RESERVATION_ID, SCHEDULE_ID);
    }

    @Test
    public void givenExistingReservationIdWhenRescheduleThenCorrectReservationDTOReturned() {
        // setup
        Reservation dbReservation = mock(Reservation.class);
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(dbReservation));

        when(dbReservation.getReservationStatus()).thenReturn(ReservationStatus.READY_TO_PLAY);
        when(dbReservation.getValue()).thenReturn(BigDecimal.TEN);
        Schedule dbSchedule = mock(Schedule.class);
        when(dbReservation.getSchedule()).thenReturn(dbSchedule);
        when(dbSchedule.getStartDateTime()).thenReturn(LocalDateTime.now().plusDays(1));

        Guest guest = mock(Guest.class);
        when(guest.getId()).thenReturn(GUEST_ID);
        Reservation canceledReservation = Reservation.builder()
                .reservationStatus(ReservationStatus.CANCELLED)
                .value(new BigDecimal("7.50"))
                .refundValue(new BigDecimal("2.50"))
                .schedule(Schedule.builder().reservations(new ArrayList<>()).build())
                .guest(guest)
                .build();
        when(reservationRepository.save(dbReservation)).thenReturn(canceledReservation);

        ReservationDTO bookedReservation = setupSuccessfullyBookedReservation();

        Reservation rescheduledReservation = mock(Reservation.class);
        when(reservationRepository.save(canceledReservation)).thenReturn(rescheduledReservation);

        // execute
        ReservationDTO result = service.rescheduleReservation(RESERVATION_ID, SCHEDULE_ID);

        // verify
        assertEquals(bookedReservation, result);
    }

    @Test
    public void getRefundValueFullRefund() {
        // setup
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);
        schedule.setStartDateTime(startDateTime);

        // execute
        BigDecimal result = service.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        // verify
        assertEquals(new BigDecimal(10), result);
    }

    @Test
    public void getRefundValue75PercentRefund() {
        // setup
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(13);
        schedule.setStartDateTime(startDateTime);

        // execute
        BigDecimal result = service.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        // verify
        assertEquals(new BigDecimal("7.50"), result);
    }

    @Test
    public void getRefundValue50PercentRefund() {
        // setup
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(4);
        schedule.setStartDateTime(startDateTime);

        // execute
        BigDecimal result = service.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        // verify
        assertEquals(new BigDecimal("5.0"), result);
    }

    @Test
    public void getRefundValue25PercentRefund() {
        // setup
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);
        schedule.setStartDateTime(startDateTime);

        // execute
        BigDecimal result = service.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        // verify
        assertEquals(new BigDecimal("2.50"), result);
    }

    @Test
    public void getNoRefund() {
        // setup
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().minusMinutes(1);
        schedule.setStartDateTime(startDateTime);

        // execute
        BigDecimal result = service.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        // verify
        assertEquals(new BigDecimal(0), result);
    }

    private ReservationDTO setupSuccessfullyBookedReservation() {
        Guest dbGuest = mock(Guest.class);
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(dbGuest));

        Schedule dbSchedule = mock(Schedule.class);
        when(dbSchedule.getReservations()).thenReturn(new ArrayList<>());
        when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.of(dbSchedule));

        Reservation reservation = Reservation.builder()
                .guest(dbGuest)
                .schedule(dbSchedule)
                .value(BigDecimal.TEN)
                .reservationStatus(ReservationStatus.READY_TO_PLAY).build();
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        ReservationDTO expectedReservationDTO = mock(ReservationDTO.class);
        when(reservationMapper.map(reservation)).thenReturn(expectedReservationDTO);

        return expectedReservationDTO;
    }
}