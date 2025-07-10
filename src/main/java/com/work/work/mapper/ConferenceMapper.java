package com.work.work.mapper;


import com.work.work.entity.Conference;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ConferenceMapper {

    @Insert("INSERT INTO conference (name, state, cover, start_time, end_time, content, user_id) " +
            "VALUES (#{name}, #{state}, #{cover}, #{startTime}, #{endTime}, #{content}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertConference(Conference conference);

    @Select("SELECT id, name, state, cover, start_time AS startTime, end_time AS endTime, " +
            "content, user_id AS userId " +
            "FROM conference WHERE id = #{id}")
    Conference selectConferenceById(Long id);

    @Select("SELECT cover FROM conference WHERE id = #{id}")
    String selectCoverById(Long id);

    @Update("UPDATE conference SET name = #{name}, start_time = #{startTime}, end_time = #{endTime}, content = #{content}," +
            "cover=#{cover} WHERE id = #{id}")
    int updateConference(Conference conference);

}
