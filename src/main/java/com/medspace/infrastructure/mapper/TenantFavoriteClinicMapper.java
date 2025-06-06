package com.medspace.infrastructure.mapper;

import com.medspace.domain.model.TenantFavoriteClinic;
import com.medspace.infrastructure.entity.TenantFavoriteClinicEntity;

public class TenantFavoriteClinicMapper {

    public static TenantFavoriteClinic toDomain(TenantFavoriteClinicEntity tenantFavoriteClinicEntity) {
        if (tenantFavoriteClinicEntity == null) {
            return null;
        }

        TenantFavoriteClinic tenantFavoriteClinic = new TenantFavoriteClinic();

        tenantFavoriteClinic.setId(tenantFavoriteClinicEntity.getId());

        tenantFavoriteClinic.setClinic(ClinicMapper.toDomain(tenantFavoriteClinicEntity.getClinic()));
        tenantFavoriteClinic.setTenant(UserMapper.toDomain(tenantFavoriteClinicEntity.getTenant()));
        tenantFavoriteClinic.setCreatedAt(tenantFavoriteClinicEntity.getCreatedAt());


        return tenantFavoriteClinic;
    }

    public static TenantFavoriteClinicEntity toEntity(TenantFavoriteClinic tenantFavoriteClinic) {
        if (tenantFavoriteClinic == null) {
            return null;
        }

        TenantFavoriteClinicEntity tenantFavoriteClinicEntity = new TenantFavoriteClinicEntity();

        tenantFavoriteClinicEntity.setId(tenantFavoriteClinic.getId());
        tenantFavoriteClinicEntity.setClinic(ClinicMapper.toEntity(tenantFavoriteClinic.getClinic()));
        tenantFavoriteClinicEntity.setTenant(UserMapper.toEntity(tenantFavoriteClinic.getTenant()));
        tenantFavoriteClinicEntity.setCreatedAt(tenantFavoriteClinic.getCreatedAt());

        return tenantFavoriteClinicEntity;
    }
}