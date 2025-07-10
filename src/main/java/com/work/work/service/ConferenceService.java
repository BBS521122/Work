package com.work.work.service;

import com.work.work.dto.ConferenceGetDTO;
import com.work.work.entity.Conference;
import com.work.work.vo.ConferenceTimelineVO;
import org.springframework.web.multipart.MultipartFile;

public interface ConferenceService {
    String uploadMedia(String uuid, MultipartFile file);

    Integer add(Conference conference, MultipartFile cover, String uuid);

    ConferenceGetDTO getConferenceById(Long id);

    String getCover(Long id);

    Integer update(Conference conference, MultipartFile cover, String uuid);

    String uploadRecord(Long id, MultipartFile file);

    String getConferenceRecordTextById(Long id);
    void videoTrans(Long id);

    ConferenceTimelineVO getTimeLine(Long id);

    String getSummary(Long id);

    String getMindMap(Long id);

    void generateMinutes(Long id);

    void generateMindMap(Long id);

}
