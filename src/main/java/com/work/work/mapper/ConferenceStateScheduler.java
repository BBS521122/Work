package com.work.work.mapper;

import com.work.work.entity.Conference;
import com.work.work.enums.ConferenceStateEnum;
import com.work.work.mapper.ConferenceMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ConferenceStateScheduler {

    private final ConferenceMapper conferenceMapper;

    public ConferenceStateScheduler(ConferenceMapper conferenceMapper) {
        this.conferenceMapper = conferenceMapper;
    }

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkAndUpdateConferenceStates() {
        LocalDateTime now = LocalDateTime.now();

        // 1. 检查需要从APPROVED转为ONGOING的会议
        List<Conference> approvedConferences = conferenceMapper.selectConferencesByState(ConferenceStateEnum.APPROVED);
        for (Conference conference : approvedConferences) {
            System.out.println("当前时间：" + now);
            System.out.println("会议开始时间：" + conference.getStartTime());
            if (now.isAfter(conference.getStartTime())) {
                conferenceMapper.updateState(conference.getId(), ConferenceStateEnum.ONGOING);
            }
        }

        // 2. 检查需要从ONGOING转为COMPLETED的会议
        List<Conference> ongoingConferences = conferenceMapper.selectConferencesByState(ConferenceStateEnum.ONGOING);
        for (Conference conference : ongoingConferences) {
            if (now.isAfter(conference.getEndTime())) {
                conferenceMapper.updateState(conference.getId(), ConferenceStateEnum.COMPLETED);
            }
        }
    }
}
