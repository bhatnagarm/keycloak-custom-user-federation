package com.techartworks.userfederation.storage;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class CustomUserFederationProviderFactory
        implements UserStorageProviderFactory<CustomUserFederationProviderLocal> {

    private static final Logger log = Logger.getLogger(CustomUserFederationProviderFactory.class);

    @EJB(lookup = "ejb/DiscUserFederationProvider")
    private transient CustomUserFederationProviderLocal provider;


    @Override
    public void init(final Config.Scope config) {

        // this configuration is pulled from the SPI configuration of this provider in the standalone[-ha] / domain.xml
        // see setup.cli

        log.info("1. Initialize User Storage ProviderFactory.");
    }

    @Override
    public void onCreate(final KeycloakSession session,
                         final RealmModel realm,
                         final ComponentModel model) {
        log.info("2. Create User Storage ProviderFactory. " + realm.getId());
    }

    @Override
    public CustomUserFederationProviderLocal create(final KeycloakSession session,
                                                     final ComponentModel model) {

        try {
            InitialContext ctx = new InitialContext();

            NamingEnumeration<NameClassPair> list = ctx.list("");
            while (list.hasMore()) {
                log.error(list.next().getName());
            }

            provider =
                    (CustomUserFederationProviderLocal) ctx.lookup("java:global/keycloak-custom-user-federation" +
                            "/keycloak-custom-user-federation-ejb/ejb/" +
                            "DiscUserFederationProvider");
            provider.setModel(model);
            provider.setSession(session);
           return provider;
        } catch (NamingException ne) {
            log.error("DiscUserFederationProvider create: " + ne.getExplanation());
            throw new RuntimeException(ne);
        } catch (Exception e) {
            log.error("DiscUserFederationProvider create: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CustomUserFederationProviderLocal getProvider() {
        return provider;
    }

    @Override
    public String getId() {
        return "disc-user-storage-federation";
    }

    @Override
    public String getHelpText() {
        return "JPA User Storage Auto & General Provider";
    }

    @Override
    public void close() {
        log.info("---------------- Closing factory ----------------");
    }
}
