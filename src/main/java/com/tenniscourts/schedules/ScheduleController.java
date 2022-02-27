package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @ApiModelProperty(value = "Add schedule to tennis court")
    @PostMapping
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(),
                createScheduleRequestDTO).getId())).build();
    }

    @ApiModelProperty(value = "Find free schedules between dates")
    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> findNonBookedSchedulesByDates(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.findNonBookedSchedulesByDates(
                LocalDateTime.of(startDate, LocalTime.of(0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59))));
    }

    @ApiModelProperty(value = "Find schedule by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable("id") Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }
}
