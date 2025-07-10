package com.work.work.mapper;

import com.work.work.dto.ConferenceTimelineDTO;
import com.work.work.entity.ConferenceRecord;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ConferenceRecordMapper {
    @Insert("INSERT INTO conference_record (conference_id, video) VALUES (#{conferenceId}, #{video})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertConferenceRecord(ConferenceRecord conferenceRecord);

    @Select("SELECT text FROM conference_record WHERE conference_id = #{id}")
    String getTextById(Long id);
    @Update("UPDATE conference_record SET text = #{text} WHERE conference_id = #{id}")
    int updateTextById(@Param("id") Long id, @Param("text") String text);

     @Update("UPDATE conference_record SET mind_map_status = #{status} WHERE conference_id = #{id}")
     int updateMindMapStatus(@Param("id") Long id, @Param("status") int status);

    @Select("SELECT mind_map_status FROM conference_record WHERE conference_id = #{id}")
    Integer getMindMapStatusById(@Param("id") Long id);

     @Update("UPDATE conference_record SET mind_map = #{mindMap} WHERE conference_id = #{id}")
     int updateMindMap(@Param("id") Long id, @Param("mindMap") String mindMap);

     @Select("SELECT mind_map FROM conference_record WHERE conference_id = #{id}")
     String getMindMapById(@Param("id") Long id);

     @Update("UPDATE conference_record SET summary_status = #{status} WHERE conference_id = #{id}")
     int updateSummaryStatus(@Param("id") Long id, @Param("status") int status);

     @Select("SELECT summary_status FROM conference_record WHERE conference_id = #{id}")
     Integer getSummaryStatusById(@Param("id") Long id);
     @Update("UPDATE conference_record SET summary = #{summary} WHERE conference_id = #{id}")
     int updateSummary(@Param("id") Long id, @Param("summary") String summary);
     @Select("SELECT summary FROM conference_record WHERE conference_id = #{id}")
     String getSummaryById(@Param("id") Long id);

    @Select("SELECT video FROM conference_record WHERE conference_id = #{id}")
    String getVideoById(Long id);

    @Select("SELECT upload_status FROM conference_record WHERE conference_id = #{id}")
    Integer getUploadById(@Param("id") Long id);

    @Update("UPDATE conference_record SET upload_status = #{status} WHERE conference_id = #{id}")
    int updateUpdateStatus(@Param("id") Long id, @Param("status") int status);

    @Update("UPDATE conference_record SET task_id = #{task} WHERE conference_id = #{id}")
    int updateTaskId(@Param("id") Long id, @Param("task") String task);

    @Select("SELECT task_id FROM conference_record WHERE conference_id = #{id}")
    String getTaskIdById(@Param("id") Long id);

    @Select("SELECT video, text, mind_map_status, summary_status FROM conference_record WHERE conference_id = #{id}")
    @Results({
        @Result(property = "video", column = "video"),
        @Result(property = "text", column = "text"),
        @Result(property = "mindMapStatus", column = "mind_map_status"),
        @Result(property = "summaryStatus", column = "summary_status")
    })
    ConferenceTimelineDTO selectTimelineByConferenceId(@Param("id") Long id);







}
