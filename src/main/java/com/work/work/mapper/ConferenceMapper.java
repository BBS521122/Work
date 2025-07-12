package com.work.work.mapper;


import com.work.work.dto.ConferenceWxDTO;
import com.work.work.dto.RequestDTO;
import com.work.work.entity.Conference;
import com.work.work.enums.ConferenceStateEnum;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    @Select("<script>" +
            "SELECT id\n" +
            "    FROM conference\n" +
            "    <where>\n" +
            "      <if test=\"keyword != null and keyword != ''\">\n" +
            "        AND name LIKE CONCAT('%', #{keyword}, '%')\n" +
            "      </if>\n" +
            "      <if test=\"state != null\">\n" +
            "        AND state = #{state}\n" +
            "      </if>\n" +
            "      <if test=\"startTime != null\">\n" +
            "        AND start_time &gt;= #{startTime}\n" +
            "      </if>\n" +
            "      <if test=\"endTime != null\">\n" +
            "          AND end_time &lt;= #{endTime}\n" +
            "      </if>\n" +
            "    </where>" +
            "</script>")
    List<Long> get(RequestDTO requestDTO);

    @Delete("DELETE FROM conference WHERE id = #{id}")
    int deleteConference(Long id);

    @Update("UPDATE conference SET state = 'APPROVED' WHERE id = #{id}")
    int approve(Long id);

    @Select("SELECT id, name, state, start_time AS startTime, end_time AS endTime " +
            "FROM conference WHERE state = #{state}")
    List<Conference> selectConferencesByState(ConferenceStateEnum state);

    @Update("UPDATE conference SET state = #{state} WHERE id = #{id}")
    int updateState(@Param("id") Long id, @Param("state") ConferenceStateEnum state);

    @Select("select id from conference where state ='COMPLETED'")
    List<Long> wxGet();

    @Select("SELECT name FROM conference WHERE id = #{id}")
    String getConferenceById(Long id);

//    @Insert("INSERT INTO test_union.conference (name, state, cover, start_time, end_time, content, user_id) " +
//            "VALUES (#{name}, #{state}, #{cover}, #{startTime}, #{endTime}, #{content}, #{userId})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
//    int insertConference(Conference conference);
//
//    @Select("SELECT id, name, state, cover, start_time AS startTime, end_time AS endTime, " +
//            "content, user_id AS userId " +
//            "FROM test_union.conference WHERE id = #{id}")
//    Conference selectConferenceById(Long id);
//
//    @Select("SELECT cover FROM test_union.conference WHERE id = #{id}")
//    String selectCoverById(Long id);
//
//    @Update("UPDATE test_union.conference SET name = #{name}, start_time = #{startTime}, end_time = #{endTime}, content = #{content}," +
//            "cover=#{cover} WHERE id = #{id}")
//    int updateConference(Conference conference);
//
//    @Select("<script>" +
//            "SELECT id\n" +
//            "    FROM test_union.conference\n" +
//            "    <where>\n" +
//            "      <if test=\"keyword != null and keyword != ''\">\n" +
//            "        AND name LIKE CONCAT('%', #{keyword}, '%')\n" +
//            "      </if>\n" +
//            "      <if test=\"state != null\">\n" +
//            "        AND state = #{state}\n" +
//            "      </if>\n" +
//            "      <if test=\"startTime != null\">\n" +
//            "        AND start_time &gt;= #{startTime}\n" +
//            "      </if>\n" +
//            "      <if test=\"endTime != null\">\n" +
//            "          AND end_time &lt;= #{endTime}\n" +
//            "      </if>\n" +
//            "    </where>" +
//            "</script>")
//    List<Long> get(RequestDTO requestDTO);
//
//    @Delete("DELETE FROM test_union.conference WHERE id = #{id}")
//    int deleteConference(Long id);
//
//    @Update("UPDATE test_union.conference SET state = 'APPROVED' WHERE id = #{id}")
//    int approve(Long id);
//
//    @Select("SELECT id, name, state, start_time AS startTime, end_time AS endTime " +
//            "FROM test_union.conference WHERE state = #{state}")
//    List<Conference> selectConferencesByState(String state);
//
//    @Update("UPDATE test_union.conference SET state = #{state} WHERE id = #{id}")
//    int updateState(@Param("id") Long id, @Param("state") String state);
//
//    @Select("select id from test_union.conference")
//    List<Long> wxGet();

}