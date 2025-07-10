package com.work.work.controller;

import com.work.work.constants.ConferenceRecordConstants;
import com.work.work.context.UserContext;
import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.*;
import com.work.work.entity.Conference;
import com.work.work.mapper.ConferenceRecordMapper;
import com.work.work.service.AliCloudService;
import com.work.work.service.ConferenceService;
import com.work.work.service.DifyService;
import com.work.work.vo.ConferenceTimelineVO;
import com.work.work.vo.HttpResponseEntity;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/conference")
@CrossOrigin
@Configuration
public class ConferenceController {
    @Autowired
    ConferenceService conferenceService;
    @Autowired
    ConferenceConverter conferenceConverter;
    @Autowired
    DifyService  difyService;
    @Autowired
    ConferenceRecordMapper  conferenceRecordMapper;
    @Autowired
    AliCloudService aliCloudService;


    /**
     * 添加新的会议信息
     *
     * @param conferenceAddDTO
     * @param cover
     * @return
     */
    @PostMapping("/add")
    public HttpResponseEntity<Integer> add(@RequestPart("data") ConferenceAddDTO conferenceAddDTO, @RequestPart("cover") MultipartFile cover) {
        Conference conference = conferenceConverter.conferenceAddDTOToConference(conferenceAddDTO);
        conference.setUserId(UserContext.getUserId());
        Integer res = conferenceService.add(conference, cover, conferenceAddDTO.getUuid());
        // 业务处理
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 更新会议信息
     *
     * @param conferenceUpdateDTO
     * @param cover
     * @return
     */
    @PostMapping("/update")
    public HttpResponseEntity<Integer> update(@RequestPart("data") ConferenceUpdateDTO conferenceUpdateDTO, @RequestPart(value = "cover",required = false) MultipartFile cover) {
        Conference conference = conferenceConverter.conferenceUpdateDTOToConference(conferenceUpdateDTO);
        Integer res = conferenceService.update(conference, cover, conferenceUpdateDTO.getUuid());
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 获取指定id的会议信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get-info")
    public HttpResponseEntity<ConferenceGetDTO> getInfo(@RequestParam("id") Long id) {
       ConferenceGetDTO res= conferenceService.getConferenceById(id);
       return new HttpResponseEntity<>(200,res,"success");
    }

    /**
     * 获取指定id的会议封面
     *
     * @param id
     * @return
     */
    @GetMapping("get-cover")
    public HttpResponseEntity<String> getCover(@RequestParam("id") Long id) {
        String res = conferenceService.getCover(id);
       return new HttpResponseEntity<>(200,res,"success");
    }

    // TODO 优化分片上传 变大maxSize

    /**
     * 在上传会议信息之前，预先上传富文本媒体内容
     *
     * @param uuid
     * @param file
     * @return
     */
    @PostMapping("/upload-media")
    public HttpResponseEntity<String> uploadMedia(@RequestParam("uuid") String uuid, @RequestParam("file") MultipartFile file) {
        String name = conferenceService.uploadMedia(uuid, file);
        return new HttpResponseEntity<>(200, name, "success");
    }

    @PostMapping("/get")
    public HttpResponseEntity<List<ConferenceGettingDTO>> get(@RequestBody RequestDTO requestDTO,
                                                              @RequestParam("page") int pageNum, @RequestParam("pageSize") int pageSize) {
        List<Long> list = conferenceService.get(requestDTO, pageNum, pageSize);
        List<ConferenceGettingDTO> res = new ArrayList<>();
        for (Long id : list) {
            res.add(conferenceService.getConference(id));
        }
    /**
     * 上传会议
     * @param id
     * @param file
     * @return
     */
    @PostMapping("/upload-record")
    public HttpResponseEntity<String> uploadRecord(@RequestParam("id") Long id, @RequestParam("video") MultipartFile file) {
        String res = conferenceService.uploadRecord(id, file);
        return new HttpResponseEntity<>(200, res, "success");
    }

    @GetMapping("/wxGet")
    public HttpResponseEntity<List<ConferenceWxDTO>> wxGet() {
        List<Long> list = conferenceService.wxGet();
        List<ConferenceWxDTO> res = new ArrayList<>();
        for (Long id : list) {
            res.add(conferenceService.getWxConference(id));
        }
        return new HttpResponseEntity<>(200, res, "success");
    @GetMapping("/get-record-text")
    public HttpResponseEntity<String> getRecordText(@RequestParam("id") Long id) {
        String res = conferenceService.getConferenceRecordTextById(id);
        return new HttpResponseEntity<>(200,res,"success");
    }

    @GetMapping("/delete")
    public HttpResponseEntity<String> delete(@RequestParam("id") Long id) {
        String res = conferenceService.delete(id);
        return new HttpResponseEntity<>(200, res, "success");

    @GetMapping("/timeline-status")
    public HttpResponseEntity<ConferenceTimelineVO>  getTimelineStatus(@RequestParam("conferenceId") Long id) {
        ConferenceTimelineVO res = conferenceService.getTimeLine(id);
        return new HttpResponseEntity<>(200,res,"success");
    }

    @PostMapping("/approve")
    public HttpResponseEntity<String> approve(@RequestParam("id") Long id) {
        String res = String.valueOf(conferenceService.approve(id));
        return new HttpResponseEntity<>(200, res, "success");
    @GetMapping("/get-minutes")
    public HttpResponseEntity<String> getMinutes(@RequestParam("conferenceId") Long id) {
        String res = conferenceService.getSummary(id);
        return new HttpResponseEntity<>(200,res,"success");
    }

    @GetMapping("/get-mindmap")
    public HttpResponseEntity<String> getMindMap(@RequestParam("conferenceId") Long id) {
        String res = conferenceService.getMindMap(id);
        return new HttpResponseEntity<>(200,res,"success");
    }

    @PostMapping("/generate-minutes")
    public HttpResponseEntity<String> generateMinutes(@RequestParam("conferenceId") Long id) {
        conferenceService.generateMinutes(id);
        return new HttpResponseEntity<>(200,"success","success");
    }
    @PostMapping("/generate-mindmap")
    public HttpResponseEntity<String> generateMindMap(@RequestParam("conferenceId") Long id) {
        conferenceService.generateMindMap(id);
        return new HttpResponseEntity<>(200,"success","success");
    }

    @PostMapping("/generate-transcription")
    public HttpResponseEntity<String> generateTranscription(@RequestParam("conferenceId") Long id) {
        conferenceService.videoTrans(id);
        return new HttpResponseEntity<>(200,"success","success");
    }


    // FIXME
    @GetMapping("/test/mindmap")
    public HttpResponseEntity<String> testGenerateMindMap(@RequestParam("id") Long id) {
//        Integer status = conferenceRecordMapper.getMindMapStatusById(id);
//        if(status.equals(ConferenceRecordConstants.NOT_DOING) || status.equals(ConferenceRecordConstants.FAILED)) {
//            difyService.updateMindMap(id);
//        }
        Integer status = conferenceRecordMapper.getSummaryStatusById(id);
        if(status.equals(ConferenceRecordConstants.NOT_DOING) || status.equals(ConferenceRecordConstants.FAILED)) {
            difyService.updateSummary(id);
        }
        return new HttpResponseEntity<>(200,"2","success");
    }
    @GetMapping("/test/upload")
    public HttpResponseEntity<String> testUpload(@RequestParam("id") Long id) {
        String video = conferenceRecordMapper.getVideoById(id);
        aliCloudService.uploadFile(video);
        return new HttpResponseEntity<>(200,"2","success");

    }
    @GetMapping("/test/url")
    public HttpResponseEntity<String> testUrl(@RequestParam("id") Long id) {
        String video = conferenceRecordMapper.getVideoById(id);
        String url = aliCloudService.getUrl(video);
        return new HttpResponseEntity<>(200,url,"success");

    }

    @GetMapping("/test/trans")
    public HttpResponseEntity<String> testTrans(@RequestParam("id") Long id) {
        conferenceService.videoTrans(id);
        return new HttpResponseEntity<>(200,"url","success");
    }

}
