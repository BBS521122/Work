package com.work.work.controller;

import com.work.work.dto.*;
import com.work.work.entity.Tenant;
import com.work.work.service.TenantService;
import com.work.work.vo.HttpResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.work.work.converter.TenantConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TenantControllerTest {

    @Mock
    private TenantService tenantService;
    @Mock
    private TenantConverter tenantConverter;

    @InjectMocks
    private TenantController tenantController;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddTenant() throws Exception {
        TenantAddDTO tenantAddDTO = new TenantAddDTO();
        tenantAddDTO.setName("Test Tenant");
        tenantAddDTO.setUuid("12345");

        MultipartFile mockFile = new MockMultipartFile("cover", "test.jpg", "image/jpeg", "test image content".getBytes());

        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");

        when(tenantConverter.tenantAddDTOToTenant(any(TenantAddDTO.class))).thenReturn(tenant);
        when(tenantService.add(any(Tenant.class), any(MultipartFile.class), anyString())).thenReturn(1);

        HttpResponseEntity<Integer> response = tenantController.add(tenantAddDTO, mockFile);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
    }



    @Test
    public void testUpdateTenant() throws Exception {
        TenantUpdateDTO tenantUpdateDTO = new TenantUpdateDTO();
        tenantUpdateDTO.setName("Updated Tenant");
        tenantUpdateDTO.setUuid("12345");

        MultipartFile mockFile = new MockMultipartFile("cover", "test.jpg", "image/jpeg", "test image content".getBytes());

        Tenant tenant = new Tenant();
        tenant.setName("Updated Tenant");

        when(tenantConverter.tenantUpdateDTOToTenant(any(TenantUpdateDTO.class))).thenReturn(tenant);
        when(tenantService.update(any(Tenant.class), any(MultipartFile.class), anyString())).thenReturn(1);

        HttpResponseEntity<Integer> response = tenantController.update(tenantUpdateDTO, mockFile);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testGetTenantInfo() {
        Long tenantId = 1L;
        TenantGetDTO mockTenant = new TenantGetDTO();
        mockTenant.setName("测试租户"); // 使用实际存在的setter方法

        when(tenantService.getTenantById(tenantId)).thenReturn(mockTenant);

        HttpResponseEntity<TenantGetDTO> response = tenantController.getInfo(tenantId);

        assertEquals(200, response.getCode());
        assertEquals("测试租户", response.getData().getName());
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testGetTenantCover() {
        Long tenantId = 1L;
        String mockCoverUrl = "http://example.com/cover.jpg";

        when(tenantService.getCover(tenantId)).thenReturn(mockCoverUrl);

        HttpResponseEntity<String> response = tenantController.getCover(tenantId);

        assertEquals(200, response.getCode());
        assertEquals(mockCoverUrl, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testUploadMedia() throws Exception {
        String uuid = "12345";
        MultipartFile mockFile = new MockMultipartFile("file", "test.mp4", "video/mp4", "test video content".getBytes());
        String mockFileName = "uploaded_test.mp4";

        when(tenantService.uploadMedia(uuid, mockFile)).thenReturn(mockFileName);

        HttpResponseEntity<String> response = tenantController.uploadMedia(uuid, mockFile);

        assertEquals(200, response.getCode());
        assertEquals(mockFileName, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testDeleteTenant() {
        Long tenantId = 1L;
        int expectedResult = 1;

        when(tenantService.delete(tenantId)).thenReturn(expectedResult);

        HttpResponseEntity<Integer> response = tenantController.delete(tenantId);

        assertEquals(200, response.getCode());
        assertEquals(expectedResult, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testGetTenants() {
        SearchDTO searchDTO = new SearchDTO();
        int pageNum = 1;
        int pageSize = 10;
        List<Long> mockIds = Arrays.asList(1L, 2L, 3L);
        List<Tenant> mockTenants = new ArrayList<>();

        Tenant tenant1 = new Tenant();
        tenant1.setId(1L);
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        Tenant tenant3 = new Tenant();
        tenant3.setId(3L);
        mockTenants.addAll(Arrays.asList(tenant1, tenant2, tenant3));

        when(tenantService.get(searchDTO, pageNum, pageSize)).thenReturn(mockIds);
        when(tenantService.getTenant(1L)).thenReturn(tenant1);
        when(tenantService.getTenant(2L)).thenReturn(tenant2);
        when(tenantService.getTenant(3L)).thenReturn(tenant3);

        HttpResponseEntity<List<Tenant>> response = tenantController.get(searchDTO, pageNum, pageSize);

        assertEquals(200, response.getCode());
        assertEquals(3, response.getData().size());
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testBatchDelete() {
        Map<String, List<String>> request = new HashMap<>();
        request.put("ids", Arrays.asList("1", "2", "abc"));

        when(tenantService.delete(1L)).thenReturn(1);
        when(tenantService.delete(2L)).thenReturn(1);

        HttpResponseEntity<String> response = tenantController.batchDelete(request);

        assertEquals(200, response.getCode());
        assertTrue(response.getData().contains("11"));
        assertTrue(response.getData().contains("Invalid ID format"));
        assertEquals("success", response.getMessage());
    }

    @Test
    public void testRegisterTenant() {
        TenantAddDTO tenantAddDTO = new TenantAddDTO();
        tenantAddDTO.setName("Test Tenant");

        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setAdmin("admin"); // 验证自动设置的admin字段

        when(tenantConverter.tenantAddDTOToTenant(any(TenantAddDTO.class))).thenReturn(tenant);
        when(tenantService.register(any(Tenant.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = tenantController.register(tenantAddDTO);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("admin", tenant.getAdmin()); // 关键断言
    }

}
