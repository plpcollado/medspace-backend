package com.medspace.infrastructure.entity;

import java.sql.Timestamp;
import java.util.Set;

import com.medspace.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "profile_photo_url")
    private String profilePictureUrl;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private User.UserType userType;

    @ManyToOne
    @JoinColumn(name = "tenant_specialty_id")
    private TenantSpecialtyEntity tenantSpecialty;

    @Column(name = "tenant_professional_license_url")
    private String tenantProfessionalLicenseUrl;

    @Column(name = "average_rating")
    private float averageRating;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "default_payment_method")
    private String defaultPaymentMethod;

    @OneToMany(mappedBy = "landlord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClinicEntity> clinics;
}
