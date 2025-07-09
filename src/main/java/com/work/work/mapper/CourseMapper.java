package com.work.work.mapper;



import com.work.work.entity.Course;
import org.apache.ibatis.annotations.*;


import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("SELECT id, course_name, course_description, course_author, " +
            "course_sort, " +
            "course_create_time, course_update_time, state,cover_url " +
            "FROM course")
    List<Course> getallCourse();

    @Select("select * from course where id = #{id}")
    Course getCourseById(Long courseId);

//批量删除方法
    @Delete("<script>" +
            "DELETE FROM course WHERE id IN " +
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteCourses(List<Long> courseIds);


    @Delete("DELETE FROM course WHERE id = #{id}")
    int deleteCourseById(@Param("id") Long id);


    @Insert("INSERT INTO course (course_name, course_description, course_author, " +
            "course_sort, course_create_time, course_update_time,cover_url) " +
            "VALUES ( #{courseName}, #{courseDescription}, #{courseAuthor}, " +
            "#{courseSort},#{courseCreateTime}, #{courseUpdateTime},#{coverUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addCourse(Course course);

    @Update("UPDATE course SET " +
            "id = #{id}, " +
            "course_name = #{courseName}, " +
            "course_description = #{courseDescription}, " +
            "course_author = #{courseAuthor}, " +
            "course_sort = #{courseSort}, " +
            "course_update_time = #{courseUpdateTime}," +
            "cover_url = #{coverUrl}" +
            "WHERE id = #{id}")
    int updateCourse(Course course);


    // 获取待审核课程
    // 获取待审核课程（状态为0）
    @Select("SELECT * FROM course WHERE state = 0")
    List<Course> getPendingCourses();

    // 更新审核状态
    @Update("UPDATE course SET state = #{state} WHERE id = #{id}")
    int updateAuditStatus(@Param("id") int id,
                          @Param("state") int state);

    // CourseMapper.java
// 获取移动端课程列表
    @Select("SELECT id, course_name, course_description, course_author, cover_url " +
            "FROM course " +
            "WHERE state = 1 " + // 只获取已审核通过的课程
            "ORDER BY course_sort DESC")
    List<Course> getMobileCourses();

    // 获取移动端课程详情
    @Select("SELECT id, course_name, course_description " +
            "FROM course " +
            "WHERE id = #{id} AND state = 1")
    Course getMobileCourseInfo(@Param("id") Long id);

    @Update("update course set course_create_time = #{time}")
    int updateTimeById(Long id, LocalDateTime time);
}
