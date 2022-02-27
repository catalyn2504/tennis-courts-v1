package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final TennisCourtRepository tennisCourtRepository;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        LocalDateTime startDateTime = createScheduleRequestDTO.getStartDateTime();

        TennisCourt tennisCourt = tennisCourtRepository.findById(tennisCourtId).orElseThrow(() -> {
            throw new EntityNotFoundException("Tennis court not found.");
        });
        Schedule schedule = Schedule.builder()
                .tennisCourt(tennisCourt)
                .startDateTime(startDateTime)
                .endDateTime(startDateTime.plusHours(1))
                .build();

        return scheduleMapper.map(scheduleRepository.save(schedule));
    }

    public List<ScheduleDTO> findNonBookedSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<ScheduleDTO> result = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStartDateTime().isAfter(startDate)
                        && schedule.getEndDateTime().isBefore(endDate)
                        && schedule.getReservations().isEmpty())
                .map(scheduleMapper::map)
                .collect(toList());

        return result;
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .map(scheduleMapper::map)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Schedule not found.");
                });
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
