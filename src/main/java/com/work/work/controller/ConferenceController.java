package com.work.work.controller;

import com.work.work.context.UserContext;
import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.ConferenceAddDTO;
import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.ConferenceUpdateDTO;
import com.work.work.entity.Conference;
import com.work.work.service.ConferenceService;
import com.work.work.vo.HttpResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/conference")
@CrossOrigin
public class ConferenceController {
    @Autowired
    ConferenceService conferenceService;
    @Autowired
    ConferenceConverter conferenceConverter;


    /**
     * 添加新的会议信息
     * @param conferenceAddDTO
     * @param cover
     * @return
     */
    @PostMapping("/add")
    public HttpResponseEntity<Integer> add(@RequestPart("data") ConferenceAddDTO conferenceAddDTO, @RequestPart("cover") MultipartFile cover) {
        Conference conference = conferenceConverter.conferenceAddDTOToConference(conferenceAddDTO);
        //FIXME Only For Test 实际使用Context获取Id
//        conference.setUserId(UserContext.getUserId());
        conference.setUserId(1L);
        Integer res = conferenceService.add(conference, cover, conferenceAddDTO.getUuid());
        // 业务处理
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 更新会议信息
     * @param conferenceUpdateDTO
     * @param cover
     * @return
     */
    @PostMapping("/update")
    public HttpResponseEntity<Integer> update(@RequestPart("data") ConferenceUpdateDTO conferenceUpdateDTO, @RequestPart("cover") MultipartFile cover) {
        Conference conference = conferenceConverter.conferenceUpdateDTOToConference(conferenceUpdateDTO);
        Integer res = conferenceService.update(conference, cover, conferenceUpdateDTO.getUuid());
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 获取指定id的会议信息
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
     * @param uuid
     * @param file
     * @return
     */
    @PostMapping("/upload-media")
    public HttpResponseEntity<String> uploadMedia(@RequestParam("uuid") String uuid, @RequestParam("file") MultipartFile file) {
        String name = conferenceService.uploadMedia(uuid, file);
        return new HttpResponseEntity<>(200, name, "success");
    }



}
