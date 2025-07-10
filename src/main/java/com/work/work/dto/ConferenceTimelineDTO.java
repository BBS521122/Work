package com.work.work.dto;

public class ConferenceTimelineDTO {
    private String video;
    private String text;
    private Integer textStatus;
    private Integer mindMapStatus;
    private Integer summaryStatus;

    public ConferenceTimelineDTO() {
    }

    public ConferenceTimelineDTO(String video, String text, Integer textStatus, Integer mindMapStatus, Integer summaryStatus) {
        this.video = video;
        this.text = text;
        this.textStatus = textStatus;
        this.mindMapStatus = mindMapStatus;
        this.summaryStatus = summaryStatus;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getTextStatus() {
        return textStatus;
    }

    public void setTextStatus(Integer textStatus) {
        this.textStatus = textStatus;
    }

    public Integer getMindMapStatus() {
        return mindMapStatus;
    }

    public void setMindMapStatus(Integer mindMapStatus) {
        this.mindMapStatus = mindMapStatus;
    }

    public Integer getSummaryStatus() {
        return summaryStatus;
    }

    public void setSummaryStatus(Integer summaryStatus) {
        this.summaryStatus = summaryStatus;
    }
}
