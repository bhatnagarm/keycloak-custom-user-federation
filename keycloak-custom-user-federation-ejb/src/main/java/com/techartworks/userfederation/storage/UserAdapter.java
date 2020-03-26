package com.techartworks.userfederation.storage;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import com.techartworks.userfederation.UserAttributesConstants;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private static final Logger log = Logger.getLogger(UserAdapter.class);

    private final UserEntity entity;
    private final String id;

    public UserAdapter(final KeycloakSession session, final RealmModel realm, final ComponentModel model,
                       final UserEntity entity) {
        super(session, realm, model);
        this.entity = entity;
        id = StorageId.keycloakId(model, entity.getResourceId());
    }

    @Override
    public String getUsername() {
        return entity.getResourceId();
    }

    @Override
    public void setUsername(final String username) {
    }

    @Override
    public String getEmail() {
        return entity.getMobile();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }

    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    @Override
    public void setSingleAttribute(final String name, final String value) {
        log.debug("UserAdapter: setSingleAttribute(" + name + ", " + value +")");
        if (UserAttributesConstants.MOBILE.equals(name)) {
            entity.setMobile(value);
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(final String name) {
        log.info("UserAdapter: removeAttribute(" + name + ")");
        super.removeAttribute(name);
    }

    @Override
    public void setAttribute(final String name, final List<String> values) {
        log.debug("UserAdapter: setAttribute(" + name + ", " + values +")");
        super.setAttribute(name, values);
    }

    @Override
    public String getFirstAttribute(final String name) {
        log.info("UserAdapter: getFirstAttribute(" + name + ")");
        final List<String> attributeList =  getAttributes().get(name);
        if (attributeList != null && !attributeList.isEmpty()) {
            return attributeList.get(0);
        }
        return super.getFirstAttribute(name);

    }

    @Override
    public Map<String, List<String>> getAttributes() {
        final Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> multivaluedHashMap = new MultivaluedHashMap<>();
        multivaluedHashMap.putAll(attrs);
        multivaluedHashMap.add(UserAttributesConstants.CUSTOMER_ID, entity.getResourceId());
        multivaluedHashMap.add(UserAttributesConstants.MOBILE, entity.getMobile());
        multivaluedHashMap.add(UserAttributesConstants.DATE_OF_BIRTH, entity.getDateOfBirth());
        multivaluedHashMap.add(UserAttributesConstants.LOCAL_LAST_NAME, entity.getLastName());
        multivaluedHashMap.add(UserAttributesConstants.LOCAL_FIRST_NAME, entity.getFirstName());
        multivaluedHashMap.add(UserAttributesConstants.ONLINE_ADMIN_ACTIVATION, entity.getOnlineAdminActivation());
        log.info(multivaluedHashMap.toString() + "UserAdapter: getAttribute()");
        return multivaluedHashMap;
    }

    @Override
    public List<String> getAttribute(final String name) {
        log.info("UserAdapter: getAttribute( " + name + ")");

        if (UserAttributesConstants.MOBILE.equals(name)) {
            return List.of(entity.getMobile());
        } else if (UserAttributesConstants.CUSTOMER_ID.equalsIgnoreCase(name)) {
            return List.of(entity.getResourceId());
        } else if (UserAttributesConstants.ONLINE_ADMIN_ACTIVATION.equalsIgnoreCase(name)) {
            return List.of(entity.getOnlineAdminActivation());
        } else if (UserAttributesConstants.DATE_OF_BIRTH.equalsIgnoreCase(name)) {
            return List.of(entity.getDateOfBirth());
        } else if (UserAttributesConstants.LOCAL_LAST_NAME.equalsIgnoreCase(name)) {
            return List.of(entity.getLastName());
        } else if (UserAttributesConstants.LOCAL_FIRST_NAME.equalsIgnoreCase(name)) {
            return List.of(entity.getFirstName());
        } else {
            return super.getAttribute(name);
        }
    }

    public UserEntity getEntity() {
        return entity;
    }
}
