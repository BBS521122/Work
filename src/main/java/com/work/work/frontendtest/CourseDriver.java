package com.work.work.frontendtest;

import com.work.work.service.MinioService;
import com.work.work.vo.HttpResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseDriver {
    @Autowired
    MinioService minioService;
    @GetMapping("/mobile/get-courses")
    public HttpResponseEntity<List<CourseOverviewMobileVO>> getMobileCourses() {
        List<CourseOverviewMobileVO> res = new ArrayList<>();
        String url1 = minioService.getSignedUrl("5215561c-613f-4a5a-961d-70563315dc44.png");
        String url2 = minioService.getSignedUrl("967e4157-81c5-4b2b-804e-b391d6d29ad3.jpg");
        CourseOverviewMobileVO courseOverviewMobileVO1 = new CourseOverviewMobileVO(1L,url1,"为什么JWT不行了","D.afters");
        CourseOverviewMobileVO courseOverviewMobileVO2 = new CourseOverviewMobileVO(2L,url2,"天气指数，我能行","Weather www");
        CourseOverviewMobileVO courseOverviewMobileVO3 =new CourseOverviewMobileVO(3L,url1,"JWT不会辜负单点登录，单点登录也不会辜负JWT","JWT win win win");
        res.add(courseOverviewMobileVO1);
        res.add(courseOverviewMobileVO2);
        res.add(courseOverviewMobileVO3);
        return new HttpResponseEntity<>(200,res,"success");
    }
}
