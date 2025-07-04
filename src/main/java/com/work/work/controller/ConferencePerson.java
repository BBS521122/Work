package com.work.work.controller;

import com.work.work.dto.ReceiptDTO;
import com.work.work.dto.ReceiptFormDTO;
import com.work.work.mapper.ConferencePersonMapper;
import com.work.work.service.ReceiptService;
import com.work.work.vo.HttpResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipt")
@CrossOrigin
@Configuration
public class ConferencePerson {

    @Autowired
    ConferencePersonMapper conferencePersonMapper;
    @Autowired
    ReceiptService receiptService;

    @PostMapping("/submit")
    public HttpResponseEntity<Integer> submit(@RequestBody ReceiptFormDTO receiptFormDTO) {
        int res = conferencePersonMapper.submit(receiptFormDTO);
        System.out.println(res);
        return new HttpResponseEntity<>(200, res, "success");
    }

    @GetMapping("/participants")
    public HttpResponseEntity<List<ReceiptDTO>> submit(@RequestParam("conferenceId") int conferenceId) {
        List<ReceiptDTO> res= receiptService.getReceipts(conferenceId);
        return new HttpResponseEntity<>(200, res, "success");
    }
}
