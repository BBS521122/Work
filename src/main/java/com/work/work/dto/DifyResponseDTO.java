package com.work.work.dto;

import java.util.Map;

public class DifyResponseDTO {
    private String task_id;
    private String workflow_run_id;
    private WorkflowData data;

    public DifyResponseDTO() {
    }

    public DifyResponseDTO(String task_id, String workflow_run_id, WorkflowData data) {
        this.task_id = task_id;
        this.workflow_run_id = workflow_run_id;
        this.data = data;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getWorkflow_run_id() {
        return workflow_run_id;
    }

    public void setWorkflow_run_id(String workflow_run_id) {
        this.workflow_run_id = workflow_run_id;
    }

    public WorkflowData getData() {
        return data;
    }

    public void setData(WorkflowData data) {
        this.data = data;
    }

    public static class WorkflowData {
        private String id;
        private String workflow_id;
        private String status;  // 可改为枚举更类型安全
        private Map<String, Object> outputs;
        private String error;
        private double elapsed_time;
        private int total_tokens;
        private int total_steps;
        private long created_at;
        private long finished_at;

        public WorkflowData() {
        }

        public WorkflowData(String id, String workflow_id, String status, Map<String, Object> outputs, String error,
                            double elapsed_time, int total_tokens, int total_steps, long created_at, long finished_at) {
            this.id = id;
            this.workflow_id = workflow_id;
            this.status = status;
            this.outputs = outputs;
            this.error = error;
            this.elapsed_time = elapsed_time;
            this.total_tokens = total_tokens;
            this.total_steps = total_steps;
            this.created_at = created_at;
            this.finished_at = finished_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getWorkflow_id() {
            return workflow_id;
        }

        public void setWorkflow_id(String workflow_id) {
            this.workflow_id = workflow_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Map<String, Object> getOutputs() {
            return outputs;
        }

        public void setOutputs(Map<String, Object> outputs) {
            this.outputs = outputs;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public double getElapsed_time() {
            return elapsed_time;
        }

        public void setElapsed_time(double elapsed_time) {
            this.elapsed_time = elapsed_time;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }

        public int getTotal_steps() {
            return total_steps;
        }

        public void setTotal_steps(int total_steps) {
            this.total_steps = total_steps;
        }

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }

        public long getFinished_at() {
            return finished_at;
        }

        public void setFinished_at(long finished_at) {
            this.finished_at = finished_at;
        }
    }
}
