package com.work.work.service;

import com.work.work.dto.ConferenceGetDTO;
import com.work.work.entity.Conference;
import org.springframework.web.multipart.MultipartFile;

public interface ConferenceService {
    String uploadMedia(String uuid, MultipartFile file);

    Integer add(Conference conference, MultipartFile cover, String uuid);

    ConferenceGetDTO getConferenceById(Long id);

    String getCover(Long id);

    Integer update(Conference conference, MultipartFile cover, String uuid);
}
