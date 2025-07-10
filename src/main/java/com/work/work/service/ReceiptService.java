package com.work.work.service;

import com.work.work.dto.ReceiptDTO;

import java.util.List;

public interface ReceiptService {

    List<ReceiptDTO> getReceipts(int conferenceId);
}
