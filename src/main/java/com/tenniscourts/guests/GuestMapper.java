package com.tenniscourts.guests;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    Guest map(GuestDTO guestDTO);

    GuestDTO map(Guest guest);

    Guest map(CreateGuestRequestDTO createGuestRequestDTO);
}
