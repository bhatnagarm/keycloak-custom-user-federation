package com.techartworks.userfederation.storage;

import javax.persistence.*;

import com.techartworks.userfederation.storage.converter.LocalDateAttributeConverter;

import java.io.Serializable;

@Entity
@Table(name = "CUSTOMER_VERIFICATION")
@NamedQueries({
        @NamedQuery(name="UserEntity.getUserCount", query="select count(u) from UserEntity u"),
        @NamedQuery(name="UserEntity.searchForUser",
                query="select u from UserEntity u where u.mobile = :mobile and u.dateOfBirth = :dateOfBirth"),
        @NamedQuery(name="UserEntity.firstTimeLoginSearch",
                query="select u from UserEntity u where u.firstName = :firstName and u.lastName = :lastName"),
        @NamedQuery(name="UserEntity.findByUserInfo",
                query="select u from UserEntity u where u.mobile = :mobile"),
        @NamedQuery(name="UserEntity.getAllUsers", query="select u from UserEntity u")
})
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 5330847131505545734L;

    @Id
    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "date_of_birth", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    private String dateOfBirth;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "online_admin_activation")
    private String onlineAdminActivation;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(final String resourceId) {
        this.resourceId = resourceId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getOnlineAdminActivation() {
        return onlineAdminActivation;
    }

    public void setOnlineAdminActivation(final String onlineAdminActivation) {
        this.onlineAdminActivation = onlineAdminActivation;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
