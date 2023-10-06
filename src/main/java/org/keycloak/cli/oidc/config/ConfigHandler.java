package org.keycloak.cli.oidc.config;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

public class ConfigHandler {

    private static ConfigHandler configHandler;

    private YamlFileStore<Config> configStore;
    private Config config;

    private ConfigHandler() throws ConfigException {
        configStore = new YamlFileStore<>(Environment.getConfFile(), Config.class);
        reload();
    }

    public static ConfigHandler get() throws ConfigException {
        if (configHandler == null) {
            configHandler = new ConfigHandler();
        }
        return configHandler;
    }

    public static void clearInstance() {
        configHandler = null;
    }

    public Config getConfig() {
        return config;
    }

    public Context getCurrentContext() throws ConfigException {
        String current = config.getCurrent();
        if (current == null || current.isEmpty()) {
            throw new ConfigException("Default context not set");
        }
        return getContext(current);
    }

    public Context getContext(String name) throws ConfigException {
        for (Context c : config.getContexts()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        throw new ConfigException("Context '" + name + "' not found");
    }

    public void reload() throws ConfigException {
        try {
            config = configStore.reload();
            if (config == null) {
                config = new Config();
            }
        } catch (IOException e) {
            throw new ConfigException("Failed to load config", e);
        }
    }

    public ConfigHandler save() throws ConfigException {
        try {
            if (config.getContexts().isEmpty()) {
                configStore.delete();
            } else {
                config.getContexts().sort(Comparator.comparing(Context::getName));

                if (config.getCurrent() == null && config.getContexts().size() == 1) {
                    config.setCurrent(config.getContexts().get(0).getName());
                }

                configStore.save(config);
            }
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

}
