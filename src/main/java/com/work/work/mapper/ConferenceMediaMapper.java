package com.work.work.mapper;


import com.work.work.entity.ConferenceMedia;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConferenceMediaMapper {
    /**
     * 插入conferenceMedia 记录
     *
     * @param conferenceMedia
     * @return
     */
    @Insert("INSERT INTO conference_media (uuid, conference_id, name) VALUES (#{uuid}, #{conferenceId}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ConferenceMedia conferenceMedia);

    @Update("UPDATE conference_media SET conference_id = #{id} WHERE uuid = #{uuid}")
    int bindMedia(String uuid, Long id);

    @Select("SELECT name FROM conference_media WHERE conference_id = #{id}")
    List<String> selectMediaNamesByConferenceId(Long id);

    @Delete("DELETE FROM conference_media WHERE conference_id = #{id}")
    int deleteMediaByConferenceId(Long id);
}
