package com.work.work.mapper;

import com.work.work.dto.RequestDTO;
import com.work.work.entity.Conference;
import com.work.work.enums.ConferenceStateEnum;
import org.apache.ibatis.annotations.Options;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConferenceMapperTest {

    @Mock
    private ConferenceMapper conferenceMapper;

    @Test
    void insertConference() {
        Conference conference = new Conference();
        conference.setName("Test Conference");
        conference.setState(ConferenceStateEnum.UNDER_CHECK);
        conference.setStartTime(LocalDateTime.now().plusDays(1));
        conference.setEndTime(LocalDateTime.now().plusDays(2));
        conference.setContent("Test content");
        conference.setUserId(1L);

        when(conferenceMapper.insertConference(any(Conference.class))).thenReturn(1);

        int result = conferenceMapper.insertConference(conference);

        assertEquals(1, result);
        verify(conferenceMapper, times(1)).insertConference(conference);
    }

    @Test
    void selectConferenceById() {
        Conference expected = new Conference();
        expected.setId(1L);
        expected.setName("Test Conference");

        when(conferenceMapper.selectConferenceById(1L)).thenReturn(expected);

        Conference result = conferenceMapper.selectConferenceById(1L);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
    }

    @Test
    void selectCoverById() {
        String expectedCover = "cover.jpg";
        when(conferenceMapper.selectCoverById(1L)).thenReturn(expectedCover);

        String result = conferenceMapper.selectCoverById(1L);

        assertEquals(expectedCover, result);
    }

    @Test
    void updateConference() {
        Conference conference = new Conference();
        conference.setId(1L);
        conference.setName("Updated Conference");

        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);

        int result = conferenceMapper.updateConference(conference);

        assertEquals(1, result);
        verify(conferenceMapper, times(1)).updateConference(conference);
    }

    @Test
    void getWithRequestDTO() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setKeyword("test");
        requestDTO.setState(String.valueOf(ConferenceStateEnum.APPROVED));

        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);
        when(conferenceMapper.get(requestDTO)).thenReturn(expectedIds);

        List<Long> result = conferenceMapper.get(requestDTO);

        assertEquals(3, result.size());
        assertEquals(expectedIds, result);
    }

    @Test
    void deleteConference() {
        when(conferenceMapper.deleteConference(1L)).thenReturn(1);

        int result = conferenceMapper.deleteConference(1L);

        assertEquals(1, result);
        verify(conferenceMapper, times(1)).deleteConference(1L);
    }

    @Test
    void approve() {
        when(conferenceMapper.approve(1L)).thenReturn(1);

        int result = conferenceMapper.approve(1L);

        assertEquals(1, result);
        verify(conferenceMapper, times(1)).approve(1L);
    }

    @Test
    void selectConferencesByState() {
        Conference conference1 = new Conference();
        conference1.setId(1L);
        conference1.setState(ConferenceStateEnum.APPROVED);

        Conference conference2 = new Conference();
        conference2.setId(2L);
        conference2.setState(ConferenceStateEnum.APPROVED);

        List<Conference> expected = Arrays.asList(conference1, conference2);
        when(conferenceMapper.selectConferencesByState(ConferenceStateEnum.APPROVED)).thenReturn(expected);

        List<Conference> result = conferenceMapper.selectConferencesByState(ConferenceStateEnum.APPROVED);

        assertEquals(2, result.size());
        assertEquals(ConferenceStateEnum.APPROVED, result.get(0).getState());
    }

    @Test
    void updateState() {
        when(conferenceMapper.updateState(1L, ConferenceStateEnum.REJECTED)).thenReturn(1);

        int result = conferenceMapper.updateState(1L, ConferenceStateEnum.REJECTED);

        assertEquals(1, result);
        verify(conferenceMapper, times(1)).updateState(1L, ConferenceStateEnum.REJECTED);
    }

    @Test
    void wxGet() {
        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L, 4L);
        when(conferenceMapper.wxGet()).thenReturn(expectedIds);

        List<Long> result = conferenceMapper.wxGet();

        assertEquals(4, result.size());
        assertEquals(expectedIds, result);
    }
}