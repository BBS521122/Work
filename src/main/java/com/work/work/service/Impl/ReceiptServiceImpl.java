package com.work.work.service.Impl;

import com.work.work.converter.ReceiptsConverter;
import com.work.work.dto.ReceiptDTO;
import com.work.work.dto.ReceiptFormDTO;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferencePersonMapper;
import com.work.work.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    ConferencePersonMapper conferencePersonMapper;
    @Autowired
    ReceiptsConverter receiptConverter;
    @Autowired
    ConferenceMapper conferenceMapper;

    @Override
    public List<ReceiptDTO> getReceipts(int conferenceId) {
        List<ReceiptFormDTO> receipts = conferencePersonMapper.getReceipts(conferenceId);
        List<ReceiptDTO> receiptDTOS = new ArrayList<>();
        for (ReceiptFormDTO receipt : receipts) {
            ReceiptDTO receiptDTO = receiptConverter.receiptToReceiptDTO(receipt);
            String conferenceName = conferenceMapper.getConferenceById((long) conferenceId);
            receiptDTO.setConferenceName(conferenceName);
            receiptDTOS.add(receiptDTO);
        }
        return receiptDTOS;
    }
}
