package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/guests")
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @ApiModelProperty(value = "Create a guest")
    @PostMapping
    public ResponseEntity<Void> createGuest(@RequestBody CreateGuestRequestDTO createGuestRequestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.createGuest(createGuestRequestDTO).getId())).build();
    }

    @ApiModelProperty(value = "Update a guest")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<GuestDTO> updateGuest(@PathVariable Long id, @RequestBody UpdateGuestRequestDTO updateGuestRequestDTO) {
        return ResponseEntity.ok(guestService.updateGuest(id, updateGuestRequestDTO));
    }

    @ApiModelProperty(value = "Delete a guest")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    @ApiModelProperty(value = "Find guest by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<GuestDTO> findGuest(@PathVariable("id")Long id) {
        return ResponseEntity.ok(guestService.findGuest(id));
    }

    @ApiModelProperty(value = "Find guests by name")
    @GetMapping(value = "/filter")
    public ResponseEntity<GuestDTO> findGuestsByName(@RequestParam("name") @NotNull String name) {
        return ResponseEntity.ok(guestService.getByName(name));
    }

    @ApiModelProperty(value = "Find all guests")
    @GetMapping
    public ResponseEntity<List<GuestDTO>> getAll() {
        return ResponseEntity.ok(guestService.getAll());
    }

}
