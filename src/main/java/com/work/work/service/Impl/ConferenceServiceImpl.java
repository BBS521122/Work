package com.work.work.service.Impl;

import com.github.pagehelper.PageHelper;
import com.work.work.constants.ConferenceRecordConstants;
import com.work.work.constants.ConferenceTimelineConstants;
import com.work.work.converter.ConferenceConverter;
import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.ConferenceGettingDTO;
import com.work.work.dto.ConferenceWxDTO;
import com.work.work.dto.RequestDTO;
import com.work.work.dto.ConferenceTimelineDTO;
import com.work.work.dto.MultipartFileWrapper;
import com.work.work.entity.Conference;
import com.work.work.entity.ConferenceMedia;
import com.work.work.entity.ConferenceRecord;
import com.work.work.mapper.ConferenceMapper;
import com.work.work.mapper.ConferenceMediaMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.mapper.ConferenceRecordMapper;
import com.work.work.service.AliCloudService;
import com.work.work.service.ConferenceService;
import com.work.work.service.DifyService;
import com.work.work.service.MinioService;
import com.work.work.vo.UserVO;
import com.work.work.utils.DifyUtils;
import com.work.work.vo.ConferenceTimelineVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConferenceServiceImpl implements ConferenceService {
    private final MinioService minioService;
    private final ConferenceMediaMapper conferenceMediaMapper;
    @Autowired
    private ConferenceMapper conferenceMapper;
    @Autowired
    ConferenceRecordMapper conferenceRecordMapper;
    @Autowired
    private ConferenceConverter conferenceConverter;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    AliCloudService aliCloudService;
    @Autowired
    DifyService difyService;

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

    @Override
    public String uploadRecord(Long id, MultipartFile file) {
        // 获取文件的 Content-Type
        String contentType = file.getContentType();
        if (contentType != null && contentType.contains(";")) {
            // 去掉 `;` 之后的内容
            contentType = contentType.split(";")[0];
        }

        // 创建一个新的 MultipartFile 包装对象，修改 Content-Type
        MultipartFile processedFile = new MultipartFileWrapper(file, contentType);

        // 上传文件到 MinIO
        String name = minioService.uploadFile(processedFile);

        // 插入记录到数据库
        conferenceRecordMapper.insertConferenceRecord(new ConferenceRecord(null, id, name, null));
        return name;
    }

    @Override
    public String getConferenceRecordTextById(Long id) {
        String name = conferenceRecordMapper.getTextById(id);
        if (name == null) {
            return null;
        }
        return minioService.getSignedUrl(name);

    }

    @Override
    @Async
    public void videoTrans(Long id) {
        Integer upload = conferenceRecordMapper.getUploadById(id);
        String video = conferenceRecordMapper.getVideoById(id);
        if (video == null) {
            return;
        }
        if (upload.equals(ConferenceRecordConstants.NOT_DOING)) {
            aliCloudService.uploadFile(video);
            conferenceRecordMapper.updateUpdateStatus(id, ConferenceRecordConstants.COMPLETED);
        }
        String taskId = conferenceRecordMapper.getTaskIdById(id);
        if (taskId == null || taskId.isEmpty()) {
            String url = aliCloudService.getUrl(video);
            String task = aliCloudService.submitTrans(url);
            conferenceRecordMapper.updateTaskId(id, task);
            taskId = task;
        }
        String row = aliCloudService.getTrans(taskId);
        String trans = DifyUtils.extractTextConcat(row);
        String fileName = UUID.randomUUID() + ".txt";
        File tempFile = null;
        try {
            tempFile = File.createTempFile(fileName, null);
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(trans);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        minioService.uploadTextFile(tempFile, fileName);
        // FIXME
        conferenceRecordMapper.updateTextById(id, fileName);
    }

    @Override
    public ConferenceTimelineVO getTimeLine(Long id) {
        ConferenceTimelineDTO conferenceTimelineDTO = conferenceRecordMapper.selectTimelineByConferenceId(id);
        Conference conference = conferenceMapper.selectConferenceById(id);
        LocalDateTime startTime = conference.getStartTime();
        LocalDateTime endTime = conference.getEndTime();
        String hasRecording = ConferenceTimelineConstants.NOT_DOING;
        String hasTranscription = ConferenceTimelineConstants.NOT_DOING;
        String hasMinutes = ConferenceTimelineConstants.NOT_DOING;
        String hasMindMap = ConferenceTimelineConstants.NOT_DOING;
        String recordingUrl = minioService.getSignedUrl(conferenceTimelineDTO.getVideo());
        if (conferenceTimelineDTO.getVideo() != null && !conferenceTimelineDTO.getVideo().isEmpty()) {
            hasRecording = ConferenceTimelineConstants.COMPLETED;
        }
        if (conferenceTimelineDTO.getText() != null && !conferenceTimelineDTO.getText().isEmpty()) {
            hasTranscription = ConferenceTimelineConstants.COMPLETED;
        }
        if (conferenceTimelineDTO.getSummaryStatus().equals(ConferenceRecordConstants.DOING)) {
            hasMinutes = ConferenceTimelineConstants.PROCESSING;
        } else if (conferenceTimelineDTO.getSummaryStatus().equals(ConferenceRecordConstants.COMPLETED)) {
            hasMinutes = ConferenceTimelineConstants.COMPLETED;
        }
        if (conferenceTimelineDTO.getMindMapStatus().equals(ConferenceRecordConstants.DOING)) {
            hasMindMap = ConferenceTimelineConstants.PROCESSING;
        } else if (conferenceTimelineDTO.getMindMapStatus().equals(ConferenceRecordConstants.COMPLETED)) {
            hasMindMap = ConferenceTimelineConstants.COMPLETED;
        }
        return new ConferenceTimelineVO(startTime, endTime, hasRecording, hasTranscription, hasMinutes, hasMindMap, recordingUrl);
    }

    @Override
    public String getSummary(Long id) {
        return conferenceRecordMapper.getSummaryById(id);
    }

    @Override
    public String getMindMap(Long id) {
        return conferenceRecordMapper.getMindMapById(id);
    }

    @Override
    public void generateMinutes(Long id) {
        Integer status = conferenceRecordMapper.getSummaryStatusById(id);
        if(status.equals(ConferenceRecordConstants.NOT_DOING) || status.equals(ConferenceRecordConstants.FAILED)) {
            difyService.updateSummary(id);
        }
    }

    @Override
    public void generateMindMap(Long id) {
        Integer status = conferenceRecordMapper.getMindMapStatusById(id);
        if(status.equals(ConferenceRecordConstants.NOT_DOING) || status.equals(ConferenceRecordConstants.FAILED)) {
            difyService.updateMindMap(id);
        }
    }


}
