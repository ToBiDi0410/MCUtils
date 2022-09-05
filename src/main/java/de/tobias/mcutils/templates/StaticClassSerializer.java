package de.tobias.mcutils.templates;

import java.io.File;

public abstract class StaticClassSerializer {

    protected Logger logger;
    protected Class toSerialize;
    public File configFile;

    public StaticClassSerializer(Class c, File f, Logger pLogger) {
        toSerialize = c;
        configFile = f;
        logger = pLogger;
    }

    public abstract boolean loadConfig();
    public abstract void saveAllFields();
    public abstract Object getField(String key);
    public abstract void setField(String key, Object insert);

    public Boolean doAll() {
        if(!loadConfig()) return false;
        saveAllFields();
        return true;
    }
}