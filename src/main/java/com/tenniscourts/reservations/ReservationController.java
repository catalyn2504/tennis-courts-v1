package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/reservations")
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @ApiModelProperty(value = "Book a reservation")
    @PostMapping
    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @ApiModelProperty(value = "Find a reservation by id")
    @GetMapping(value = "/{reservationId}")
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable("reservationId") Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @ApiModelProperty(value = "Cancel reservation")
    @DeleteMapping(value = "/{reservationId}")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @ApiModelProperty(value = "Reschedule reservation")
    @PutMapping(value = "/{reservationId}/{scheduleId}")
    public ResponseEntity<ReservationDTO> rescheduleReservation(@PathVariable Long reservationId, @PathVariable Long scheduleId) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId));
    }
}
