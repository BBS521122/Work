package com.work.work.entity;

public class TenantMedia {
    private Long id;
    private String uuid;
    private String tenant_id;
    private String name;

    public TenantMedia() {
    }

    public TenantMedia(Long id, String uuid, String tenant_id, String name) {
        this.id = id;
        this.uuid = uuid;
        this.tenant_id = tenant_id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
