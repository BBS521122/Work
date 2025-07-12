package com.work.work.service.Impl;

import com.work.work.dto.ReceiptDTO;
import com.work.work.dto.ReceiptFormDTO;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferencePersonMapper;
import com.work.work.converter.ReceiptsConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptServiceImplTest {

    @Mock
    private ConferencePersonMapper conferencePersonMapper;

    @Mock
    private ReceiptsConverter receiptConverter;

    @Mock
    private ConferenceMapper conferenceMapper;

    @InjectMocks
    private ReceiptServiceImpl receiptService;

    @Test
    public void testGetReceipts() {
        // 准备测试数据
        int conferenceId = 1;
        String conferenceName = "Test Conference";
        
        ReceiptFormDTO formDTO1 = new ReceiptFormDTO();
        ReceiptFormDTO formDTO2 = new ReceiptFormDTO();
        List<ReceiptFormDTO> mockReceipts = Arrays.asList(formDTO1, formDTO2);
        
        ReceiptDTO dto1 = new ReceiptDTO();
        ReceiptDTO dto2 = new ReceiptDTO();
        
        // 设置mock行为
        when(conferencePersonMapper.getReceipts(conferenceId)).thenReturn(mockReceipts);
        when(receiptConverter.receiptToReceiptDTO(formDTO1)).thenReturn(dto1);
        when(receiptConverter.receiptToReceiptDTO(formDTO2)).thenReturn(dto2);
        when(conferenceMapper.getConferenceById((long) conferenceId)).thenReturn(conferenceName);
        
        // 执行测试
        List<ReceiptDTO> result = receiptService.getReceipts(conferenceId);
        
        // 验证结果
        assertEquals(2, result.size());
        assertEquals(conferenceName, result.get(0).getConferenceName());
        assertEquals(conferenceName, result.get(1).getConferenceName());
        
        // 验证mock交互
        verify(conferencePersonMapper, times(1)).getReceipts(conferenceId);
        verify(receiptConverter, times(1)).receiptToReceiptDTO(formDTO1);
        verify(receiptConverter, times(1)).receiptToReceiptDTO(formDTO2);
        verify(conferenceMapper, times(2)).getConferenceById((long) conferenceId);
    }
}
