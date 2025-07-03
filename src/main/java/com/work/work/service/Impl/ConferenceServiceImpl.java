package com.work.work.service.Impl;

import com.github.pagehelper.PageHelper;
import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.ConferenceGettingDTO;
import com.work.work.dto.ConferenceWxDTO;
import com.work.work.dto.RequestDTO;
import com.work.work.entity.Conference;
import com.work.work.entity.ConferenceMedia;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferenceMediaMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.ConferenceService;
import com.work.work.service.MinioService;
import com.work.work.vo.UserVO;
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
    @Autowired
    private UserMapper userMapper;

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
        String name = userMapper.selectNameById(conference.getUserId());
        conferenceGetDTO.setUserName(name);
        return conferenceGetDTO; // 添加返回语句

    }

    @Override
    public ConferenceGettingDTO getConference(Long id) {
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
        ConferenceGettingDTO conferenceGettingDTO = conferenceConverter.conferenceToConferenceGettingDTO(conference);
        // 设置替换后的内容
        conferenceGettingDTO.setContent(resultContent.toString());
        String name = userMapper.selectNameById(conference.getUserId());
        conferenceGettingDTO.setUserName(name);
        return conferenceGettingDTO; // 添加返回语句

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

        // 判断 cover 是否为空
        String newCoverName;
        if (cover == null || cover.isEmpty()) {
            // 为空则用旧的 cover
            newCoverName = oldConference.getCover();
        } else {
            // 不为空则上传新 cover
            newCoverName = minioService.uploadFile(cover);
            // 只有上传新 cover 时才删除旧 cover
            if (oldConference.getCover() != null) {
                minioService.deleteFile(oldConference.getCover());
            }
        }
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

        for (String oldMediaName : oldMediaNames) {
            minioService.deleteFile(oldMediaName);
        }

        return bindRes;
    }

    @Override
    public List<Long> get(RequestDTO requestDTO, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Long> list = conferenceMapper.get(requestDTO);
        return list;
    }

    @Override
    @Transactional
    public String delete(Long id) {
        List<String> coverName = conferenceMediaMapper.selectMediaNamesByConferenceId(id);
        for (String name : coverName) {
            minioService.deleteFile(name);
        }
        return conferenceMapper.deleteConference(id) + conferenceMediaMapper.deleteMediaByConferenceId(id) + "";
    }

    @Override
    public int approve(Long id) {
        return conferenceMapper.approve(id);
    }

    @Override
    public List<Long> wxGet() {
        return conferenceMapper.wxGet();
    }

    @Override
    public ConferenceWxDTO getWxConference(Long id) {
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

        ConferenceWxDTO conferenceWxDTO = conferenceConverter.conferenceToConferenceWxDTO(conference);
        conferenceWxDTO.setContent(resultContent.toString());
//        String name = userMapper.selectNameById(conference.getUserId());
        conferenceWxDTO.setUserName("admin");
        conferenceWxDTO.setCover(minioService.getSignedUrl(conference.getCover()));
        return conferenceWxDTO;
    }
}