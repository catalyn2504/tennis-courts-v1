package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    private final GuestMapper guestMapper;

    public GuestDTO createGuest(CreateGuestRequestDTO createGuestRequestDTO) {
        return guestMapper.map(guestRepository.save(guestMapper.map(createGuestRequestDTO)));
    }

    public GuestDTO updateGuest(Long id, UpdateGuestRequestDTO updateGuestRequestDTO) {
        Guest existingGuest = guestRepository.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });
        existingGuest.setName(updateGuestRequestDTO.getName());

        return guestMapper.map(guestRepository.save(existingGuest));
    }

    public void deleteGuest(Long id) {
        guestRepository.findById(id).ifPresentOrElse(
                (guest) -> guestRepository.deleteById(guest.getId()),
                () -> {
                    throw new EntityNotFoundException("Guest not found.");
                });
    }

    public GuestDTO findGuest(Long id) {
        return guestRepository.findById(id).map(guestMapper::map).orElseThrow(()->{
            throw new EntityNotFoundException("Guest not found.");
        });
    }

    public GuestDTO getByName(String name) {
        return guestRepository.findByName(name).map(guestMapper::map).orElseThrow(()->{
            throw new EntityNotFoundException("Guest not found.");
        });
    }

    public List<GuestDTO> getAll() {
        return guestRepository.findAll().stream().map(guestMapper::map).collect(Collectors.toList());
    }
}
