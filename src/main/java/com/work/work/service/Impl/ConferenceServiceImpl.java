package com.work.work.service.Impl;

import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.ConferenceGetDTO;
import com.work.work.entity.Conference;
import com.work.work.entity.ConferenceMedia;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferenceMediaMapper;
import com.work.work.service.ConferenceService;
import com.work.work.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConferenceServiceImpl implements ConferenceService {
    private final MinioService minioService;
    private final ConferenceMediaMapper conferenceMediaMapper;
    @Autowired
    private ConferenceMapper conferenceMapper;
    @Autowired
    private ConferenceConverter conferenceConverter;

    public ConferenceServiceImpl(MinioService minioService, ConferenceMediaMapper conferenceMediaMapper) {
        this.minioService = minioService;
        this.conferenceMediaMapper = conferenceMediaMapper;
    }

    /**
     * 上传文件并将文件信息写入数据库
     *
     * @param uuid
     * @param file
     * @return
     */
    @Override
    public String uploadMedia(String uuid, MultipartFile file) {
        // 上传文件到minio
        String name = minioService.uploadFile(file);
        // 上传到数据库
        ConferenceMedia conferenceMedia = new ConferenceMedia(null, uuid, null, name);
        int res = conferenceMediaMapper.insert(conferenceMedia);
        if (res != 1) {
            throw new RuntimeException("插入失败");
        }
        return name;
    }

    @Override
    public Integer add(Conference conference, MultipartFile cover, String uuid) {
        String name = minioService.uploadFile(cover);
        // 插入数据库
        conference.setCover(name);
        int res = conferenceMapper.insertConference(conference);
        if (res != 1) {
            throw new RuntimeException("插入Conference失败");
        }
        //绑定媒体
        return conferenceMediaMapper.bindMedia(uuid, conference.getId());
    }

    @Override
    public ConferenceGetDTO getConferenceById(Long id) {
        Conference conference = conferenceMapper.selectConferenceById(id);
        String content = conference.getContent();
        // 匹配内容中的图片、视频URL
        String mediaTagRegex = "<(img|video|source)[^>]*src=['\"]([^'\"]+)['\"][^>]*>";
        Pattern pattern = Pattern.compile(mediaTagRegex);
        Matcher matcher = pattern.matcher(content);

        StringBuffer resultContent = new StringBuffer();
        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String fileName = matcher.group(2);
            // 生成 Minio 文件的访问 URL
            String fileUrl = minioService.getSignedUrl(fileName); // 使用正确的方法名
            // 替换原始标签中的文件名为 URL
            String newTag = fullTag.replace(fileName, fileUrl);
            matcher.appendReplacement(resultContent, Matcher.quoteReplacement(newTag));
        }
        matcher.appendTail(resultContent);

        // 创建DTO并设置替换后的内容
        ConferenceGetDTO conferenceGetDTO = conferenceConverter.conferenceToConferenceGetDTO(conference);
        // 设置替换后的内容
        conferenceGetDTO.setContent(resultContent.toString());
        // FIXME 替换成UserMapper相关方法，根据user Id获取name
        conferenceGetDTO.setUserName("admin");
        return conferenceGetDTO; // 添加返回语句

    }

    @Override
    public String getCover(Long id) {
        String name = conferenceMapper.selectCoverById(id);
        return minioService.getSignedUrl(name);
    }


    @Override
    @Transactional
    public Integer update(Conference conference, MultipartFile cover, String uuid) {
        // 根据 ID 获取旧的 Conference
        Conference oldConference = conferenceMapper.selectConferenceById(conference.getId());
        if (oldConference == null) {
            throw new RuntimeException("未找到旧的 Conference");
        }
        String oldCoverName = oldConference.getCover();
        List<String> oldMediaNames = conferenceMediaMapper.selectMediaNamesByConferenceId(conference.getId());

        // 上传新的 cover
        String newCoverName = minioService.uploadFile(cover);
        conference.setCover(newCoverName);

        // 更新 Conference 数据
        int res = conferenceMapper.updateConference(conference);
        if (res != 1) {
            throw new RuntimeException("更新 Conference 失败");
        }

        // 删除旧的 conference_media 数据库记录
        conferenceMediaMapper.deleteMediaByConferenceId(conference.getId());

        // 绑定新的媒体信息
        Integer bindRes = conferenceMediaMapper.bindMedia(uuid, conference.getId());

        // 数据库操作成功后再删除旧文件
        if (oldCoverName != null) {
            minioService.deleteFile(oldCoverName);
        }
        for (String oldMediaName : oldMediaNames) {
            minioService.deleteFile(oldMediaName);
        }

        return bindRes;
    }
}
