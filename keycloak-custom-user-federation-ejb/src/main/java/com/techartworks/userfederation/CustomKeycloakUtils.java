package com.techartworks.userfederation;

import org.keycloak.models.AuthenticatorConfigModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class CustomKeycloakUtils {

    public static String getConfigString(final AuthenticatorConfigModel config, final String configName) {
        return config.getConfig().get(configName);
    }

    public static boolean getConfigBoolean(final AuthenticatorConfigModel config, final String configName) {
        return config.getConfig().get(configName) == null ?
                Boolean.FALSE : Boolean.valueOf(config.getConfig().get(configName));
    }

    public static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
}
