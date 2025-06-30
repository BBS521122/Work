package com.work.work.mapper.sql;

import com.work.work.entity.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsMapper {

    @Insert("""
    INSERT INTO news 
    (title, summary, content, image_path, author, created_time, updated_time, is_deleted, sort_order, status, tenant_id)
    VALUES 
    (#{title}, #{summary}, #{content}, #{imagePath}, #{author}, NOW(), NOW(), 0, #{sortOrder}, #{status}, #{tenantId})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(News news);

    @Select("SELECT * FROM news WHERE status = #{status} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<News> selectByStatus(@Param("status") String status);

    @Select("SELECT * FROM news WHERE is_deleted = 0 ORDER BY created_time DESC")
    List<News> selectAll();

    @Update("""
    UPDATE news SET title=#{title}, summary=#{summary}, content=#{content},
    image_path=#{imagePath}, author=#{author}, updated_time=NOW(),
    sort_order=#{sortOrder}, status=#{status}
    WHERE id=#{id} AND is_deleted=0
    """)
    int update(News news);


    @Update("UPDATE news SET is_deleted=1 WHERE id=#{id}")
    int softDelete(@Param("id") Long id);

    @Update("UPDATE news SET is_deleted=0 WHERE id=#{id}")
    int restore(@Param("id") Long id);


    @Delete("DELETE FROM news WHERE id=#{id} AND is_deleted=1")
    int hardDelete(@Param("id") Long id);

    @Select("SELECT * FROM news WHERE id=#{id} AND is_deleted=0")
    News selectById(@Param("id") Long id);

    @Select("SELECT * FROM news WHERE id=#{id}")
    News selectByIdIncludingDeleted(@Param("id") Long id);
    /**
     * 批量恢复新闻，将 is_deleted 置为0
     */
    @Update("<script>" +
            "UPDATE news SET is_deleted=0 WHERE id IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    int restoreBatch(@Param("list") List<Long> ids);
    @Select("SELECT * FROM news WHERE is_deleted=1 ORDER BY updated_time DESC")
    List<News> selectDeleted();

    @Select("SELECT * FROM news WHERE is_deleted=1 AND tenant_id = #{tenantId} ORDER BY updated_time DESC")
    List<News> selectDeletedByTenant(Long tenantId);

    /**
     * 批量彻底删除新闻（物理删除）
     */
    @Delete("<script>" +
            "DELETE FROM news WHERE id IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            " AND is_deleted=1" +
            "</script>")
    int hardDeleteBatch(@Param("list") List<Long> ids);

    @Update("""
    UPDATE news SET sort_order = sort_order + 1
    WHERE sort_order >= #{start} AND sort_order <= #{end} AND id != #{excludeId}
    """)
    int incrementSortOrderRange(@Param("start") int start,
                                @Param("end") int end,
                                @Param("excludeId") Long excludeId);

    @Update("""
    UPDATE news SET sort_order = sort_order - 1
    WHERE sort_order >= #{start} AND sort_order <= #{end} AND id != #{excludeId}
    """)
    int decrementSortOrderRange(@Param("start") int start,
                                @Param("end") int end,
                                @Param("excludeId") Long excludeId);


    @Update("UPDATE news SET sort_order=#{sortOrder} WHERE id=#{id}")
    void updateSortOrderById(@Param("id") Long id, @Param("sortOrder") int sortOrder);
    @Select("SELECT * FROM news WHERE sort_order > #{order} AND is_deleted = 0 AND status = '已通过' ORDER BY sort_order ASC")
    List<News> selectPassedBySortOrderGreaterThan(@Param("order") int order);


    @Select("SELECT * FROM news WHERE sort_order >= #{order} AND is_deleted = 0 AND status = '已通过' ORDER BY sort_order DESC")
    List<News> selectPassedBySortOrderGreaterThanEqual(@Param("order") int order);
    @Update("UPDATE news SET status=#{status}, updated_time=NOW() WHERE id=#{id} AND is_deleted = 0")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    @Select("SELECT MAX(sort_order) FROM news WHERE is_deleted=0 AND status = '已通过'")
    Integer selectMaxSortOrder();
    @Select("SELECT * FROM news WHERE tenant_id = #{tenantId} AND is_deleted = 0 ORDER BY updated_time DESC")
    List<News> selectByTenantId(@Param("tenantId") Long tenantId);

}


