package com.work.work.service;

import com.work.work.dto.*;
import com.work.work.entity.Tenant;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TenantService {

    Integer add(Tenant tenant, MultipartFile cover, String uuid);

    Integer update(Tenant tenant, MultipartFile cover, String uuid);

    TenantGetDTO getTenantById(Long id);

    String getCover(Long id);

    String uploadMedia(String uuid, MultipartFile file);

    List<Long> get(SearchDTO searchDTO, int pageNum, int pageSize);

    Tenant getTenant(Long id);

    String delete(Long id);
}
