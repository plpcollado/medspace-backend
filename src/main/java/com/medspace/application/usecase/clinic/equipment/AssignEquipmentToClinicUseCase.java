package com.medspace.application.usecase.clinic.equipment;

import com.medspace.application.service.ClinicService;
import com.medspace.domain.model.ClinicEquipment;
import io.quarkus.security.ForbiddenException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AssignEquipmentToClinicUseCase {
    @Inject
    ClinicService clinicService;

    @Transactional
    public ClinicEquipment execute(Long clinicEquipmentId, Long clinicId, Long userId) {
        Boolean isOwner = clinicService.validateClinicOwnership(clinicId, userId);
        if (!isOwner) {
            throw new ForbiddenException("Assign unauthorized");
        }

        return clinicService.assignEquipmentToClinic(clinicEquipmentId, clinicId);
    }
}
