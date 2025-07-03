package com.work.work.controller;

import com.work.work.context.UserContext;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserConferenceControllerTest {

    @Mock
    private ConferenceService conferenceService;

    @Mock
    private ConferenceConverter conferenceConverter;

    @InjectMocks
    private UserConferenceController userConferenceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add_ShouldReturnSuccess() throws Exception {
        // 准备测试数据
        ConferenceAddDTO addDTO = new ConferenceAddDTO();
        MultipartFile mockFile = new MockMultipartFile("test.jpg", new byte[10]);
        
        Conference conference = new Conference();
        when(conferenceConverter.conferenceAddDTOToConference(any())).thenReturn(conference);
        when(conferenceService.add(any(), any(), any())).thenReturn(1);
        
        // 执行测试
        HttpResponseEntity<Integer> response = userConferenceController.add(addDTO, mockFile);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void update_ShouldReturnSuccess() throws Exception {
        // 准备测试数据
        ConferenceUpdateDTO updateDTO = new ConferenceUpdateDTO();
        MultipartFile mockFile = new MockMultipartFile("test.jpg", new byte[10]);
        
        Conference conference = new Conference();
        when(conferenceConverter.conferenceUpdateDTOToConference(any())).thenReturn(conference);
        when(conferenceService.update(any(), any(), any())).thenReturn(1);
        
        // 执行测试
        HttpResponseEntity<Integer> response = userConferenceController.update(updateDTO, mockFile);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void getInfo_ShouldReturnConferenceInfo() {
        // 准备测试数据
        ConferenceGetDTO mockDTO = new ConferenceGetDTO();
        when(conferenceService.getConferenceById(anyLong())).thenReturn(mockDTO);
        
        // 执行测试
        HttpResponseEntity<ConferenceGetDTO> response = userConferenceController.getInfo(1L);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void getCover_ShouldReturnCoverUrl() {
        // 准备测试数据
        when(conferenceService.getCover(anyLong())).thenReturn("http://example.com/cover.jpg");
        
        // 执行测试
        HttpResponseEntity<String> response = userConferenceController.getCover(1L);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals("http://example.com/cover.jpg", response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void uploadMedia_ShouldReturnFileName() {
        // 准备测试数据
        MultipartFile mockFile = new MockMultipartFile("test.jpg", new byte[10]);
        when(conferenceService.uploadMedia(anyString(), any())).thenReturn("test.jpg");
        
        // 执行测试
        HttpResponseEntity<String> response = userConferenceController.uploadMedia("uuid123", mockFile);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals("test.jpg", response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void get_ShouldReturnConferenceList() {
        // 准备测试数据
        RequestDTO requestDTO = new RequestDTO();
        List<Long> idList = List.of(1L, 2L);
        when(conferenceService.get(any(), anyInt(), anyInt())).thenReturn(idList);
        
        ConferenceGettingDTO dto1 = new ConferenceGettingDTO();
        ConferenceGettingDTO dto2 = new ConferenceGettingDTO();
        when(conferenceService.getConference(1L)).thenReturn(dto1);
        when(conferenceService.getConference(2L)).thenReturn(dto2);
        
        // 执行测试
        HttpResponseEntity<List<ConferenceGettingDTO>> response = 
            userConferenceController.get(requestDTO, 1, 10);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals(2, response.getData().size());
        assertEquals("success", response.getMessage());
    }

    @Test
    void delete_ShouldReturnSuccess() {
        // 准备测试数据
        when(conferenceService.delete(anyLong())).thenReturn("deleted");
        
        // 执行测试
        HttpResponseEntity<String> response = userConferenceController.delete(1L);
        
        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals("deleted", response.getData());
        assertEquals("success", response.getMessage());
    }
}
