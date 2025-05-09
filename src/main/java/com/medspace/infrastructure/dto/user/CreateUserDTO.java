package com.medspace.infrastructure.dto.user;


import java.io.InputStream;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import com.medspace.domain.model.TenantSpecialty;
import com.medspace.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    @NotBlank
    @FormParam("fullName")
    @PartType(MediaType.TEXT_PLAIN)
    private String fullName;


    @NotBlank
    @FormParam("email")
    @Email
    @PartType(MediaType.TEXT_PLAIN)
    private String email;


    @NotBlank
    @FormParam("phoneNumber")
    @PartType(MediaType.TEXT_PLAIN)
    private String phoneNumber;

    @FormParam("profilePhoto")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream profilePhoto;

    @NotNull
    @FormParam("userType")
    @PartType(MediaType.TEXT_PLAIN)
    @Pattern(regexp = " LANDLORD|TENANT|ANALYST",
            message = "User type must be one of: LANDLORD, TENANT, ANALYST")
    private String userType;

    @FormParam("tenantSpecialtyId")
    @PartType(MediaType.TEXT_PLAIN)
    private Long tenantSpecialtyId;


    @FormParam("tenantProfessionalLicense")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream tenantProfessionalLicense;


    // You may also want to update the toUser method in CreateUserDTO.java for better safety
    public User toUser() {
        User user = new User();
        user.setFullName(this.fullName);
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        user.setUserType(User.UserType.valueOf(this.userType.trim())); // Added trim() for safety

        // Only set tenant specialty for TENANT users and when the ID is provided
        if (this.tenantSpecialtyId != null && user.getUserType() == User.UserType.TENANT) {
            TenantSpecialty specialty = new TenantSpecialty();
            specialty.setId(this.tenantSpecialtyId);
            user.setTenantSpecialty(specialty);
        }

        return user;
    }
}
