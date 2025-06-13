package com.medspace.application.usecase.clinic;

import java.util.List;
import com.medspace.application.service.ClinicService;
import com.medspace.domain.model.Clinic;
import com.medspace.infrastructure.dto.clinic.ClinicQueryDTO;
import com.medspace.infrastructure.dto.clinic.GetClinicAvailabilityDTO;
import com.medspace.infrastructure.dto.clinic.GetClinicDTO;
import com.medspace.infrastructure.dto.clinic.GetClinicEquipmentDTO;
import com.medspace.infrastructure.dto.clinic.GetClinicPhotoDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetClinicByIdUseCase {
    @Inject
    ClinicService clinicService;

    public GetClinicDTO execute(Long id, ClinicQueryDTO queryFilterDTO) {
        Clinic clinic = clinicService.getClinicById(id);
        if (clinic == null) {
            throw new NotFoundException("Clinic with id " + id + " not found");
        }
        Double averageRating = clinicService.getAverageRatingById(id);

        List<GetClinicPhotoDTO> photoDTOs =
                queryFilterDTO.getIncludePhotos()
                        ? clinicService.listPhotosByClinicId(id).stream()
                                .map(GetClinicPhotoDTO::new).toList()
                        : null;

        List<GetClinicEquipmentDTO> equipmentDTOs =
                queryFilterDTO.getIncludeEquipments()
                        ? clinicService.getEquipmentsByClinicId(id).stream()
                                .map(GetClinicEquipmentDTO::new).toList()
                        : null;

        List<GetClinicAvailabilityDTO> availabilityDTOs = queryFilterDTO.getIncludeAvailabilities()
                ? clinicService.getAvailabilitiesByClinicId(id).stream()
                        .map(GetClinicAvailabilityDTO::new).toList()
                : null;

        return new GetClinicDTO(clinic, averageRating, photoDTOs, equipmentDTOs, availabilityDTOs);
    }
}
