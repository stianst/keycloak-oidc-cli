package org.keycloak.cli.oidc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.keycloak.cli.oidc.Constants;
import org.keycloak.cli.oidc.User;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

public class ConfigHandler {

    private static ConfigHandler configHandler;

    private File configFile;
    private ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private Config config;

    private ConfigHandler() throws ConfigException {
        if (System.getenv().containsKey(Constants.ENV_CONF_FILE_KEY)) {
            configFile = new File(System.getenv(Constants.ENV_CONF_FILE_KEY));
        } else if(System.getProperties().containsKey(Constants.SYSPROP_CONF_FILE_KEY)) {
            configFile = new File(System.getProperty(Constants.SYSPROP_CONF_FILE_KEY));
        } else {
            File userHome = new File(System.getProperty("user.home"));
            File homeDir = new File(userHome, ".kc");
            configFile = new File(homeDir, "oidc.yaml");
        }

        if (!configFile.getParentFile().isDirectory()) {
            configFile.getParentFile().mkdirs();
        }

        reload();
    }

    public static ConfigHandler get() throws ConfigException {
        if (configHandler == null) {
            configHandler = new ConfigHandler();
        }
        return configHandler;
    }

    public void reload() throws ConfigException {
        try {
            if (configFile.isFile()) {
                config = objectMapper.readValue(configFile, Config.class);
            }

            if (config == null) {
                config = new Config();
            }
        } catch (IOException e) {
            throw new ConfigException("Failed to load config", e);
        }
    }

    public ConfigHandler save() throws ConfigException {
        try {
            config.getContexts().sort(Comparator.comparing(Context::getName));

            if (config.getCurrent() == null && config.getContexts().size() == 1) {
                config.setCurrent(config.getContexts().get(0).getName());
            }

            objectMapper.writeValue(configFile, config);
        } catch (IOException e) {
            throw new ConfigException("Failed to save config", e);
        }
        return this;
    }

    public ConfigHandler setCurrent(String name) throws ConfigException {
        if (getContext(name) == null) {
            throw new ConfigException("Context not found");
        }
        config.setCurrent(name);
        return this;
    }

    public ConfigHandler set(Context context) {
        String current = config.getCurrent();

        delete(context.getName());
        config.getContexts().add(context);

        config.setCurrent(current);
        return this;
    }

    public ConfigHandler delete(String name) {
        Iterator<Context> contextItr = config.getContexts().iterator();
        while (contextItr.hasNext()) {
            if (contextItr.next().getName().equals(name)) {
                contextItr.remove();
            }
        }
        if (config.getCurrent() != null && config.getCurrent().equals(name)) {
            config.setCurrent(null);
        }
        return this;
    }

    public Context getCurrentContext() {
        return getContext(config.getCurrent());
    }

    public Context getContext(String name) {
        for (Context c : config.getContexts()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public void deleteTokens(String name) throws ConfigException {
        Context context = getContext(name);
        deleteTokens(context);
        save();
    }

    public void deleteTokens() throws ConfigException {
        for (Context context : config.getContexts()) {
            deleteTokens(context);
        }
        save();
    }

    private void deleteTokens(Context context) {
        context.setRefreshToken(null);
        context.setAccessToken(null);
        context.setIdToken(null);
    }

    public void printCurrentContext(boolean brief) throws ConfigException {
        try {
            Context context = copy(getContext(config.getCurrent()), brief);
            User.cli().print(objectMapper.writeValueAsString(context));
        } catch (IOException e) {
            throw new ConfigException("Failed to serialize config");
        }
    }

    public void printContext(String name, boolean brief) throws ConfigException {
        Context context = copy(getContext(name), brief);
        try {
            User.cli().print(objectMapper.writeValueAsString(context));
        } catch (IOException e) {
            throw new ConfigException("Failed to serialize config");
        }
    }

    public void printContexts(boolean brief) throws ConfigException {
        Config config = copy(this.config, brief);
        try {
            User.cli().print(objectMapper.writeValueAsString(config));
        } catch (IOException e) {
            throw new ConfigException("Failed to serialize config");
        }
    }

    private Config copy(Config config, boolean brief) {
        Config copy = new Config();
        copy.setCurrent(config.getCurrent());
        for (Context c : config.getContexts()) {
            copy.getContexts().add(copy(c, brief));
        }
        return copy;
    }

    private Context copy(Context context, boolean brief) {
        Context copy = new Context();
        copy.setName(context.getName());
        copy.setIssuer(context.getIssuer());
        copy.setFlow(context.getFlow());
        if (!brief) {
            copy.setClientId(context.getClientId());
            copy.setClientSecret(maskSecret(context.getClientSecret()));
            copy.setUsername(context.getUsername());
            copy.setUserPassword(maskSecret(context.getUserPassword()));
            copy.setRefreshToken(maskSecret(context.getRefreshToken()));
            copy.setIdToken(maskSecret(context.getIdToken()));
            copy.setAccessToken(maskSecret(context.getAccessToken()));
        }
        return copy;
    }

    private String maskSecret(String value) {
        return value != null ? "********" : null;
    }

}
