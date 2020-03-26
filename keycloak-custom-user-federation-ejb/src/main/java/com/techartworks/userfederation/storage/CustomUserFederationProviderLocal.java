package com.techartworks.userfederation.storage;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

public interface CustomUserFederationProviderLocal extends UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider {

    void setModel(final ComponentModel model);

    void setSession(final KeycloakSession session);
}
