package com.work.work.service.Impl;

import com.github.pagehelper.PageHelper;
import com.work.work.converter.TenantConverter;
import com.work.work.dto.SearchDTO;
import com.work.work.dto.TenantGetDTO;
import com.work.work.entity.Tenant;
import com.work.work.entity.TenantMedia;
import com.work.work.mapper.sql.TenantMapper;
import com.work.work.mapper.sql.TenantMediaMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.MinioService;
import com.work.work.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TenantServiceImpl implements TenantService {
    @Autowired
    MinioService minioService;
    @Autowired
    TenantMapper tenantMapper;
    @Autowired
    TenantMediaMapper tenantMediaMapper;
    @Autowired
    TenantConverter tenantConverter;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Integer add(Tenant tenant, MultipartFile cover, String uuid) {
        String name = minioService.uploadFile(cover);
        // 插入数据库
        tenant.setCover(name);
        int res = tenantMapper.insertTenant(tenant);
        if (res != 1) {
            throw new RuntimeException("插入Tenant失败");
        }
        //绑定媒体
        return tenantMediaMapper.bindMedia(uuid, tenant.getId());
    }

    @Override
    @Transactional
    public Integer update(Tenant tenant, MultipartFile cover, String uuid) {
        // 根据 ID 获取旧的 Conference
        Tenant oldTenant = tenantMapper.selectTenantById(tenant.getId());
        if (oldTenant == null) {
            throw new RuntimeException("未找到旧的 Tenant");
        }
        String oldCoverName = oldTenant.getCover();
        List<String> oldMediaNames = tenantMediaMapper.selectMediaNamesByTenantId(tenant.getId());

        // 判断 cover 是否为空
        String newCoverName;
        if (cover == null || cover.isEmpty()) {
            // 为空则用旧的 cover
            newCoverName = oldTenant.getCover();
        } else {
            // 不为空则上传新 cover
            newCoverName = minioService.uploadFile(cover);
            // 只有上传新 cover 时才删除旧 cover
            if (oldTenant.getCover() != null) {
                minioService.deleteFile(oldTenant.getCover());
            }
        }
        tenant.setCover(newCoverName);

        // 更新 Conference 数据
        int res = tenantMapper.updateTenant(tenant);
        if (res != 1) {
            throw new RuntimeException("更新 Tenant 失败");
        }

        // 删除旧的 conference_media 数据库记录
        tenantMediaMapper.deleteMediaByTenantId(tenant.getId());

        // 绑定新的媒体信息
        Integer bindRes = tenantMediaMapper.bindMedia(uuid, tenant.getId());

        for (String oldMediaName : oldMediaNames) {
            minioService.deleteFile(oldMediaName);
        }

        return bindRes;
    }

    @Override
    public TenantGetDTO getTenantById(Long id) {
        Tenant tenant = tenantMapper.selectTenantById(id);
        String note = tenant.getNote();
        // 匹配内容中的图片、视频URL
        String mediaTagRegex = "<(img|video|source)[^>]*src=['\"]([^'\"]+)['\"][^>]*>";
        Pattern pattern = Pattern.compile(mediaTagRegex);
        Matcher matcher = pattern.matcher(note);

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
        TenantGetDTO tenantGetDTO = tenantConverter.conferenceToTenantGetDTO(tenant);
        // 设置替换后的内容
        tenantGetDTO.setNote(resultContent.toString());

        return tenantGetDTO; // 添加返回语句
    }

    @Override
    public String getCover(Long id) {
        String name = tenantMapper.selectCoverById(id);
        return minioService.getSignedUrl(name);
    }

    @Override
    public String uploadMedia(String uuid, MultipartFile file) {
        // 上传文件到minio
        String name = minioService.uploadFile(file);
        // 上传到数据库
        TenantMedia tenantMedia = new TenantMedia(null, uuid, null, name);
        int res = tenantMediaMapper.insert(tenantMedia);
        if (res != 1) {
            throw new RuntimeException("插入失败");
        }
        return name;
    }

    @Override
    public List<Long> get(SearchDTO searchDTO, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Long> list = tenantMapper.get(searchDTO);
        return list;
    }

    @Override
    public Tenant getTenant(Long id) {
        Tenant tenant = tenantMapper.selectTenantById(id);
        System.out.println("test:"+tenant.getContactPerson());

        String note = tenant.getNote();
        // 匹配内容中的图片、视频URL
        String mediaTagRegex = "<(img|video|source)[^>]*src=['\"]([^'\"]+)['\"][^>]*>";
        Pattern pattern = Pattern.compile(mediaTagRegex);
        Matcher matcher = pattern.matcher(note);

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

        tenant.setNote(resultContent.toString());

        return tenant;
    }

    @Override
    @Transactional
    public int delete(Long id) {
        List<String> coverName = tenantMediaMapper.selectMediaNamesByTenantId(id);
        for (String name : coverName) {
            minioService.deleteFile(name);
        }
        return tenantMapper.deleteTenant(id) + tenantMediaMapper.deleteMediaByTenantId(id);
    }
}
