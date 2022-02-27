package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    private final ScheduleRepository scheduleRepository;

    private final GuestRepository guestRepository;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Long guestId = createReservationRequestDTO.getGuestId();
        Long scheduleId = createReservationRequestDTO.getScheduleId();

        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest does not exist.");
        });
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> {
            throw new EntityNotFoundException("Schedule does not exist.");
        });
        if (!schedule.getReservations().isEmpty()) {
            throw new AlreadyExistsEntityException("This time slot is already booked.");
        }

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .schedule(schedule)
                .value(BigDecimal.TEN)
                .reservationStatus(ReservationStatus.READY_TO_PLAY)
                .build();
        schedule.addReservation(reservation);

        return reservationMapper.map(reservationRepository.save(reservation));
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);
        Schedule schedule = reservation.getSchedule();
        schedule.setReservations(new ArrayList<>());
        reservation.setSchedule(schedule);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (minutes >= 1440) {
            return reservation.getValue();
        } else if (minutes >= 720) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.75));
        } else if (minutes >= 120) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.5));
        } else if (minutes >= 1) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.25));
        }

        return BigDecimal.ZERO;
    }

    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
        Reservation previousReservation = cancel(previousReservationId);

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }
}
