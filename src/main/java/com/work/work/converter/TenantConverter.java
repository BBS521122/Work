package com.work.work.converter;


import com.work.work.dto.ConferenceGetDTO;
import com.work.work.dto.TenantAddDTO;
import com.work.work.dto.TenantGetDTO;
import com.work.work.dto.TenantUpdateDTO;
import com.work.work.entity.Conference;
import com.work.work.entity.Tenant;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cover", ignore = true)
    Tenant tenantAddDTOToTenant(TenantAddDTO tenantAddDTO);

    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "cover", ignore = true)
    Tenant tenantUpdateDTOToTenant(TenantUpdateDTO tenantUpdateDTO);

    TenantGetDTO conferenceToTenantGetDTO(Tenant tenant);
}
