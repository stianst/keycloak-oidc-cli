package org.keycloak.cli.oidc.config;

public class Environment {

    public static final String ENV_CONF_FILE_KEY = "KC_OIDC_CONF_FILE";
    public static final String ENV_BROWSER_CMD = "KC_OIDC_BROWSER_CMD";

    public static final String SYSPROP_CONF_FILE_KEY = "kc.oidc.conf.file";
    public static final String SYSPROP_BROWSER_CMD = "kc.oidc.conf.file";

    public static String getConfFile() {
        return getOption(ENV_CONF_FILE_KEY, SYSPROP_CONF_FILE_KEY);
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
