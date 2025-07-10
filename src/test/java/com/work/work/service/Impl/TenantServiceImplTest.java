package com.work.work.service.Impl;

import com.work.work.dto.SearchDTO;
import com.work.work.dto.TenantGetDTO;
import com.work.work.entity.Tenant;
import com.work.work.entity.TenantMedia;
import com.work.work.mapper.sql.TenantMapper;
import com.work.work.mapper.sql.TenantMediaMapper;
import com.work.work.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantServiceImplTest {

    @Mock
    private MinioService minioService;
    
    @Mock
    private TenantMapper tenantMapper;
    
    @Mock
    private TenantMediaMapper tenantMediaMapper;
    
    @InjectMocks
    private TenantServiceImpl tenantService;
    
    private Tenant tenant;
    private MultipartFile file;
    private String uuid = "test-uuid";

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setCover("cover.jpg");
        tenant.setNote("<img src=\"image1.jpg\">");
        
        file = new MockMultipartFile(
            "test.jpg", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );
    }

    // ========== add 方法测试 ==========
    @Test
    void add_正常情况_返回成功() {
        when(minioService.uploadFile(any())).thenReturn("new-cover.jpg");
        when(tenantMapper.insertTenant(any())).thenReturn(1);
        when(tenantMediaMapper.bindMedia(any(), any())).thenReturn(1);
        
        Integer result = tenantService.add(tenant, file, uuid);
        assertEquals(1, result);
        
        verify(minioService).uploadFile(file);
        verify(tenantMapper).insertTenant(tenant);
        verify(tenantMediaMapper).bindMedia(uuid, tenant.getId());
    }
    
    @Test
    void add_文件上传失败_抛出异常() {
        when(minioService.uploadFile(any())).thenThrow(new RuntimeException("上传失败"));
        
        assertThrows(RuntimeException.class, () -> {
            tenantService.add(tenant, file, uuid);
        });
    }
    
    @Test
    void add_插入租户失败_抛出异常() {
        when(minioService.uploadFile(any())).thenReturn("new-cover.jpg");
        when(tenantMapper.insertTenant(any())).thenReturn(0);
        
        assertThrows(RuntimeException.class, () -> {
            tenantService.add(tenant, file, uuid);
        });
    }

    // ========== update 方法测试 ==========
    @Test
    void update_正常情况_返回成功() {
        Tenant oldTenant = new Tenant();
        oldTenant.setCover("old-cover.jpg");
        
        when(tenantMapper.selectTenantById(any())).thenReturn(oldTenant);
        when(minioService.uploadFile(any())).thenReturn("new-cover.jpg");
        when(tenantMapper.updateTenant(any())).thenReturn(1);
        when(tenantMediaMapper.bindMedia(any(), any())).thenReturn(1);
        when(tenantMediaMapper.selectMediaNamesByTenantId(any())).thenReturn(Collections.singletonList("old-media.jpg"));
        
        Integer result = tenantService.update(tenant, file, uuid);
        assertEquals(1, result);
        
        verify(minioService).deleteFile("old-cover.jpg");
        verify(minioService).deleteFile("old-media.jpg");
        verify(tenantMediaMapper).deleteMediaByTenantId(tenant.getId());
    }
    
    @Test
    void update_空文件_使用旧封面() {
        Tenant oldTenant = new Tenant();
        oldTenant.setCover("old-cover.jpg");
        
        when(tenantMapper.selectTenantById(any())).thenReturn(oldTenant);
        when(tenantMapper.updateTenant(any())).thenReturn(1);
        when(tenantMediaMapper.bindMedia(any(), any())).thenReturn(1);
        when(tenantMediaMapper.selectMediaNamesByTenantId(any())).thenReturn(Collections.emptyList());
        
        Integer result = tenantService.update(tenant, null, uuid);
        assertEquals(1, result);
        
        verify(minioService, never()).uploadFile(any());
        verify(minioService, never()).deleteFile("old-cover.jpg");
    }
    
    @Test
    void update_租户不存在_抛出异常() {
        when(tenantMapper.selectTenantById(any())).thenReturn(null);
        
        assertThrows(RuntimeException.class, () -> {
            tenantService.update(tenant, file, uuid);
        });
    }

    // ========== getTenantById 方法测试 ==========
    @Test
    void getTenantById_正常情况_返回DTO() {
        when(tenantMapper.selectTenantById(any())).thenReturn(tenant);
        when(minioService.getSignedUrl(any())).thenReturn("http://minio/image1.jpg");
        
        TenantGetDTO result = tenantService.getTenantById(1L);
        assertNotNull(result);
        assertTrue(result.getNote().contains("http://minio/image1.jpg"));
    }
    
    @Test
    void getTenantById_无媒体内容_返回原始内容() {
        tenant.setNote("纯文本内容");
        when(tenantMapper.selectTenantById(any())).thenReturn(tenant);
        
        TenantGetDTO result = tenantService.getTenantById(1L);
        assertEquals("纯文本内容", result.getNote());
    }

    // ========== uploadMedia 方法测试 ==========
    @Test
    void uploadMedia_正常情况_返回文件名() {
        when(minioService.uploadFile(any())).thenReturn("uploaded.jpg");
        when(tenantMediaMapper.insert(any())).thenReturn(1);
        
        String result = tenantService.uploadMedia(uuid, file);
        assertEquals("uploaded.jpg", result);
    }
    
    @Test
    void uploadMedia_插入失败_抛出异常() {
        when(minioService.uploadFile(any())).thenReturn("uploaded.jpg");
        when(tenantMediaMapper.insert(any())).thenReturn(0);
        
        assertThrows(RuntimeException.class, () -> {
            tenantService.update(tenant, file, uuid);
        });
    }

    // ========== get 方法测试 ==========
    @Test
    void get_正常情况_返回列表() {
        SearchDTO searchDTO = new SearchDTO();
        List<Long> expected = Arrays.asList(1L, 2L, 3L);
        
        when(tenantMapper.get(any())).thenReturn(expected);
        
        List<Long> result = tenantService.get(searchDTO, 1, 10);
        assertEquals(expected, result);
    }
    
    @Test
    void get_空结果_返回空列表() {
        SearchDTO searchDTO = new SearchDTO();
        
        when(tenantMapper.get(any())).thenReturn(Collections.emptyList());
        
        List<Long> result = tenantService.get(searchDTO, 1, 10);
        assertTrue(result.isEmpty());
    }

    // ========== delete 方法测试 ==========
    @Test
    void delete_正常情况_返回删除总数() {
        when(tenantMediaMapper.selectMediaNamesByTenantId(any()))
            .thenReturn(Arrays.asList("file1.jpg", "file2.jpg"));
        when(tenantMapper.deleteTenant(any())).thenReturn(1);
        when(tenantMediaMapper.deleteMediaByTenantId(any())).thenReturn(2);
        
        int result = tenantService.delete(1L);
        assertEquals(3, result);
        
        verify(minioService, times(2)).deleteFile(any());
    }
    
    @Test
    void delete_无关联文件_仅删除数据库记录() {
        when(tenantMediaMapper.selectMediaNamesByTenantId(any()))
            .thenReturn(Collections.emptyList());
        when(tenantMapper.deleteTenant(any())).thenReturn(1);
        when(tenantMediaMapper.deleteMediaByTenantId(any())).thenReturn(0);
        
        int result = tenantService.delete(1L);
        assertEquals(1, result);
        
        verify(minioService, never()).deleteFile(any());
    }
}
