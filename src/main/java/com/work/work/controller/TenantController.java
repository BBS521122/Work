package com.work.work.controller;

import com.work.work.context.UserContext;
import com.work.work.converter.ConferenceConverter;
import com.work.work.converter.TenantConverter;
import com.work.work.dto.*;
import com.work.work.entity.Tenant;
import com.work.work.service.ConferenceService;
import com.work.work.service.TenantService;
import com.work.work.vo.HttpResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tenant")
@CrossOrigin
@Configuration
public class TenantController {

    @Autowired
    TenantService tenantService;
    @Autowired
    ConferenceService conferenceService;
    @Autowired
    ConferenceConverter conferenceConverter;
    @Autowired
    TenantConverter tenantConverter;


    /**
     * 添加新的租户信息
     *
     * @param tenantAddDTO
     * @param cover
     * @return
     */
    @PostMapping("/add")
    public HttpResponseEntity<Integer> add(@RequestPart("data") TenantAddDTO tenantAddDTO, @RequestPart("cover") MultipartFile cover) {
        System.out.println(tenantAddDTO.getName());
        Tenant tenant = tenantConverter.tenantAddDTOToTenant(tenantAddDTO);
        System.out.println(tenant.getName());
        tenant.setId(UserContext.getUserId());
        Integer res = tenantService.add(tenant, cover, tenantAddDTO.getUuid());
        // 业务处理
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 更新会议信息
     *
     * @param tenantUpdateDTO
     * @param cover
     * @return
     */
    @PostMapping("/update")
    public HttpResponseEntity<Integer> update(@RequestPart("data") TenantUpdateDTO tenantUpdateDTO, @RequestPart(value = "cover", required = false) MultipartFile cover) {
        Tenant tenant = tenantConverter.tenantUpdateDTOToTenant(tenantUpdateDTO);
        Integer res = tenantService.update(tenant, cover, tenantUpdateDTO.getUuid());
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 获取指定id的会议信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get-info")
    public HttpResponseEntity<TenantGetDTO> getInfo(@RequestParam("id") Long id) {
        TenantGetDTO res = tenantService.getTenantById(id);
        return new HttpResponseEntity<>(200, res, "success");
    }

    /**
     * 获取指定id的会议封面
     *
     * @param id
     * @return
     */
    @GetMapping("get-cover")
    public HttpResponseEntity<String> getCover(@RequestParam("id") Long id) {
        String res = tenantService.getCover(id);
        return new HttpResponseEntity<>(200, res, "success");
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
        String name = tenantService.uploadMedia(uuid, file);
        return new HttpResponseEntity<>(200, name, "success");
    }

    @GetMapping("/delete")
    public HttpResponseEntity<Integer> delete(@RequestParam("id") Long id) {
        int res = tenantService.delete(id);
        return new HttpResponseEntity<>(200, res, "success");
    }

    @PostMapping("/get")
    public HttpResponseEntity<List<Tenant>> get(@RequestBody SearchDTO searchDTO,
                                                @RequestParam("page") int pageNum, @RequestParam("pageSize") int pageSize) {
        List<Long> list = tenantService.get(searchDTO, pageNum, pageSize);
        List<Tenant> res = new ArrayList<>();
        for (Long id : list) {
            res.add(tenantService.getTenant(id));
            System.out.println(tenantService.getTenant(id).getContactPerson());
        }
        return new HttpResponseEntity<>(200, res, "success");
    }

    @PostMapping("/batch-delete")
    public HttpResponseEntity<String> batchDelete(@RequestBody Map<String, List<String>> request) {
        List<String> ids = request.get("ids");
        StringBuilder res = new StringBuilder();
        for (String id : ids) {
            try {
                long number = Long.parseLong(id);
                res.append(tenantService.delete(number));
            } catch (NumberFormatException e) {
                res.append("Invalid ID format: ").append(id).append("; ");
            }
        }
        return new HttpResponseEntity<>(200, res.toString(), "success");
    }
}
