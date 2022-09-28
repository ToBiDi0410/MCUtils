package de.tobias.mcutils.shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tobias.mcutils.templates.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class JSONClassSerializer<ContentType> {

    public transient Class c;
    public transient File configFile;
    public transient Logger logger;
    public transient Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public JSONClassSerializer(Class toSerialize, File file, Logger pLogger) {
        configFile = file;
        c = toSerialize;
        logger = pLogger;
    }

    public boolean create() {
        boolean existed = configFile.exists();
        try {
            if(!configFile.getParentFile().exists()) {
                if(!configFile.getParentFile().mkdirs()) throw new Exception("Failed to create parent directories");
            }
            if(!existed) configFile.createNewFile();
        } catch (Exception ex) {
            logger.error("[" + configFile.getName() + "] Failed to create file!");
            ex.printStackTrace();
            return false;
        }

        if(existed) return true;

        try {
            String json = gson.toJson(c.newInstance());
            FileUtils.write(configFile, json, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger.error("[" + configFile.getName() + "] Failed to save file!");
            ex.printStackTrace();
            return false;
        }
        logger.info("["  + configFile.getName() + "]" + " New file created!");
        return true;
    }

    public ContentType get() {
        logger.debug("[" + configFile.getName() + "] Loading data...");
        if(create()) {
            try {
                String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                ContentType o = (ContentType) gson.fromJson(json, c);
                return o;
            } catch (Exception ex) {
                logger.error("[" + configFile.getName() + "] Failed to create file!");
                ex.printStackTrace();
            }
        }
        return null;
    }
}
