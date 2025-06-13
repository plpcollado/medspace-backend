package com.medspace.infrastructure.repository;


import com.medspace.domain.model.Clinic;
import com.medspace.domain.repository.ClinicRepository;
import com.medspace.infrastructure.dto.clinic.ClinicQueryDTO;
import com.medspace.infrastructure.entity.ClinicAvailabilityEntity;
import com.medspace.infrastructure.entity.ClinicEntity;
import com.medspace.infrastructure.entity.ClinicEquipmentEntity;
import com.medspace.infrastructure.entity.UserEntity;
import com.medspace.infrastructure.mapper.ClinicMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.Set;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Expression;

@ApplicationScoped
public class ClinicRepositoryImpl
        implements ClinicRepository, PanacheRepositoryBase<ClinicEntity, Long> {


    @Inject
    UserRepositoryImpl userRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Clinic insertClinic(Clinic clinic) {
        ClinicEntity clinicEntity = ClinicMapper.toEntity(clinic);
        persist(clinicEntity);
        clinic = ClinicMapper.toDomain(clinicEntity);
        return clinic;
    }

    @Override
    public List<Clinic> getAllClinics() {
        List<ClinicEntity> clinicEntities = listAll();
        List<Clinic> clinics = new ArrayList<>();

        for (ClinicEntity clinicEntity : clinicEntities) {
            clinics.add(ClinicMapper.toDomain(clinicEntity));
        }

        return clinics;
    }

    @Override
    public List<Clinic> getFilteredClinics(ClinicQueryDTO filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ClinicEntity> query = cb.createQuery(ClinicEntity.class);
        Root<ClinicEntity> clinic = query.from(ClinicEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getTargetDate() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(clinic.get("availableFromDate"), filter.getTargetDate()));
            predicates.add(
                    cb.greaterThanOrEqualTo(clinic.get("availableToDate"), filter.getTargetDate()));
        }

        if (filter.getEquipmentList() != null) {
            Join<ClinicEntity, ClinicEquipmentEntity> equipmentJoin =
                    clinic.join("equipments", JoinType.INNER);
            predicates.add(equipmentJoin.get("type").in(filter.getEquipmentList()));
        }

        if (filter.getTargetHour() != null) {
            Join<ClinicEntity, ClinicAvailabilityEntity> availability =
                    clinic.join("availabilities");
            predicates.add(
                    cb.lessThanOrEqualTo(availability.get("startTime"), filter.getTargetHour()));
            predicates.add(
                    cb.greaterThanOrEqualTo(availability.get("endTime"), filter.getTargetHour()));
        }

        if (filter.getTargetCity() != null) {
            predicates.add(cb.equal(clinic.get("addressCity"), filter.getTargetCity()));
        }

        query.select(clinic).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        List<ClinicEntity> entities = em.createQuery(query).getResultList();
        return entities.stream().map(ClinicMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Clinic> getClinicsByLandlordId(Long landlordId) {
        List<ClinicEntity> clinicEntities = find("landlord.id", landlordId).list();
        List<Clinic> clinics = new ArrayList<>();

        for (ClinicEntity clinicEntity : clinicEntities) {
            clinics.add(ClinicMapper.toDomain(clinicEntity));
        }

        return clinics;
    }

    @Override
    public Clinic getClinicById(Long id) {
        ClinicEntity clinicEntity = findById(id);
        if (clinicEntity == null) {
            return null;
        }
        return ClinicMapper.toDomain(clinicEntity);
    }

    @Override
    @Transactional
    public void deleteClinicById(Long id) {
        ClinicEntity clinicEntity = findById(id);
        if (clinicEntity != null) {
            delete(clinicEntity);
        } else {
            throw new NotFoundException("clinic with id " + id + " not found");
        }
    }

    @Override
    @Transactional
    public Clinic assignClinicToUser(Long clinicId, Long userId) {
        ClinicEntity clinicEntity = findById(clinicId);
        if (clinicEntity == null) {
            throw new NotFoundException("clinic with id " + clinicId + " not found");
        }

        UserEntity userEntity = userRepository.findById(userId);
        if (userEntity == null) {
            throw new NotFoundException("user with id " + userId + " not found");
        }

        clinicEntity.setLandlord(userEntity);
        persist(clinicEntity);
        return ClinicMapper.toDomain(clinicEntity);
    }

    @Override
    @Transactional
    public Clinic updateClinic(Long id, Clinic clinic) {
        ClinicEntity clinicEntity = findById(id);
        if (clinicEntity == null) {
            throw new NotFoundException("clinic with id " + id + " not found");
        }

        // Update only the fields that are allowed to be updated
        if (clinic.getDisplayName() != null) {
            clinicEntity.setDisplayName(clinic.getDisplayName());
        }
        if (clinic.getCategory() != null) {
            clinicEntity.setCategory(clinic.getCategory());
        }
        if (clinic.getDescription() != null) {
            clinicEntity.setDescription(clinic.getDescription());
        }
        if (clinic.getPricePerDay() > 0) {
            clinicEntity.setPricePerDay(clinic.getPricePerDay());
        }
        if (clinic.getMaxStayDays() > 0) {
            clinicEntity.setMaxStayDays(clinic.getMaxStayDays());
        }

        persist(clinicEntity);
        return ClinicMapper.toDomain(clinicEntity);
    }

    @Override
    public long countAll() {
        return count();
    }


    @Override
    public long countClinicsByCategory(Clinic.Category category) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ClinicEntity> clinic = query.from(ClinicEntity.class);
    
        Predicate predicate = cb.equal(clinic.get("category"), category);
        query.select(cb.count(clinic)).where(predicate);
    
        return em.createQuery(query).getSingleResult();
    }

    @Override
    public long countByCity(String city) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ClinicEntity> clinic = query.from(ClinicEntity.class);
    
        // Normalizamos el city para ignorar mayúsculas y espacios (igual que en JPQL)
        Expression<String> normalizedCityDb = cb.lower(cb.function("REPLACE", String.class, clinic.get("addressCity"), cb.literal(" "), cb.literal("_")));
        String normalizedCityParam = city.toLowerCase().replaceAll("\\s+", "_");
    
        // Creamos el predicado con igualdad entre city normalizado en BD y parámetro normalizado
        Predicate cityPredicate = cb.equal(normalizedCityDb, normalizedCityParam);
    
        query.select(cb.count(clinic)).where(cityPredicate);
    
        return em.createQuery(query).getSingleResult();
    }
    

@Override
public Set<String> findAllUniqueCities() {
    return new HashSet<>(
        em.createQuery("SELECT DISTINCT c.addressCity FROM ClinicEntity c", String.class)
          .getResultList()
    );
}
public long getByCategoryAndCity(Clinic.Category category, String city) {
    String normalizedCity = city.toLowerCase().replaceAll("\\s+", "_");

    return em.createQuery(
        "SELECT COUNT(c) FROM ClinicEntity c WHERE " +
        "LOWER(REPLACE(c.addressCity, ' ', '_')) = :normalizedCity AND c.category = :category",
        Long.class)
        .setParameter("normalizedCity", normalizedCity)
        .setParameter("category", category)
        .getSingleResult();
}


}
