package com.work.work.service;

import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.ConferenceGettingDTO;
import com.work.work.dto.ConferenceWxDTO;
import com.work.work.dto.RequestDTO;
import com.work.work.entity.Conference;
import com.work.work.vo.ConferenceTimelineVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConferenceService {
    String uploadMedia(String uuid, MultipartFile file);

    Integer add(Conference conference, MultipartFile cover, String uuid);

    ConferenceGetDTO getConferenceById(Long id);

    ConferenceGettingDTO getConference(Long id);

    String getCover(Long id);

    Integer update(Conference conference, MultipartFile cover, String uuid);

    List<Long> get(RequestDTO requestDTO, int pageNum, int pageSize);
    String uploadRecord(Long id, MultipartFile file);

    String getConferenceRecordTextById(Long id);
    void videoTrans(Long id);

    ConferenceTimelineVO getTimeLine(Long id);

    String getSummary(Long id);

    String delete(Long id);
    String getMindMap(Long id);

    int approve(Long id);
    void generateMinutes(Long id);

    List<Long> wxGet();
    void generateMindMap(Long id);

    ConferenceWxDTO getWxConference(Long Id);
}
