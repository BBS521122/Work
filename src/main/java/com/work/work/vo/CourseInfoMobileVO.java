package com.work.work.vo;

import java.util.List;

public class CourseInfoMobileVO {
    private String title;
    private String description;
    private List<CourseIndexMobileVO> list;

    public CourseInfoMobileVO() {
    }

    public CourseInfoMobileVO(String title, String description, List<CourseIndexMobileVO> list) {
        this.title = title;
        this.description = description;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CourseIndexMobileVO> getList() {
        return list;
    }

    public void setList(List<CourseIndexMobileVO> list) {
        this.list = list;
    }
}
