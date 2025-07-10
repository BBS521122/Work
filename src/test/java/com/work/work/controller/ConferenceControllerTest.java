package com.work.work.controller;

import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.*;
import com.work.work.entity.Conference;
import com.work.work.service.ConferenceService;
import com.work.work.vo.HttpResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConferenceControllerTest {

    @Mock
    private ConferenceService conferenceService;

    @Mock
    private ConferenceConverter conferenceConverter;

    @InjectMocks
    private ConferenceController conferenceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add() throws Exception {
        ConferenceAddDTO addDTO = new ConferenceAddDTO();
        addDTO.setUuid("test-uuid");
        MultipartFile cover = new MockMultipartFile("cover", "test.jpg", "image/jpeg", "test".getBytes());
        Conference conference = new Conference();
        
        when(conferenceConverter.conferenceAddDTOToConference(addDTO)).thenReturn(conference);
        when(conferenceService.add(conference, cover, "test-uuid")).thenReturn(1);
        
        HttpResponseEntity<Integer> response = conferenceController.add(addDTO, cover);
        
        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void update() throws Exception {
        ConferenceUpdateDTO updateDTO = new ConferenceUpdateDTO();
        updateDTO.setUuid("test-uuid");
        MultipartFile cover = new MockMultipartFile("cover", "test.jpg", "image/jpeg", "test".getBytes());
        Conference conference = new Conference();
        
        when(conferenceConverter.conferenceUpdateDTOToConference(updateDTO)).thenReturn(conference);
        when(conferenceService.update(conference, cover, "test-uuid")).thenReturn(1);
        
        HttpResponseEntity<Integer> response = conferenceController.update(updateDTO, cover);
        
        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void getInfo() {
        Long id = 1L;
        ConferenceGetDTO expected = new ConferenceGetDTO();
        
        when(conferenceService.getConferenceById(id)).thenReturn(expected);
        
        HttpResponseEntity<ConferenceGetDTO> response = conferenceController.getInfo(id);
        
        assertEquals(200, response.getCode());
        assertEquals(expected, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void getCover() {
        Long id = 1L;
        String expected = "cover-url";
        
        when(conferenceService.getCover(id)).thenReturn(expected);
        
        HttpResponseEntity<String> response = conferenceController.getCover(id);
        
        assertEquals(200, response.getCode());
        assertEquals(expected, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void uploadMedia() throws Exception {
        String uuid = "test-uuid";
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        String expected = "media-url";
        
        when(conferenceService.uploadMedia(uuid, file)).thenReturn(expected);
        
        HttpResponseEntity<String> response = conferenceController.uploadMedia(uuid, file);
        
        assertEquals(200, response.getCode());
        assertEquals(expected, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void get() {
        RequestDTO requestDTO = new RequestDTO();
        int pageNum = 1;
        int pageSize = 10;
        List<Long> ids = List.of(1L, 2L);
        List<ConferenceGettingDTO> expected = new ArrayList<>();
        expected.add(new ConferenceGettingDTO());
        expected.add(new ConferenceGettingDTO());
        
        when(conferenceService.get(requestDTO, pageNum, pageSize)).thenReturn(ids);
        when(conferenceService.getConference(1L)).thenReturn(expected.get(0));
        when(conferenceService.getConference(2L)).thenReturn(expected.get(1));
        
        HttpResponseEntity<List<ConferenceGettingDTO>> response = conferenceController.get(requestDTO, pageNum, pageSize);
        
        assertEquals(200, response.getCode());
        assertEquals(expected.size(), response.getData().size());
        assertEquals("success", response.getMessage());
    }

    @Test
    void wxGet() {
        List<Long> ids = List.of(1L, 2L);
        List<ConferenceWxDTO> expected = new ArrayList<>();
        expected.add(new ConferenceWxDTO());
        expected.add(new ConferenceWxDTO());
        
        when(conferenceService.wxGet()).thenReturn(ids);
        when(conferenceService.getWxConference(1L)).thenReturn(expected.get(0));
        when(conferenceService.getWxConference(2L)).thenReturn(expected.get(1));
        
        HttpResponseEntity<List<ConferenceWxDTO>> response = conferenceController.wxGet();
        
        assertEquals(200, response.getCode());
        assertEquals(expected.size(), response.getData().size());
        assertEquals("success", response.getMessage());
    }

    @Test
    void delete() {
        Long id = 1L;
        String expected = "success";
        
        when(conferenceService.delete(id)).thenReturn(expected);
        
        HttpResponseEntity<String> response = conferenceController.delete(id);
        
        assertEquals(200, response.getCode());
        assertEquals(expected, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void approve() {
        Long id = 1L;
        int expected = 1;
        
        when(conferenceService.approve(id)).thenReturn(expected);
        
        HttpResponseEntity<String> response = conferenceController.approve(id);
        
        assertEquals(200, response.getCode());
        assertEquals(String.valueOf(expected), response.getData());
        assertEquals("success", response.getMessage());
    }
}
