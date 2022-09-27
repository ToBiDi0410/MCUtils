package de.tobias.mcutils.templates;

@SuppressWarnings("unused")
public abstract class Logger {

    public static Logger instance = null;

    public String prefix;
    public Boolean debug = false;

    public Logger(String pPrefix) {
        prefix = pPrefix;
        instance = this;
    }

    public Logger(String pPrefix, Logger logger) {
        prefix = logger.prefix + pPrefix;
    }

    public abstract void info(String msg);
    public abstract void warn(String msg);
    public abstract void error(String msg);
    public abstract void debug(String msg);

    public static Logger getInstance() {
        return instance;
    }
}
