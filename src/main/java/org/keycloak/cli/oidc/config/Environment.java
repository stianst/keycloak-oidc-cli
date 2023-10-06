package org.keycloak.cli.oidc.config;

import java.io.File;

public class Environment {

    public static final String ENV_CONF_FILE_KEY = "KC_OIDC_CONF_FILE";
    public static final String ENV_TOKEN_CACHE_FILE_KEY = "KC_OIDC_TOKEN_CACHE_FILE";
    public static final String ENV_BROWSER_CMD = "KC_OIDC_BROWSER_CMD";

    public static final String SYSPROP_CONF_FILE_KEY = "kc.oidc.conf.file";
    public static final String SYSPROP_TOKEN_CACHE_FILE_KEY = "kc.oidc.token.cache.file";
    public static final String SYSPROP_BROWSER_CMD = "kc.oidc.conf.file";

    public static File getConfFile() {
        String envConfFile = getOption(ENV_CONF_FILE_KEY, SYSPROP_CONF_FILE_KEY);
        if (envConfFile != null) {
            return new File(envConfFile);
        } else {
            File userHome = new File(System.getProperty("user.home"));
            File homeDir = new File(userHome, ".kc");
            return new File(homeDir, "kc-oidc-config.yaml");
        }
    }
    public static File getTokenCacheFile() {
        String envTokenCacheFile = getOption(ENV_TOKEN_CACHE_FILE_KEY, SYSPROP_TOKEN_CACHE_FILE_KEY);
        if (envTokenCacheFile != null) {
            return new File(envTokenCacheFile);
        } else {
            File userHome = new File(System.getProperty("user.home"));
            File homeDir = new File(userHome, ".kc");
            return new File(homeDir, "kc-oidc-tokens.yaml");
        }
    }

    public static String getBrowserCmd() {
        return getOption(ENV_BROWSER_CMD, SYSPROP_BROWSER_CMD);
    }

    private static String getOption(String envKey, String sysPropKey) {
        String value = System.getenv(envKey);
        if (value != null) {
            return value;
        }
        return System.getProperty(sysPropKey);
    }

}
