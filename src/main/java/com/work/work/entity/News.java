package com.work.work.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String summary;

    @Lob
    private String content;

    @Column(name = "image_path")
    private String imagePath;

    private String author;

    @Column(name = "created_time", insertable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", insertable = false, updatable = false)
    private LocalDateTime updatedTime;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0;
    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "status")
    private String status = "待审核";

    @Column(name = "tenant_id")
    private Long tenantId;
}