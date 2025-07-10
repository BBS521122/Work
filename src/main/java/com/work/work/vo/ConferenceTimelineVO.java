package com.work.work.vo;

import java.time.LocalDateTime;

public class ConferenceTimelineVO {
   private LocalDateTime startTime;
   private LocalDateTime endTime;
    private String hasRecording;
    private String hasTranscription;
    private String hasMinutes;
    private String hasMindMap;
    private String recordingUrl;

    public ConferenceTimelineVO() {
    }

    public ConferenceTimelineVO(LocalDateTime startTime, LocalDateTime endTime, String hasRecording,
                                String hasTranscription, String hasMinutes, String hasMindMap, String recordingUrl) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.hasRecording = hasRecording;
        this.hasTranscription = hasTranscription;
        this.hasMinutes = hasMinutes;
        this.hasMindMap = hasMindMap;
        this.recordingUrl = recordingUrl;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getHasRecording() {
        return hasRecording;
    }

    public void setHasRecording(String hasRecording) {
        this.hasRecording = hasRecording;
    }

    public String getHasTranscription() {
        return hasTranscription;
    }

    public void setHasTranscription(String hasTranscription) {
        this.hasTranscription = hasTranscription;
    }

    public String getHasMinutes() {
        return hasMinutes;
    }

    public void setHasMinutes(String hasMinutes) {
        this.hasMinutes = hasMinutes;
    }

    public String getHasMindMap() {
        return hasMindMap;
    }

    public void setHasMindMap(String hasMindMap) {
        this.hasMindMap = hasMindMap;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

}
