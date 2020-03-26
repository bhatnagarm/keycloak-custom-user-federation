package com.techartworks.userfederation.storage;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Stateful(name="ejb/CustomUserFederationProvider", mappedName="ejb/CustomUserFederationProvider" )
@Local
@TransactionManagement(TransactionManagementType.BEAN)
public class CustomUserFederationProvider implements CustomUserFederationProviderLocal {

    private static final Logger log = Logger.getLogger(CustomUserFederationProvider.class);
    private static final String MOBILE = "mobile";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String POST_CODE = "postCode";

    @PersistenceContext(unitName = "user-storage-jpa")
    private transient EntityManager entityManager;

    private transient ComponentModel model;
    private transient KeycloakSession session;

    @Override
    public void preRemove(final RealmModel realm) {
        log.info("CustomUserFederationProvider.preRemove(RealmModel realm)");
    }

    @Override
    public void preRemove(final RealmModel realm, final GroupModel group) {
        log.info("CustomUserFederationProvider.preRemove(RealmModel realm, GroupModel group)");
    }

    @Override
    public void preRemove(final RealmModel realm, final RoleModel role) {
        log.info("CustomUserFederationProvider.preRemove(RealmModel realm, RoleModel role)");
    }

    @Remove
    @Override
    public void close() {
        log.info("CustomUserFederationProvider.close()");
    }

    @Override
    public UserModel getUserById(final String id, final RealmModel realm) {
        log.debug("getUserById: " + id);
        String persistenceId = StorageId.externalId(id);
        UserEntity entity = entityManager.find(UserEntity.class, persistenceId);
        if (null == entity) {
            log.info("could not find user by id: " + id);
            return null;
        }
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public UserModel getUserByUsername(final String username, final RealmModel realm) {
        log.debug("getUserByUsername: " + username);
        TypedQuery<UserEntity> query = entityManager.createNamedQuery("UserEntity.findByUserInfo", UserEntity.class);
        query.setParameter(MOBILE, username);
        List<UserEntity> result = query.getResultList();
        if (result.isEmpty()) {
            log.info("could not find username: " + username);
            return null;
        }

        return new UserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public UserModel getUserByEmail(final String email, final RealmModel realm) {
        log.debug("getUserByEmail(String " + email + ", RealmModel realm)");
        return null;
    }

    @Override
    public int getUsersCount(final RealmModel realm) {
        log.info("getUsersCount(RealmModel realm)");
        Object count = entityManager.createNamedQuery("UserEntity.getUserCount")
                .getSingleResult();
        return ((Number)count).intValue();
    }

    @Override
    public List<UserModel> getUsers(final RealmModel realm) {
        log.info("getUsers(RealmModel realm)");
        return getUsers(realm, -1, -1);
    }

    @Override
    public List<UserModel> getUsers(final RealmModel realm, final int firstResult, final int maxResults) {
        log.info("getUsers(RealmModel realm, int firstResult, int maxResults)");
        final TypedQuery<UserEntity> query = entityManager.createNamedQuery("UserEntity.getAllUsers", UserEntity.class);
        log.error("1. CustomUserFederationProvider: getAddUsers");
        return getUserModels(realm, firstResult, maxResults, query);
    }

    private List<UserModel> getUserModels(final RealmModel realm,
                                          final int firstResult,
                                          final int maxResults,
                                          final TypedQuery<UserEntity> query) {
        log.info("getUserModels(RealmModel realm, int firstResult, int maxResults, TypedQuery<UserEntity> query)");
        if (-1 != firstResult) {
            query.setFirstResult(firstResult);
        }
        if (-1 != maxResults) {
            query.setMaxResults(maxResults);
        }
        final List<UserEntity> results = query.getResultList();
        log.info("2. CustomUserFederationProvider : getUserModels : " + results.size());
        results.forEach(log::info);
        return results.stream()
                .map(entity -> new UserAdapter(session, realm, model, entity))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<UserModel> searchForUser(final String search, final RealmModel realm) {
        log.info("searchForUser( " + search + ", RealmModel realm)");
        return searchForUser(search, realm, -1, -1);
    }

    @Override
    public List<UserModel> searchForUser(final String search,
                                         final RealmModel realm,
                                         final int firstResult,
                                         final int maxResults) {
        log.info("searchForUser( " + search + ", RealmModel realm, "+ firstResult + ", "+ maxResults + ")");
        final TypedQuery<UserEntity> query = entityManager.createNamedQuery("UserEntity.searchForUser", UserEntity.class);
        query.setParameter(MOBILE, search.toLowerCase(Locale.getDefault()));
        return getUserModels(realm, firstResult, maxResults, query);
    }

    @Override
    public List<UserModel> searchForUser(final Map<String, String> params, final RealmModel realm) {
        log.info("searchForUser(Map<String, String> params, RealmModel realm)");
        params.forEach((s, s2) -> log.info("Key:" + s + " Value: " + s2));

        return searchForUser(params, realm, -1, -1);
    }

    @Override
    public List<UserModel> searchForUser(final Map<String, String> params,
                                         final RealmModel realm,
                                         final int firstResult,
                                         final int maxResults) {
        log.debug("searchForUser(Map<String, String> " + params.toString() + ", RealmModel realm, int " + firstResult + ", int " + maxResults + ")");
        TypedQuery<UserEntity> query;
        log.debug("Params Size: " + params.size());

        if (params.keySet().containsAll(Set.of(FIRST_NAME, LAST_NAME, POST_CODE))) {
            log.debug("firstName "+ params.get(FIRST_NAME));
            log.debug("lastName "+ params.get(LAST_NAME));
            query = entityManager.createNamedQuery("UserEntity.firstTimeLoginSearch", UserEntity.class);
            query.setParameter(FIRST_NAME, params.get(FIRST_NAME))
                    .setParameter(LAST_NAME, params.get(LAST_NAME));

        } else if (params.keySet().containsAll(Set.of(MOBILE, DATE_OF_BIRTH))) {
            log.debug("Mobile "+ params.get(MOBILE));
            log.debug("Dob "+ params.get(DATE_OF_BIRTH));
            query = entityManager.createNamedQuery("UserEntity.searchForUser", UserEntity.class);
            query.setParameter(MOBILE, params.get(MOBILE))
                 .setParameter(DATE_OF_BIRTH, params.get(DATE_OF_BIRTH));

        } else {
            query = entityManager.createNamedQuery("UserEntity.getAllUsers", UserEntity.class);
        }

        return getUserModels(realm, firstResult, maxResults, query);
    }

    @Override
    public List<UserModel> getGroupMembers(final RealmModel realm,
                                           final GroupModel group,
                                           final int firstResult,
                                           final int maxResults) {
        log.info("getGroupMembers(realm, group, firstResult, maxResults)");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(final RealmModel realm, final GroupModel group) {
        log.info("getGroupMembers(realm, group)");
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(final String attrName,
                                                        final String attrValue,
                                                        final RealmModel realm) {
        log.debug("searchForUserByUserAttribute(" + attrName + ", " + attrValue + ", " + realm.getName() + ")");
        TypedQuery<UserEntity> query = entityManager.createNamedQuery("UserEntity.searchForUser", UserEntity.class);
        query.setParameter(MOBILE, attrValue.toLowerCase(Locale.getDefault()));
        return getUserModels(realm, -1, -1, query);
    }

    @Override
    public void setModel(final ComponentModel model) {
        log.info("setModel");
        this.model = model;
    }

    @Override
    public void setSession(final KeycloakSession session) {
        log.info("setSession");
        this.session = session;
    }

}
