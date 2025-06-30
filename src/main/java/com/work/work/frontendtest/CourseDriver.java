package com.work.work.frontendtest;

import com.work.work.service.MinioService;
import com.work.work.vo.HttpResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseDriver {
    @Autowired
    MinioService minioService;
    @GetMapping("/mobile/get-courses")
    public HttpResponseEntity<List<CourseOverviewMobileVO>> getMobileCourses(@RequestParam String query) {
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

    @GetMapping("/course/mobile/get-info")
    public HttpResponseEntity<CourseInfoMobileVO>  getCourseInfo(@RequestParam Long id){
        String title = "JWT不相信眼泪";
        String description="在数字世界的暗流中，JWT（JSON Web Token）是冷酷的通行证，它用签名加密信任，用过期时间划定忠诚。这里没有共情，" +
                "只有验证——解码失败？401 Unauthorized；令牌失效？请重新登录。开发者对着控制台的红字叹息，但JWT不会妥协，它像算法界的判官，" +
                "只认payload和secret，不认眼泪。" +
                "\n" + "有人抱怨它苛刻，却忘了脆弱性总藏在松弛的逻辑里。JWT的规则写满棱角：别信任未签名的请求，别泄露你的密钥。它用沉默宣告：" +
                "安全的世界里，感性是漏洞，而纪律才是盾牌。";
        List<CourseIndexMobileVO> list = new ArrayList<>();
        String signedUrl1 = minioService.getSignedUrl("08af892f-c43e-4cca-a6a4-384f1557ccff.mp4");
        String signedUrl2 = minioService.getSignedUrl("0b17609b-c302-4326-a45d-b6485a9f4195.mp4");
        CourseIndexMobileVO courseIndexMobileVO1 = new CourseIndexMobileVO(1,"什么是jwt",signedUrl1);
        CourseIndexMobileVO courseIndexMobileVO2 = new CourseIndexMobileVO(2,"为什么要用jwt",signedUrl2);
        list.add(courseIndexMobileVO1);
        list.add(courseIndexMobileVO2);
        CourseInfoMobileVO res = new CourseInfoMobileVO(title, description, list);
        return new HttpResponseEntity<>(200,res,"success");
    }

}
