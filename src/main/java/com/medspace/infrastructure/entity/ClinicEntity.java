package com.medspace.infrastructure.entity;

import com.medspace.domain.model.Clinic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Clinic.Category category;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price_per_day", nullable = false)
    private double pricePerDay;

    @Column(name = "max_stay_days", nullable = false)
    private int maxStayDays;

    @Column(name = "address_street", nullable = false)
    private String addressStreet;

    @Column(name = "address_city", nullable = false)
    private String addressCity;

    @Column(name = "address_state", nullable = false)
    private String addressState;

    @Column(name = "address_zip", nullable = false)
    private String addressZip;

    @Column(name = "address_country", nullable = false)
    private String addressCountry;

    @Column(name = "address_longitude", nullable = false)
    private String addressLongitude;

    @Column(name = "address_latitude", nullable = false)
    private String addressLatitude;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "available_from_date", nullable = false)
    private Date availableFromDate;

    @Column(name = "available_to_date", nullable = false)
    private Date availableToDate;

    @Column(name = "size", nullable = false)
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "landlord_id")
    private UserEntity landlord;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClinicPhotoEntity> photos;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClinicEquipmentEntity> equipments;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClinicAvailabilityEntity> availabilities;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RentRequestEntity> rentRequests;

}
