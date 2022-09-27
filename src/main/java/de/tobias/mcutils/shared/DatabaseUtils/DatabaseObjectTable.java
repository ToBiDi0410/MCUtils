package de.tobias.mcutils.shared.DatabaseUtils;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.mcutils.bungee.BungeeLogger;
import de.tobias.mcutils.shared.AIODatabase;
import de.tobias.mcutils.shared.CachedObject;
import de.tobias.mcutils.shared.RuntimeDetector;
import de.tobias.mcutils.templates.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.*;

@SuppressWarnings("unused")
public class DatabaseObjectTable<ContentType> {

    public String name;
    public Class objectClass;

    public HashMap<String, Class> fields = new HashMap<>();
    public AIODatabase database;
    public Logger logger;
    public ArrayList<CachedObject> cache = new ArrayList<>();
    public HashMap<String, HashMap<String, Object>> byFieldCache = new HashMap<>();
    public boolean enableCaching = true;

    public long lastUpdate = System.currentTimeMillis();

    public DatabaseObjectTable(Class pObjectClass, String pName, AIODatabase db, Logger baseLogger) {
        if(!(pObjectClass.getSuperclass() == DatabaseObjectTableEntry.class)) throw new Error("Objects in 'DatabaseObjectTable' have to extend 'DatabaseObjectTableEntry'");
        name = pName;
        objectClass = pObjectClass;
        database = db;
        logger = RuntimeDetector.isBukkit() ? new BukkitLogger("§7[§bTABLE:§6" + name + "§7]" + baseLogger) : new BungeeLogger("§7[§bTABLE:§6" + name + "§7]" + baseLogger);
    }

    public void init() {
        logger.info( "Preparing table for class §b'" + objectClass.getName() + "'§x...");
        for(Field field : objectClass.getDeclaredFields()) {
            if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                if(field.getType() == int.class) logger.warn("Use of primitive 'int' is not allowed inside DatabaseObjects");
                if(field.getType() == boolean.class) logger.warn("Use of primitive 'boolean' is not allowed inside DatabaseObjects");
                if(field.getType() == float.class) logger.warn("Use of primitive 'float' is not allowed inside DatabaseObjects");
                if(field.getType() == long.class) logger.warn("Use of primitive 'long' is not allowed inside DatabaseObjects");
                if(field.getType() == double.class) logger.warn("Use of primitive 'double' is not allowed inside DatabaseObjects");
                if(field.getType() == Integer.class) fields.put(field.getName().toUpperCase(), Integer.class);
                if(field.getType() == Float.class) fields.put(field.getName().toUpperCase(), Float.class);
                if(field.getType() == Boolean.class) fields.put(field.getName().toUpperCase(), Boolean.class);
                if(field.getType() == Long.class) fields.put(field.getName().toUpperCase(), Long.class);
                if(field.getType() == Double.class) fields.put(field.getName().toUpperCase(), Double.class);
                if(field.getType() == String.class) fields.put(field.getName().toUpperCase(), String.class);
                if(field.getType() == UUID.class) fields.put(field.getName().toUpperCase(), UUID.class);
            }
        }

        String sqlQuery = "CREATE TABLE IF NOT EXISTS `" + name + "` (`ID` TEXT NOT NULL";
        for(Map.Entry<String, Class> field : fields.entrySet()) sqlQuery += ", `" + field.getKey().toUpperCase() + "` " + ClassToSQLType(field.getValue());
        sqlQuery += ");";
        database.registerTableName(name);
        database.execute(sqlQuery);
        logger.info("Created table with §a" + fields.size() + " §xfields");
    }

    public ArrayList<ContentType> getAllByField(String searchFieldName, Object value) {
        String cachedName = "MATCHFIELDVALUE|||" + searchFieldName + "|||" + value.toString();
        Optional<CachedObject> cached = cache.stream().filter((a) -> a.id.equalsIgnoreCase(cachedName)).findAny();
        if(cached.isPresent()) {
            if(cached.get().fetched >= lastUpdate) {
                logger.debug("Took from cache: §6" + cachedName);
                return (ArrayList<ContentType>) cached.get().data;
            }
        }

        if(!fields.containsKey(searchFieldName.toUpperCase())) throw new Error("Unknown field: " + searchFieldName);
        ArrayList<ContentType> entries = new ArrayList<>();
        try {
            ResultSet rs = database.query("SELECT * FROM `" + name + "` WHERE `" + searchFieldName.toUpperCase() + "` = " + ObjectToSQLParameter(value) + ";");
            while(rs.next()) entries.add(getByID(rs.getString("ID")));

            if(cached.isPresent()) {
                cached.get().fetched = System.currentTimeMillis();
                cached.get().data = entries;
            } else if(enableCaching) {
                CachedObject newCachedObject = new CachedObject();
                newCachedObject.id = cachedName;
                newCachedObject.fetched = System.currentTimeMillis();
                newCachedObject.data = entries;
                cache.add(newCachedObject);
            }

        } catch (Exception ex) {
            logger.error("Failed to get data from table by fields:");
            ex.printStackTrace();
        }
        return entries;
    }

    public ContentType getByID(String id) {
        String cachedName = "MATCHID|||" + id;
        Optional<CachedObject> cached = cache.stream().filter((a) -> a.id.equalsIgnoreCase(cachedName)).findAny();
        if(cached.isPresent()) {
            if(cached.get().fetched >= lastUpdate) {
                logger.debug("Took from cache: §6" + cachedName);
                return (ContentType) cached.get().data;
            }
        }

        try {
            ResultSet rs = database.query("SELECT * FROM `" + name + "` WHERE `ID` = " + ObjectToSQLParameter(id) + ";");
            if(rs.next()) {
                ContentType entry = (ContentType) objectClass.getDeclaredConstructor().newInstance();
                ((DatabaseObjectTableEntry) entry).SET_FROM_SQL(rs.getString("ID"), this);

                for (Field field : objectClass.getDeclaredFields()) {
                    String fieldName = field.getName().toUpperCase();
                    if (fields.containsKey(fieldName)) {
                        Class fieldClass = fields.get(fieldName);
                        field.setAccessible(true);

                        if (fields.get(fieldName) == Long.class) field.set(entry, rs.getLong(fieldName));
                        else if (fields.get(fieldName) == Double.class) field.set(entry, rs.getDouble(fieldName));
                        else if (fields.get(fieldName) == Float.class) field.set(entry, rs.getFloat(fieldName));
                        else if (fields.get(fieldName) == Boolean.class) field.set(entry, rs.getBoolean(fieldName));
                        else if (fields.get(fieldName) == UUID.class) field.set(entry, UUID.fromString(rs.getString(fieldName)));
                        else field.set(entry, rs.getObject(fieldName));
                    }
                }

                if(cached.isPresent()) {
                    cached.get().fetched = System.currentTimeMillis();
                    cached.get().data = entry;
                } else if(enableCaching) {
                    CachedObject newCachedObject = new CachedObject();
                    newCachedObject.id = cachedName;
                    newCachedObject.fetched = System.currentTimeMillis();
                    newCachedObject.data = entry;
                    cache.add(newCachedObject);
                }

                return entry;
            } else return null;
        } catch (Exception ex) {
            logger.error("Failed to get entry with ID:");
            ex.printStackTrace();
            return null;
        }
    }

    public boolean insert(ContentType entry) {
        try {
            String sqlStart = "INSERT INTO `" + name + "` (%FIELDS%) VALUES (%VALUES%)";
            StringBuilder sqlFields = new StringBuilder("`ID`");
            StringBuilder sqlValues = new StringBuilder("'" + ((DatabaseObjectTableEntry) entry).getID() + "'");

            for(Field field : entry.getClass().getDeclaredFields()) {
                if(fields.containsKey(field.getName().toUpperCase())) {
                    field.setAccessible(true);
                    sqlFields.append(", `").append(field.getName().toUpperCase()).append("`");
                    sqlValues.append(", ").append(ObjectToSQLParameter(field.get(entry)));
                }
            }

            String fullSql = sqlStart.replace("%FIELDS%", sqlFields.toString()).replace("%VALUES%", sqlValues.toString());
            lastUpdate = System.currentTimeMillis();
            return database.execute(fullSql);
        } catch (Exception ex) {
            logger.error("Failed to save object:");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(ContentType entry) {
        if(!drop(entry)) return false;
        return insert(entry);
    }

    public boolean drop(ContentType entry) {
        lastUpdate = System.currentTimeMillis();
        return database.execute("DELETE FROM `" + name + "` WHERE `ID` = '" + ((DatabaseObjectTableEntry<ContentType>) entry).getID() + "';");
    }

    public static String ClassToSQLType(Class c) {
        if(c == Integer.class) return "INT";
        if(c == Long.class) return "BIGINT";
        if(c == String.class) return "TEXT";
        if(c == Float.class) return "FLOAT";
        if(c == Double.class) return "DOUBLE";
        if(c == UUID.class) return "TEXT";
        return "TEXT";
    }

    public static String ObjectToSQLParameter(Object o) {
        if(o == null) return "null";
        if(o.getClass() == String.class) return "'" + o + "'";
        return o.toString();
    }
}