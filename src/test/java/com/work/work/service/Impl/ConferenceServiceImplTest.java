package com.work.work.service.Impl;

import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.ConferenceGettingDTO;
import com.work.work.dto.ConferenceWxDTO;
import com.work.work.dto.RequestDTO;
import com.work.work.entity.Conference;
import com.work.work.entity.ConferenceMedia;
import com.work.work.enums.ConferenceStateEnum;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferenceMediaMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.MinioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConferenceServiceImplTest {

    @Mock
    private MinioService minioService;

    @Mock
    private ConferenceMediaMapper conferenceMediaMapper;

    @Mock
    private ConferenceMapper conferenceMapper;

    @Mock
    private ConferenceConverter conferenceConverter;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ConferenceServiceImpl conferenceService;

    private Conference testConference;
    private MockMultipartFile testFile;
    private String testUuid;

    @BeforeEach
    void setUp() {
        // 手动设置通过@Autowired注入的依赖
        ReflectionTestUtils.setField(conferenceService, "conferenceMapper", conferenceMapper);
        ReflectionTestUtils.setField(conferenceService, "conferenceConverter", conferenceConverter);
        ReflectionTestUtils.setField(conferenceService, "userMapper", userMapper);

        testConference = new Conference();
        testConference.setId(1L);
        testConference.setName("测试会议");
        testConference.setState(ConferenceStateEnum.UNDER_CHECK);
        testConference.setCover("test-cover.jpg");
        testConference.setStartTime(LocalDateTime.now());
        testConference.setEndTime(LocalDateTime.now().plusHours(2));
        testConference.setContent("<img src=\"test-image.jpg\">");
        testConference.setUserId(1L);

        testFile = new MockMultipartFile(
            "file",
            "test-file.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        testUuid = "test-uuid-123";
    }

    @AfterEach
    void tearDown() {
        reset(minioService, conferenceMediaMapper, conferenceMapper, conferenceConverter, userMapper);
    }

    @Test
    void uploadMedia_Success() {
        // Arrange
        String expectedFileName = "uploaded-file.jpg";
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn(expectedFileName);
        when(conferenceMediaMapper.insert(any(ConferenceMedia.class))).thenReturn(1);

        // Act
        String result = conferenceService.uploadMedia(testUuid, testFile);

        // Assert
        assertEquals(expectedFileName, result);
        verify(minioService).uploadFile(testFile);
        verify(conferenceMediaMapper).insert(any(ConferenceMedia.class));
    }

    @Test
    void uploadMedia_InsertFailure_ThrowsException() {
        // Arrange
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn("test-file.jpg");
        when(conferenceMediaMapper.insert(any(ConferenceMedia.class))).thenReturn(0);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            conferenceService.uploadMedia(testUuid, testFile);
        });
    }

    @Test
    void add_Success() {
        // Arrange
        String expectedFileName = "uploaded-cover.jpg";
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn(expectedFileName);
        when(conferenceMapper.insertConference(any(Conference.class))).thenReturn(1);
        when(conferenceMediaMapper.bindMedia(anyString(), anyLong())).thenReturn(1);

        // Act
        Integer result = conferenceService.add(testConference, testFile, testUuid);

        // Assert
        assertEquals(1, result);
        assertEquals(expectedFileName, testConference.getCover());
        verify(minioService).uploadFile(testFile);
        verify(conferenceMapper).insertConference(testConference);
        verify(conferenceMediaMapper).bindMedia(testUuid, testConference.getId());
    }

    @Test
    void add_InsertConferenceFailure_ThrowsException() {
        // Arrange
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn("test-cover.jpg");
        when(conferenceMapper.insertConference(any(Conference.class))).thenReturn(0);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            conferenceService.add(testConference, testFile, testUuid);
        });
    }

    @Test
    void getConferenceById_Success() {
        // Arrange
        String expectedSignedUrl = "https://minio.example.com/signed-url";
        String expectedUserName = "测试用户";
        ConferenceGetDTO expectedDTO = new ConferenceGetDTO();
        expectedDTO.setName("测试会议");
        expectedDTO.setUserName(expectedUserName);

        when(conferenceMapper.selectConferenceById(1L)).thenReturn(testConference);
        when(minioService.getSignedUrl(anyString())).thenReturn(expectedSignedUrl);
        when(conferenceConverter.conferenceToConferenceGetDTO(testConference)).thenReturn(expectedDTO);
        when(userMapper.selectNameById(1L)).thenReturn(expectedUserName);

        // Act
        ConferenceGetDTO result = conferenceService.getConferenceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUserName, result.getUserName());
        verify(conferenceMapper).selectConferenceById(1L);
        verify(minioService).getSignedUrl("test-image.jpg");
        verify(conferenceConverter).conferenceToConferenceGetDTO(testConference);
        verify(userMapper).selectNameById(1L);
    }

    @Test
    void getConference_Success() {
        // Arrange
        String expectedSignedUrl = "https://minio.example.com/signed-url";
        String expectedUserName = "测试用户";
        ConferenceGettingDTO expectedDTO = new ConferenceGettingDTO();
        expectedDTO.setName("测试会议");
        expectedDTO.setUserName(expectedUserName);

        when(conferenceMapper.selectConferenceById(1L)).thenReturn(testConference);
        when(minioService.getSignedUrl(anyString())).thenReturn(expectedSignedUrl);
        when(conferenceConverter.conferenceToConferenceGettingDTO(testConference)).thenReturn(expectedDTO);
        when(userMapper.selectNameById(1L)).thenReturn(expectedUserName);

        // Act
        ConferenceGettingDTO result = conferenceService.getConference(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUserName, result.getUserName());
        verify(conferenceMapper).selectConferenceById(1L);
        verify(minioService).getSignedUrl("test-image.jpg");
        verify(conferenceConverter).conferenceToConferenceGettingDTO(testConference);
        verify(userMapper).selectNameById(1L);
    }

    @Test
    void getCover_Success() {
        // Arrange
        String expectedCoverName = "test-cover.jpg";
        String expectedSignedUrl = "https://minio.example.com/signed-cover-url";
        
        when(conferenceMapper.selectCoverById(1L)).thenReturn(expectedCoverName);
        when(minioService.getSignedUrl(expectedCoverName)).thenReturn(expectedSignedUrl);

        // Act
        String result = conferenceService.getCover(1L);

        // Assert
        assertEquals(expectedSignedUrl, result);
        verify(conferenceMapper).selectCoverById(1L);
        verify(minioService).getSignedUrl(expectedCoverName);
    }

    @Test
    void update_Success() {
        // Arrange
        Conference oldConference = new Conference();
        oldConference.setId(1L);
        oldConference.setCover("old-cover.jpg");
        
        String newCoverName = "new-cover.jpg";
        List<String> oldMediaNames = Arrays.asList("old-media1.jpg", "old-media2.jpg");
        
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(oldConference);
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn(newCoverName);
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);
        when(conferenceMediaMapper.selectMediaNamesByConferenceId(1L)).thenReturn(oldMediaNames);
        when(conferenceMediaMapper.bindMedia(anyString(), anyLong())).thenReturn(1);

        // Act
        Integer result = conferenceService.update(testConference, testFile, testUuid);

        // Assert
        assertEquals(1, result);
        assertEquals(newCoverName, testConference.getCover());
        verify(conferenceMapper).selectConferenceById(1L);
        verify(minioService).uploadFile(testFile);
        verify(minioService).deleteFile("old-cover.jpg");
        verify(conferenceMapper).updateConference(testConference);
        verify(conferenceMediaMapper).deleteMediaByConferenceId(1L);
        verify(conferenceMediaMapper).bindMedia(testUuid, 1L);
        verify(minioService).deleteFile("old-media1.jpg");
        verify(minioService).deleteFile("old-media2.jpg");
    }

    @Test
    void update_OldConferenceNotFound_ThrowsException() {
        // Arrange
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            conferenceService.update(testConference, testFile, testUuid);
        });
    }

    @Test
    void update_UpdateConferenceFailure_ThrowsException() {
        // Arrange
        Conference oldConference = new Conference();
        oldConference.setId(1L);
        oldConference.setCover("old-cover.jpg");
        
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(oldConference);
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn("new-cover.jpg");
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(0);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            conferenceService.update(testConference, testFile, testUuid);
        });
    }

    @Test
    void get_Success() {
        // Arrange
        RequestDTO requestDTO = new RequestDTO();
        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);
        
        when(conferenceMapper.get(requestDTO)).thenReturn(expectedIds);

        // Act
        List<Long> result = conferenceService.get(requestDTO, 1, 10);

        // Assert
        assertEquals(expectedIds, result);
        verify(conferenceMapper).get(requestDTO);
    }

    @Test
    void delete_Success() {
        // Arrange
        List<String> mediaNames = Arrays.asList("media1.jpg", "media2.jpg");
        when(conferenceMediaMapper.selectMediaNamesByConferenceId(1L)).thenReturn(mediaNames);
        when(conferenceMapper.deleteConference(1L)).thenReturn(1);
        when(conferenceMediaMapper.deleteMediaByConferenceId(1L)).thenReturn(2);

        // Act
        String result = conferenceService.delete(1L);

        // Assert
        assertEquals("3", result);
        verify(conferenceMediaMapper).selectMediaNamesByConferenceId(1L);
        verify(minioService).deleteFile("media1.jpg");
        verify(minioService).deleteFile("media2.jpg");
        verify(conferenceMapper).deleteConference(1L);
        verify(conferenceMediaMapper).deleteMediaByConferenceId(1L);
    }

    @Test
    void approve_Success() {
        // Arrange
        when(conferenceMapper.approve(1L)).thenReturn(1);

        // Act
        int result = conferenceService.approve(1L);

        // Assert
        assertEquals(1, result);
        verify(conferenceMapper).approve(1L);
    }

    @Test
    void wxGet_Success() {
        // Arrange
        List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);
        when(conferenceMapper.wxGet()).thenReturn(expectedIds);

        // Act
        List<Long> result = conferenceService.wxGet();

        // Assert
        assertEquals(expectedIds, result);
        verify(conferenceMapper).wxGet();
    }

    @Test
    void getWxConference_Success() {
        // Arrange
        String expectedSignedUrl = "https://minio.example.com/signed-url";
        ConferenceWxDTO expectedDTO = new ConferenceWxDTO();
        expectedDTO.setName("测试会议");
        expectedDTO.setUserName("admin");
        expectedDTO.setCover(expectedSignedUrl);

        when(conferenceMapper.selectConferenceById(1L)).thenReturn(testConference);
        when(minioService.getSignedUrl(anyString())).thenReturn(expectedSignedUrl);
        when(conferenceConverter.conferenceToConferenceWxDTO(testConference)).thenReturn(expectedDTO);

        // Act
        ConferenceWxDTO result = conferenceService.getWxConference(1L);

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUserName());
        assertEquals(expectedSignedUrl, result.getCover());
        verify(conferenceMapper).selectConferenceById(1L);
        verify(minioService, times(2)).getSignedUrl(anyString());
        verify(conferenceConverter).conferenceToConferenceWxDTO(testConference);
    }

    @Test
    void update_CoverIsNull_UseOldCover() {
        Conference oldConference = new Conference();
        oldConference.setId(1L);
        oldConference.setCover("old-cover.jpg");
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(oldConference);
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);
        when(conferenceMediaMapper.selectMediaNamesByConferenceId(1L)).thenReturn(Arrays.asList());
        when(conferenceMediaMapper.bindMedia(anyString(), anyLong())).thenReturn(1);

        Integer result = conferenceService.update(testConference, null, testUuid);

        assertEquals(1, result);
        assertEquals("old-cover.jpg", testConference.getCover());
        verify(minioService, never()).uploadFile(any());
        verify(minioService, never()).deleteFile("old-cover.jpg");
    }

    @Test
    void update_CoverIsEmpty_UseOldCover() {
        Conference oldConference = new Conference();
        oldConference.setId(1L);
        oldConference.setCover("old-cover.jpg");
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(oldConference);
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);
        when(conferenceMediaMapper.selectMediaNamesByConferenceId(1L)).thenReturn(Arrays.asList());
        when(conferenceMediaMapper.bindMedia(anyString(), anyLong())).thenReturn(1);

        Integer result = conferenceService.update(testConference, emptyFile, testUuid);

        assertEquals(1, result);
        assertEquals("old-cover.jpg", testConference.getCover());
        verify(minioService, never()).uploadFile(any());
        verify(minioService, never()).deleteFile("old-cover.jpg");
    }

    @Test
    void update_CoverNotNull_OldCoverNull() {
        Conference oldConference = new Conference();
        oldConference.setId(1L);
        oldConference.setCover(null);
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(oldConference);
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn("new-cover.jpg");
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);
        when(conferenceMediaMapper.selectMediaNamesByConferenceId(1L)).thenReturn(Arrays.asList());
        when(conferenceMediaMapper.bindMedia(anyString(), anyLong())).thenReturn(1);

        Integer result = conferenceService.update(testConference, testFile, testUuid);

        assertEquals(1, result);
        assertEquals("new-cover.jpg", testConference.getCover());
        verify(minioService).uploadFile(testFile);
        verify(minioService, never()).deleteFile(any());
    }

    @Test
    void update_CoverNotNull_OldCoverNotNull() {
        Conference oldConference = new Conference();
        oldConference.setId(1L);
        oldConference.setCover("old-cover.jpg");
        when(conferenceMapper.selectConferenceById(1L)).thenReturn(oldConference);
        when(minioService.uploadFile(any(MockMultipartFile.class))).thenReturn("new-cover.jpg");
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);
        when(conferenceMediaMapper.selectMediaNamesByConferenceId(1L)).thenReturn(Arrays.asList());
        when(conferenceMediaMapper.bindMedia(anyString(), anyLong())).thenReturn(1);

        Integer result = conferenceService.update(testConference, testFile, testUuid);

        assertEquals(1, result);
        assertEquals("new-cover.jpg", testConference.getCover());
        verify(minioService).uploadFile(testFile);
        verify(minioService).deleteFile("old-cover.jpg");
    }
}