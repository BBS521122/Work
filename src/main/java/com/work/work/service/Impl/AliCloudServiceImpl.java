package com.work.work.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.work.work.properties.AliCloudProperties;
import com.work.work.service.AliCloudService;
import com.work.work.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class AliCloudServiceImpl implements AliCloudService {
    @Autowired
    AliCloudProperties aliCloudProperties;
    @Autowired
    MinioService minioService;

    @Override
    public void uploadFile(String name) {
        String endpoint = aliCloudProperties.getEndpoint();
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }

        String bucketName = aliCloudProperties.getBucketName();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
        String region = "cn-beijing";

        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, name, minioService.getFile(name));
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public String getUrl(String name) {
        String res = null;
        String endpoint = aliCloudProperties.getEndpoint();
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }

        String bucketName = aliCloudProperties.getBucketName();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
        String region = "cn-beijing";

        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 设置预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Date expiration = new Date(new Date().getTime() + 5 * 3600 * 1000L);
            // 生成以GET方法访问的预签名URL。本示例没有额外请求头，其他人可以直接通过浏览器访问相关内容。
            URL url = ossClient.generatePresignedUrl(bucketName, name, expiration);
            System.out.println(url);
            res = url.toString();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return res;
    }

    @Override
    public String submitTrans(String url) {
        final String accessKeyId = aliCloudProperties.getAk();
        final String accessKeySecret = aliCloudProperties.getSk();
        /**
         * 地域ID
         */
        final String regionId = "cn-shanghai";
        final String endpointName = "cn-shanghai";
        final String product = "nls-filetrans";
        final String domain = "filetrans.cn-shanghai.aliyuncs.com";

        IAcsClient client;
        // 设置endpoint
        try {
            DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
        } catch (ClientException e) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + e.getMessage());
        }
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        CommonRequest postRequest = new CommonRequest();
        postRequest.setDomain("filetrans.cn-shanghai.aliyuncs.com"); // 设置域名，固定值。
        postRequest.setVersion("2018-08-17");         // 设置中国站的版本号。
        // postRequest.setVersion("2019-08-23");         // 设置国际站的版本号，国际站用户请设置此值。
        postRequest.setAction("SubmitTask");          // 设置action，固定值。
        postRequest.setProduct("nls-filetrans");      // 设置产品名称，固定值。
        // 设置录音文件识别请求参数，以JSON字符串的格式设置到请求Body中。
        JSONObject taskObject = new JSONObject();
        taskObject.put("appkey", aliCloudProperties.getAppkey());
        taskObject.put("file_link", url);  // 设置录音文件的链接
        taskObject.put("version", "4.0");  // 新接入请使用4.0版本，已接入（默认2.0）如需维持现状，请注释掉该参数设置。
        taskObject.put("enable_sample_rate_adaptive", true);
        String task = taskObject.toJSONString();
        postRequest.putBodyParameter("Task", task);  // 设置以上JSON字符串为Body参数。
        postRequest.setMethod(MethodType.POST);      // 设置为POST方式请求。
        //postRequest.setHttpContentType(FormatType.JSON);    //当aliyun-java-sdk-core 版本为4.6.0及以上时，请取消该行注释
        /**
         * 提交录音文件识别请求
         */
        String taskId = "";   // 获取录音文件识别请求任务的ID，以供识别结果查询使用。
        CommonResponse postResponse = null;
        try {
            postResponse = client.getCommonResponse(postRequest);
        } catch (ClientException e) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + e.getMessage());
        }
        if (postResponse.getHttpStatus() == 200) {
            JSONObject result = JSONObject.parseObject(postResponse.getData());
            String statusText = result.getString("StatusText");
            if ("SUCCESS".equals(statusText)) {
                System.out.println("录音文件识别请求成功响应： " + result.toJSONString());
                taskId = result.getString("TaskId");
            } else {
                System.out.println("录音文件识别请求失败： " + result.toJSONString());
            }
        } else {
            System.err.println("录音文件识别请求失败，Http错误码：" + postResponse.getHttpStatus());
            System.err.println("录音文件识别请求失败响应：" + JSONObject.toJSONString(postResponse));
        }
        return taskId;
    }

    public String getTrans(String taskId) {
        String res = "";
        final String accessKeyId = aliCloudProperties.getAk();
        final String accessKeySecret = aliCloudProperties.getSk();
        /**
         * 地域ID
         */
        final String regionId = "cn-shanghai";
        final String endpointName = "cn-shanghai";
        final String product = "nls-filetrans";
        final String domain = "filetrans.cn-shanghai.aliyuncs.com";

        IAcsClient client;
        // 设置endpoint
        try {
            DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
        } catch (ClientException e) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + e.getMessage());
        }
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        CommonRequest getRequest = new CommonRequest();
        getRequest.setDomain("filetrans.cn-shanghai.aliyuncs.com");   // 设置域名，固定值。
        getRequest.setVersion("2018-08-17");         // 设置中国站的版本号。
        getRequest.setAction("GetTaskResult");           // 设置action，固定值。
        getRequest.setProduct("nls-filetrans");          // 设置产品名称，固定值。
        getRequest.putQueryParameter("TaskId", taskId);  // 设置任务ID为查询参数。
        getRequest.setMethod(MethodType.GET);            // 设置为GET方式的请求。
        /**
         * 提交录音文件识别结果查询请求
         * 以轮询的方式进行识别结果的查询，直到服务端返回的状态描述为“SUCCESS”、“SUCCESS_WITH_NO_VALID_FRAGMENT”，或者为错误描述，则结束轮询。
         */
        String statusText = "";
        while (true) {
            CommonResponse getResponse = null;
            try {
                getResponse = client.getCommonResponse(getRequest);
            } catch (ClientException e) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message:" + e.getMessage());
            }
            if (getResponse.getHttpStatus() != 200) {
                System.err.println("识别结果查询请求失败，Http错误码： " + getResponse.getHttpStatus());
                System.err.println("识别结果查询请求失败： " + getResponse.getData());
                break;
            }
            JSONObject result = JSONObject.parseObject(getResponse.getData());
            System.out.println("识别查询结果：" + result.toJSONString());
            statusText = result.getString("StatusText");
            if ("RUNNING".equals(statusText) || "QUEUEING".equals(statusText)) {
                // 继续轮询
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if ("SUCCESS".equals(statusText) || "SUCCESS_WITH_NO_VALID_FRAGMENT".equals(statusText)) {
                res = result.getString("Result");
                break;
            } else {
                throw new RuntimeException("失败的识别");
            }
        }
        return res;
    }


}
