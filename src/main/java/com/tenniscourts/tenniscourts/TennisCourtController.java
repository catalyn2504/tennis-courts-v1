package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/tenniscourts")
public class TennisCourtController extends BaseRestController {

    private final TennisCourtService tennisCourtService;

    @ApiModelProperty(value = "Create a tennis court")
    @PostMapping
    public ResponseEntity<Void> addTennisCourt(@RequestBody TennisCourtDTO tennisCourtDTO) {
        return ResponseEntity.created(locationByEntity(tennisCourtService.addTennisCourt(tennisCourtDTO).getId())).build();
    }

    @ApiModelProperty(value = "Find tennis court by id")
    @GetMapping(value = "/{tennisCourtId}")
    public ResponseEntity<TennisCourtDTO> findTennisCourtById(@PathVariable("tennisCourtId") Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtById(tennisCourtId));
    }

    @ApiModelProperty(value = "Find tennis court with schedules by id")
    @GetMapping(value = "/{tennisCourtId}/full")
    public ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(@PathVariable("tennisCourtId") Long tennisCourtId) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId));
    }
}
