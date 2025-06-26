package com.work.work.converter;

import com.work.work.dto.ConferenceAddDTO;
import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.ConferenceUpdateDTO;
import com.work.work.entity.Conference;
import com.work.work.entity.ConferenceMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConferenceConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "state", constant = "UNDER_CHECK")
    @Mapping(target = "cover", ignore = true)
    Conference conferenceAddDTOToConference(ConferenceAddDTO conferenceAddDTO);

    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "content",ignore = true)
    ConferenceGetDTO conferenceToConferenceGetDTO(Conference conference);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "cover", ignore = true)
    Conference conferenceUpdateDTOToConference(ConferenceUpdateDTO conferenceUpdateDTO);

}
