package com.work.work.service.Impl;

import com.work.work.constants.ConferenceRecordConstants;
import com.work.work.constants.DifyConstants;
import com.work.work.dto.DifyResponseDTO;
import com.work.work.entity.Conference;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferenceRecordMapper;
import com.work.work.properties.DifyProperties;
import com.work.work.service.ConferenceService;
import com.work.work.service.DifyService;
import com.work.work.utils.DifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DifyServiceImpl implements DifyService {
    @Autowired
    DifyProperties difyProperties;
    @Autowired
    ConferenceService conferenceService;
    @Autowired
    ConferenceRecordMapper conferenceRecordMapper;
    @Autowired
    ConferenceMapper conferenceMapper;


    @Override
    @Async
    public void updateMindMap(Long conferenceId) {
        conferenceRecordMapper.updateMindMapStatus(conferenceId, ConferenceRecordConstants.DOING);
        String title = conferenceMapper.selectConferenceById(conferenceId).getName();
        String url = conferenceService.getConferenceRecordTextById(conferenceId);
        try {
            DifyResponseDTO difyResponseDTO = sendMindMapRequest(title, url);
            if (!difyResponseDTO.getData().getStatus().equals("succeeded")) {
                throw new RuntimeException("Dify返回失败");
            }
            // 更新数据库
            String res = (String) difyResponseDTO.getData().getOutputs().get(DifyConstants.OUTPUT_FIELD_NAME);
            String json = DifyUtils.sanitizeJson(res);
            conferenceRecordMapper.updateMindMap(conferenceId, json);
            conferenceRecordMapper.updateMindMapStatus(conferenceId, ConferenceRecordConstants.COMPLETED);
        } catch (Exception e) {
            conferenceRecordMapper.updateMindMapStatus(conferenceId, ConferenceRecordConstants.FAILED);
            e.printStackTrace();
        }

    }

    @Override
    @Async
    public void updateSummary(Long conferenceId) {
        conferenceRecordMapper.updateSummaryStatus(conferenceId, ConferenceRecordConstants.DOING);
        String title = conferenceMapper.selectConferenceById(conferenceId).getName();
        String url = conferenceService.getConferenceRecordTextById(conferenceId);
        try {
            DifyResponseDTO difyResponseDTO = sendSummaryRequest(title, url);
            if (!difyResponseDTO.getData().getStatus().equals("succeeded")) {
                throw new RuntimeException("Dify返回失败");
            }
            // 更新数据库
            String res = (String) difyResponseDTO.getData().getOutputs().get(DifyConstants.OUTPUT_FIELD_NAME);
            String html = DifyUtils.sanitizeHtml(res);
            conferenceRecordMapper.updateSummary(conferenceId, html);
            conferenceRecordMapper.updateSummaryStatus(conferenceId, ConferenceRecordConstants.COMPLETED);
        } catch (Exception e) {
            conferenceRecordMapper.updateSummaryStatus(conferenceId, ConferenceRecordConstants.FAILED);
            e.printStackTrace();
        }

    }

    public DifyResponseDTO sendMindMapRequest(String title, String url) {
        String baseUrl = difyProperties.getBaseUrl();
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> pureText = new HashMap<>();
        pureText.put("type", "document");
        pureText.put("transfer_method", "remote_url");
        pureText.put("url", url);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("pure_text", pureText);
        inputs.put("title", title);

        Map<String, Object> body = new HashMap<>();
        body.put("user", "user");
        body.put("inputs", inputs);
        body.put("response_mode", "blocking");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 添加 Authorization 头，格式：Bearer <API_KEY>
        headers.set("Authorization", "Bearer " + difyProperties.getMindMapKey());

        ResponseEntity<DifyResponseDTO> response = restTemplate.postForEntity(
                difyProperties.getBaseUrl(),
                new HttpEntity<>(body, headers),
                DifyResponseDTO.class
        );
        return response.getBody();
    }

    public DifyResponseDTO sendSummaryRequest(String title, String url) {
        String baseUrl = difyProperties.getBaseUrl();
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> pureText = new HashMap<>();
        pureText.put("type", "document");
        pureText.put("transfer_method", "remote_url");
        pureText.put("url", url);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("pure_text", pureText);
        inputs.put("title", title);

        Map<String, Object> body = new HashMap<>();
        body.put("user", "user");
        body.put("inputs", inputs);
        body.put("response_mode", "blocking");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 添加 Authorization 头，格式：Bearer <API_KEY>
        headers.set("Authorization", "Bearer " + difyProperties.getSummaryKey());

        ResponseEntity<DifyResponseDTO> response = restTemplate.postForEntity(
                difyProperties.getBaseUrl(),
                new HttpEntity<>(body, headers),
                DifyResponseDTO.class
        );
        return response.getBody();
    }

}
