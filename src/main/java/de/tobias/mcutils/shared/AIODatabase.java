package de.tobias.mcutils.shared;

import de.tobias.mcutils.bukkit.BukkitStaticClassSerializer;
import de.tobias.mcutils.bungee.BungeeStaticClassSerializer;
import de.tobias.mcutils.templates.Logger;
import de.tobias.mcutils.templates.StaticClassSerializer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class AIODatabase {

    private final File dbFile;
    Boolean isMySQL = false;

    String MYSQL_HOST = "192.168.178.1";
    String MYSQL_PORT = "3306";
    String MYSQL_USERNAME = "homes";
    String MYSQL_PASSWORD = "supersecure#123";
    String MYSQL_DB = "homes";
    private final ArrayList<String> tableNames = new ArrayList<>();
    private final Logger logger;

    Connection conn = null;

    public AIODatabase(File dataFolder, Logger pLogger) {
        logger = pLogger;
        File cfgFile = new File(dataFolder, "database.yml");
        dbFile = new File(dataFolder, "database.db");

        if(RuntimeDetector.isBukkit()) {
            StaticClassSerializer serial = new BukkitStaticClassSerializer(AIODatabase.class, cfgFile, pLogger);
            serial.doAll();
        } else {
            StaticClassSerializer serial = new BungeeStaticClassSerializer(AIODatabase.class, cfgFile, pLogger);
            serial.doAll();
        }
    }

    public boolean connect() {
        logger.info("Connecting to database...");
        try {
            if(!isMySQL) {
                if(!dbFile.getParentFile().exists()) {
                    if(!dbFile.getParentFile().mkdirs()) throw new Exception("Failed to create parent directories");
                    logger.warn("Created empty Plugin directory!");
                }
                Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:" + dbFile.getPath();
                conn = DriverManager.getConnection(url);
                logger.info("Now connected to SQLite database at: " + dbFile.getAbsolutePath());
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST + "/" + MYSQL_DB + "?" + "user=" + MYSQL_USERNAME + "&password=" + MYSQL_PASSWORD);
                logger.info("Now connected to MySQL at: " + MYSQL_HOST);
            }
            return true;
        } catch (Exception ex) {
            logger.error("Failed to connect to database: ");
            ex.printStackTrace();
            return false;
        }

    }

    public void disconnect() {
        try {
            if(conn != null && !conn.isClosed()) {
                logger.info("Disconnecting from database...");
                conn.close();
                logger.info("Database is now closed");
            }
        } catch (Exception ex) {
            logger.error("Failed to disconnect from database: ");
            ex.printStackTrace();
        }
    }

    public String format(String sql) {
        if(isMySQL) {
            for(String name : tableNames) {
                String tableName = "`" + name + "`";
                sql = sql.replaceAll(tableName, "`" + MYSQL_DB + "`." + tableName);
            }
        }
        return sql;
    }

    public boolean execute(String sql) {
        sql = format(sql);
        try {
            PreparedStatement exec = conn.prepareStatement(sql);
            exec.execute();
            return true;
        } catch (Exception ex) {
            logger.error("Failed database execute: ");
            logger.error(sql);
            ex.printStackTrace();
            return false;
        }
    }

    public ResultSet query(String sql) {
        sql = format(sql);
        try {
            PreparedStatement exec = conn.prepareStatement(sql);
            return exec.executeQuery();
        } catch (Exception ex) {
            logger.error("Failed database query: ");
            logger.error(sql);
            ex.printStackTrace();
            return null;
        }
    }

    public void registerTableName(String name) {
        tableNames.add(name);
    }
}
