package com.work.work.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dify")
public class DifyProperties {
    /**
     * dify workflow baseurl
     */
    private String baseUrl;
    /**
     * 生成会议纪要api
     */
    private String summaryKey;
    /**
     * 生成思维导图api
     */
    private String mindMapKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSummaryKey() {
        return summaryKey;
    }

    public void setSummaryKey(String summaryKey) {
        this.summaryKey = summaryKey;
    }

    public String getMindMapKey() {
        return mindMapKey;
    }

    public void setMindMapKey(String mindMapKey) {
        this.mindMapKey = mindMapKey;
    }
}
