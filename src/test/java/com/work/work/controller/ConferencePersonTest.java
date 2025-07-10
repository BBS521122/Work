package com.work.work.controller;

import com.work.work.dto.ReceiptDTO;
import com.work.work.dto.ReceiptFormDTO;
import com.work.work.mapper.ConferencePersonMapper;
import com.work.work.service.ReceiptService;
import com.work.work.vo.HttpResponseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConferencePersonTest {

    @Mock
    private ConferencePersonMapper conferencePersonMapper;

    @InjectMocks
    private ConferencePerson conferencePerson;
    @Mock
    private ReceiptService receiptService;

    @Test
    void submit_SuccessfulInsert_ReturnsSuccessResponse() {
        // 准备测试数据
        ReceiptFormDTO receiptFormDTO = new ReceiptFormDTO();
        receiptFormDTO.setId(1L);
        receiptFormDTO.setUnit("Test Unit");
        receiptFormDTO.setName("Test Name");
        receiptFormDTO.setGender("Male");
        receiptFormDTO.setPhone("1234567890");
        receiptFormDTO.setEmail("test@example.com");
        receiptFormDTO.setRoomType("Single");
        receiptFormDTO.setArrivalMethod("Train");
        receiptFormDTO.setArrivalTrain("G123");
        receiptFormDTO.setArrivalTime("2023-01-01 10:00");
        receiptFormDTO.setReturnMethod("Train");
        receiptFormDTO.setReturnTrain("G124");
        receiptFormDTO.setReturnTime("2023-01-03 10:00");
        receiptFormDTO.setRemarks("Test Remarks");

        // 模拟Mapper行为
        when(conferencePersonMapper.submit(receiptFormDTO)).thenReturn(1);

        // 调用被测方法
        HttpResponseEntity<Integer> response = conferencePerson.submit(receiptFormDTO);

        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());

        // 验证Mapper方法被调用
        verify(conferencePersonMapper, times(1)).submit(receiptFormDTO);
    }

    @Test
    void submit_FailedInsert_ReturnsSuccessResponseWithZero() {
        // 准备测试数据
        ReceiptFormDTO receiptFormDTO = new ReceiptFormDTO();
        receiptFormDTO.setId(1L);
        receiptFormDTO.setUnit("Test Unit");

        // 模拟Mapper行为
        when(conferencePersonMapper.submit(receiptFormDTO)).thenReturn(0);

        // 调用被测方法
        HttpResponseEntity<Integer> response = conferencePerson.submit(receiptFormDTO);

        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals(0, response.getData());
        assertEquals("success", response.getMessage());

        // 验证Mapper方法被调用
        verify(conferencePersonMapper, times(1)).submit(receiptFormDTO);
    }

    /**
     * 测试正常获取参会人员列表的情况
     */
    @Test
    void submit_WithValidConferenceId_ReturnsReceiptList() {
        // 准备测试数据
        int conferenceId = 1;
        List<ReceiptDTO> mockReceipts = Arrays.asList(
                new ReceiptDTO("张三", "部门1", "男", "13800138000", "zhangsan@example.com", "会议1"),
                new ReceiptDTO("李四", "部门2", "女", "13800138001", "lisi@example.com", "会议1")
        );

        // 模拟ReceiptService行为
        when(receiptService.getReceipts(conferenceId)).thenReturn(mockReceipts);

        // 调用被测方法
        HttpResponseEntity<List<ReceiptDTO>> response = conferencePerson.submit(conferenceId);

        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals("success", response.getMessage());
        assertEquals(2, response.getData().size());
        assertEquals("张三", response.getData().get(0).getName());
        assertEquals("部门1", response.getData().get(0).getUnit());

        // 验证Service方法被调用
        verify(receiptService, times(1)).getReceipts(conferenceId);
    }

    /**
     * 测试获取空参会人员列表的情况
     */
    @Test
    void submit_WithNoParticipants_ReturnsEmptyList() {
        // 准备测试数据
        int conferenceId = 2;

        // 模拟ReceiptService行为
        when(receiptService.getReceipts(conferenceId)).thenReturn(Collections.emptyList());

        // 调用被测方法
        HttpResponseEntity<List<ReceiptDTO>> response = conferencePerson.submit(conferenceId);

        // 验证结果
        assertEquals(200, response.getCode());
        assertEquals("success", response.getMessage());
        assertTrue(response.getData().isEmpty());

        // 验证Service方法被调用
        verify(receiptService, times(1)).getReceipts(conferenceId);
    }

    /**
     * 测试ReceiptService抛出异常的情况
     */
    @Test
    void submit_WhenServiceThrowsException_ReturnsErrorResponse() {
        // 准备测试数据
        int conferenceId = 3;

        // 模拟ReceiptService行为
        when(receiptService.getReceipts(conferenceId)).thenThrow(new RuntimeException("Database error"));

        // 调用被测方法并验证异常
        assertThrows(RuntimeException.class, () -> {
            conferencePerson.submit(conferenceId);
        });

        // 验证Service方法被调用
        verify(receiptService, times(1)).getReceipts(conferenceId);
    }
}
