package com.techartworks.userfederation.storage;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_address")
public class UserPostcodeEntity implements Serializable {

    private static final long serialVersionUID = -7171051674364560600L;

    @Id
    private String id;

    @Column(name = "resource_id")
    private String customerId;

    @Column(name = "postcode")
    private String postCode;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(final String postCode) {
        this.postCode = postCode;
    }
}
