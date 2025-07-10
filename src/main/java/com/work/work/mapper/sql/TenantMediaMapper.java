package com.work.work.mapper.sql;

import com.work.work.entity.TenantMedia;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TenantMediaMapper {

    @Update("UPDATE tenant_media SET tenant_id = #{id} WHERE uuid = #{uuid}")
    int bindMedia(String uuid, Long id);

    @Select("SELECT name FROM tenant_media WHERE tenant_id = #{id}")
    List<String> selectMediaNamesByTenantId(Long id);

    @Delete("DELETE FROM tenant_media WHERE tenant_id = #{id}")
    int deleteMediaByTenantId(Long id);

    @Insert("INSERT INTO tenant_media (uuid, tenant_id, name) VALUES (#{uuid}, #{tenant_id}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TenantMedia tenantMedia);
}
