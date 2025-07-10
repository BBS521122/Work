package com.work.work.mapper;

import com.work.work.entity.Chapter;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface ChapterMapper {


    @Select("SELECT id, course_id, video_url, name, `order` " +
            "FROM chapter " +
            "WHERE course_id = #{courseId} " +
            "ORDER BY `order` ASC") // 确保按顺序返回
    List<Chapter> getChaptersByCourseId(@Param("courseId") Long courseId);


    @Select("SELECT id, course_id, video_url, name, `order` " +
            "FROM chapter " +
            "WHERE id = #{chapterId} AND course_id = #{courseId}")
    Chapter getChapterByCourseAndId(@Param("courseId") Long courseId,
                                    @Param("chapterId") Long chapterId);

    @Select("SELECT video_url " +
            "FROM chapter " +
            "WHERE name = #{name}")
    String getVideoByCourseAndId(@Param("name") String name);



    //新增章节
    @Insert("INSERT INTO chapter (course_id, video_url, name, `order`) " +
            "VALUES (#{courseId}, #{videoUrl}, #{name}, #{order})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertChapter(Chapter chapter);



    //更新章节
    @Update("UPDATE chapter SET " +
            "video_url = #{videoUrl}, " +
            "name = #{name}, " +
            "`order` = #{order} " +
            "WHERE id = #{id} AND course_id = #{courseId}")
    int updateChapter(Chapter chapter);



    //删除特定课程的特定章节
    @Delete("DELETE FROM chapter " +
            "WHERE id = #{chapterId} AND course_id = #{courseId}")
    int deleteChapterByCourseAndId(@Param("courseId") Long courseId,
                                   @Param("chapterId") Long chapterId);


    //删除章节的方法
    @Delete("DELETE FROM chapter WHERE course_id = #{courseId}")
    int deleteChaptersByCourseId(@Param("courseId") Long courseId);

    //删除章节的方法
    @Delete("DELETE FROM chapter WHERE name = #{name}")
    int deleteChaptersByName(@Param("name") String name);


}





