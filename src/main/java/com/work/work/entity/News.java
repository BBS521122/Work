package com.work.work.entity;

import java.time.LocalDateTime;

public class News {
    private Long id;
    private String title;
    private String summary;
    private String content;  // @Lob removed
    private String imagePath;
    private String author;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer isDeleted = 0;
    private Integer sortOrder;
    private String status = "待审核";
    private Long tenantId;

    public News() {
    }

    public News(Long id, String title, String summary, String content, String imagePath, String author, LocalDateTime createdTime, LocalDateTime updatedTime, Integer isDeleted, Integer sortOrder, String status, Long tenantId) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.imagePath = imagePath;
        this.author = author;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.isDeleted = isDeleted;
        this.sortOrder = sortOrder;
        this.status = status;
        this.tenantId = tenantId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    // toString() method
    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", author='" + author + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", isDeleted=" + isDeleted +
                ", sortOrder=" + sortOrder +
                ", status='" + status + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}