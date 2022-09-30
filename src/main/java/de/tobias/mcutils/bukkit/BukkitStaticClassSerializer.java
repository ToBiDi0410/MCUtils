package de.tobias.mcutils.bukkit;

import de.tobias.mcutils.templates.Logger;
import de.tobias.mcutils.templates.StaticClassSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class BukkitStaticClassSerializer extends StaticClassSerializer {

    public YamlConfiguration configuration;

    public BukkitStaticClassSerializer(Class c, File f, Logger logger) {
        super(c, f, logger);
    }

    @Override
    public boolean loadConfig() {
        logger.debug("[" + configFile.getName() + "] Loading data...");
        try {
            if(!configFile.getParentFile().exists()) {
                if(!configFile.getParentFile().mkdirs()) throw new Exception("Failed to create parent directories");
            }
            if(!configFile.exists()) configFile.createNewFile();
        } catch (Exception ex) {
            logger.error("[" + configFile.getName() + "] Failed to create file!");
            ex.printStackTrace();
            return false;
        }

        try {
            configuration = YamlConfiguration.loadConfiguration(configFile);
            Field[] attributes = toSerialize.getDeclaredFields();

            for(String key : configuration.getKeys(true)) {
                for(Field field : attributes) {
                    if(field.getName().equalsIgnoreCase(key)) {
                        if(field.getType() == String.class) {
                            field.set(null, configuration.getString(key));
                        } else if (field.getType() == Integer.class) {
                            field.set(null, configuration.getInt(key));
                        } else if (field.getType() == Boolean.class) {
                            field.set(null, configuration.getBoolean(key));
                        } else if (field.getType() == Long.class) {
                            field.set(null, configuration.getLong(key));
                        } else if (field.getType() == Double.class) {
                            field.set(null, configuration.getDouble(key));
                        } else if (field.getType() == Location.class) {
                            field.set(null, configuration.getLocation(key));
                        } else if (field.getType() == Float.class) {
                            field.set(null, Double.valueOf(configuration.getDouble(key)).floatValue());
                        }  else if (field.getType() == ArrayList.class) {
                            field.set(null, new ArrayList<>(configuration.getList(key)));
                        } else {
                            logger.warn("[" + configFile.getName() + "] Field cannot be loaded: " + field.getType() + " (UNKNOWN TYPE)");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("[" + configFile.getName() + "] Failed to load value from config!");
            ex.printStackTrace();
            return false;
        }

        logger.info("["  + configFile.getName() + "]" + " File loaded!");
        return true;
    }

    @Override
    public void saveAllFields() {
        Field[] attributes = toSerialize.getDeclaredFields();
        for(Field field : attributes) {
            if(Modifier.isPublic(field.getModifiers())) {
                if(field.getType() == String.class || field.getType() == Integer.class || field.getType() == Boolean.class || field.getType() == Location.class || field.getType() == ArrayList.class || field.getType() == Float.class || field.getType() == Double.class || field.getType() == Long.class) {
                    try {
                        configuration.set(field.getName(), field.get(null));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    logger.debug("["  + configFile.getName() + "] Ignoring Field: " + field.getName() + " (" + field.getType() + ")");
                }
            } else {
                logger.debug("["  + configFile.getName() + "] Ignoring Field: " + field.getName() + " (" + (Modifier.isPublic(field.getModifiers()) ? "PUBLIC" : (Modifier.isProtected(field.getModifiers()) ? "PROTECTED" : "PRIVATE")) + ")");
            }
        }
        try {
            configuration.save(configFile);
        } catch (Exception ex) {
            logger.error("["  + configFile.getName() + "] Failed to save file!");
            ex.printStackTrace();
        }
    }

    @Override
    public Object getField(String key) {
        return configuration.get(key);
    }

    @Override
    public void setField(String key, Object insert) {
        configuration.set(key, insert);
    }
}