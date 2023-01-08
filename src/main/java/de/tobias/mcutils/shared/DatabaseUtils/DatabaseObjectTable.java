package de.tobias.mcutils.shared.DatabaseUtils;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.mcutils.bungee.BungeeLogger;
import de.tobias.mcutils.shared.AIODatabase;
import de.tobias.mcutils.shared.CachedObject;
import de.tobias.mcutils.shared.RuntimeDetector;
import de.tobias.mcutils.templates.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
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

    public DatabaseObjectTable(Class pObjectClass, String pName, AIODatabase db, Logger baseLogger) {
        if(!(pObjectClass.getSuperclass() == DatabaseObjectTableEntry.class)) throw new Error("Objects in 'DatabaseObjectTable' have to extend 'DatabaseObjectTableEntry'");
        name = pName;
        objectClass = pObjectClass;
        database = db;
        logger = RuntimeDetector.isBukkit() ? new BukkitLogger("§7[§bTABLE:§6" + name + "§7] ", baseLogger) : new BungeeLogger("§7[§bTABLE:§6" + name + "§7] ", baseLogger);
    }

    public void init() {
        logger.info( "Preparing table for class §b'" + objectClass.getName() + "'§x...");
        for(Field field : objectClass.getDeclaredFields()) {
            if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                if(field.getType() == int.class) logger.warn("Use of primitive 'int' is not allowed inside DatabaseObjects");
                else if(field.getType() == boolean.class) logger.warn("Use of primitive 'boolean' is not allowed inside DatabaseObjects");
                else if(field.getType() == float.class) logger.warn("Use of primitive 'float' is not allowed inside DatabaseObjects");
                else if(field.getType() == long.class) logger.warn("Use of primitive 'long' is not allowed inside DatabaseObjects");
                else if(field.getType() == double.class) logger.warn("Use of primitive 'double' is not allowed inside DatabaseObjects");
                else if(field.getType() == Integer.class) fields.put(field.getName().toUpperCase(), Integer.class);
                else if(field.getType() == Float.class) fields.put(field.getName().toUpperCase(), Float.class);
                else if(field.getType() == Boolean.class) fields.put(field.getName().toUpperCase(), Boolean.class);
                else if(field.getType() == Long.class) fields.put(field.getName().toUpperCase(), Long.class);
                else if(field.getType() == Double.class) fields.put(field.getName().toUpperCase(), Double.class);
                else if(field.getType() == String.class) fields.put(field.getName().toUpperCase(), String.class);
                else if(field.getType() == UUID.class) fields.put(field.getName().toUpperCase(), UUID.class);
                else if(field.getType() == BigInteger.class) fields.put(field.getName().toUpperCase(), BigInteger.class);
                else if(field.getType() == BigDecimal.class) fields.put(field.getName().toUpperCase(), BigDecimal.class);
                else logger.debug("Ignoring field '" + field.getName() + "' because of type");
            } else {
                logger.debug("Ignoring field '" + field.getName() + "' because of modifiers");
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
        ArrayList<ContentType> entries = new ArrayList<>();
        for(String id : getAllIDsByField(searchFieldName, value)) {
            entries.add(getByID(id));
        }
        return entries;
    }

    public ArrayList<String> getAllIDsByField(String searchFieldName, Object value) {
        String cachedName = "MATCHFIELDVALUEID|||" + searchFieldName + "|||" + value.toString();
        Optional<CachedObject> cached = cache.stream().filter((a) -> a.id.equalsIgnoreCase(cachedName)).findAny();
        if(cached.isPresent()) {
            logger.debug("Took from cache: §6" + cachedName);
            return (ArrayList<String>) cached.get().data;
        } else {
            logger.debug("Ignoring cache because of expiry: §6" + cachedName);
        }

        if(!fields.containsKey(searchFieldName.toUpperCase())) throw new Error("Unknown field: " + searchFieldName);
        ArrayList<String> entries = new ArrayList<>();
        try {
            ResultSet rs = database.query("SELECT `ID` FROM `" + name + "` WHERE `" + searchFieldName.toUpperCase() + "` = " + ObjectToSQLParameter(value) + ";");
            while(rs.next()) entries.add(rs.getString("ID"));

            if(enableCaching) {
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

    public ArrayList<ContentType> getAll(Integer limit) {
        String cachedName = "GETALL|||";
        Optional<CachedObject> cached = cache.stream().filter((a) -> a.id.equalsIgnoreCase(cachedName)).findAny();
        if(cached.isPresent()) {
            logger.debug("Took from cache: §6" + cachedName);
            return (ArrayList<ContentType>) cached.get().data;
        } else {
            logger.debug("Ignoring cache because of expiry: §6" + cachedName);
        }

        ArrayList<ContentType> entries = new ArrayList<>();
        try {
            ResultSet rs = database.query("SELECT `ID` FROM `" + name + "` LIMIT " + limit + ";");
            while(rs.next()) entries.add(getByID(rs.getString("ID")));

            if(enableCaching) {
                CachedObject newCachedObject = new CachedObject();
                newCachedObject.id = cachedName;
                newCachedObject.fetched = System.currentTimeMillis();
                newCachedObject.data = entries;
                cache.add(newCachedObject);
            }
        } catch (Exception ex) {
            logger.error("Failed to all entries from table:");
            ex.printStackTrace();
        }
        return entries;
    }

    public boolean updateFieldForAll(String fieldName, Object data) {
        boolean res = database.execute("UPDATE `" + name + "` SET `" + fieldName.toUpperCase() + "` = " + ObjectToSQLParameter(data) + ";");
        if(!res) return false;
        cache.clear();
        return true;
    }

    public ArrayList<ContentType> getOrderedByWithLimit(String criteria, Integer count) {
        ArrayList<ContentType> entries = new ArrayList<>();
        try {
            ResultSet rs = database.query("SELECT `ID` FROM `" + name + "` ORDER BY CAST(`" + criteria.toUpperCase() + "` as NUMERIC) DESC LIMIT " + count + ";");
            while(rs.next()) entries.add(getByID(rs.getString("ID")));
        } catch (Exception ex) {
            logger.error("Failed to get data from table by criteria:");
            ex.printStackTrace();
        }
        return entries;
    }

    public ContentType getByID(String id) {
        String cachedName = "MATCHID|||" + id;
        Optional<CachedObject> cached = cache.stream().filter((a) -> a.id.equalsIgnoreCase(cachedName)).findAny();
        if(cached.isPresent()) {
            logger.debug("Took from cache: §6" + cachedName);
            return (ContentType) cached.get().data;
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
                        else if (fields.get(fieldName) == BigInteger.class) field.set(entry, new BigInteger(rs.getString(fieldName)));
                        else if (fields.get(fieldName) == BigDecimal.class) field.set(entry, new BigDecimal(rs.getString(fieldName)));
                        else field.set(entry, rs.getObject(fieldName));
                    }
                }

                if(enableCaching) {
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
            //Create a String of the fields and a string of the parameter question marks
            String fieldsString = "";
            String paramentersString = "";
            for(String fieldName : getFinalFieldNames(entry)) {
                fieldsString += ("`" + fieldName + "`,");
                paramentersString += "?,";
            }
            if(fieldsString.endsWith(",")) fieldsString = fieldsString.substring(0, fieldsString.length()-1);
            if(paramentersString.endsWith(",")) paramentersString = paramentersString.substring(0, paramentersString.length()-1);

            //Prepare the Statement
            String sqlWithFields = "INSERT INTO `" + name + "` (" + fieldsString + ") VALUES (" + paramentersString + ")";
            PreparedStatement stmt = database.getPreparedStatement(sqlWithFields);

            //Add all values (starting from 1)
            int counter = 1;
            for(Object fieldValue : getFinalFieldValues(entry)) {
                if(fieldValue.getClass() == UUID.class) stmt.setString(counter, fieldValue.toString());
                else stmt.setObject(counter, fieldValue);
                logger.debug(counter + " --> " + fieldValue.toString());
                counter++;
            }

            boolean res = database.executeStatement(stmt);
            generalStructureChanged();
            return res;
        } catch (Exception ex) {
            logger.error("Failed to save object:");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(ContentType entry) {
        try {
            //Create a String of the fields with parameters
            String fieldsString = "";
            for(String fieldName : getFinalFieldNames(entry)) fieldsString += ("`" + fieldName + "` = ?,");
            if(fieldsString.endsWith(",")) fieldsString = fieldsString.substring(0, fieldsString.length()-1);

            //Prepare the Statement
            String sqlWithFields = "UPDATE `" + name + "` SET " + fieldsString + " WHERE `ID` = ?;";
            PreparedStatement stmt = database.getPreparedStatement(sqlWithFields);

            //Add all values (starting from 1)
            int counter = 1;
            for(Object fieldValue : getFinalFieldValues(entry)) {
                if(fieldValue.getClass() == UUID.class) stmt.setString(counter, fieldValue.toString());
                else stmt.setObject(counter, fieldValue);
                logger.debug(counter + " --> " + fieldValue.toString());
                counter++;
            }

            //Add ID as last value (for WHERE selector)
            stmt.setString(counter, ((DatabaseObjectTableEntry<?>) entry).getID());
            boolean res = database.executeStatement(stmt);
            removeCache(entry);
            return res;
        } catch (Exception ex) {
            logger.error("Failed to update object:");
            ex.printStackTrace();
            return false;
        }
    }

    public void removeCache(ContentType entry) {
        cache.removeIf(obj -> obj.data == entry);
    }

    private ArrayList<String> getFinalFieldNames(ContentType entry) {
        ArrayList<String> list = new ArrayList<>();
        list.add("ID");

        for(Field field : entry.getClass().getDeclaredFields()) {
            if(fields.containsKey(field.getName().toUpperCase())) {
                field.setAccessible(true);
                list.add(field.getName().toUpperCase());
            }
        }

        return list;
    }

    private ArrayList<Object> getFinalFieldValues(ContentType entry) throws Exception {
        ArrayList<Object> list = new ArrayList<>();
        list.add(((DatabaseObjectTableEntry) entry).getID());

        for(Field field : entry.getClass().getDeclaredFields()) {
            if(fields.containsKey(field.getName().toUpperCase())) {
                field.setAccessible(true);
                list.add(field.get(entry));
            }
        }

        return list;
    }

    private void generalStructureChanged() {
        cache.removeIf(obj -> (obj.id.contains("MATCHFIELDVALUEID|||") || obj.id.contains("GETALL|||")));
    }

    public boolean drop(ContentType entry) {
        generalStructureChanged();
        return database.execute("DELETE FROM `" + name + "` WHERE `ID` = '" + ((DatabaseObjectTableEntry<ContentType>) entry).getID() + "';");
    }

    public static String ClassToSQLType(Class c) {
        if(c == Integer.class) return "INT";
        if(c == Long.class) return "BIGINT";
        if(c == String.class) return "TEXT";
        if(c == Float.class) return "FLOAT";
        if(c == Double.class) return "DOUBLE";
        if(c == UUID.class) return "TEXT";
        if(c == BigInteger.class) return "BIGINT";
        if(c == BigDecimal.class) return "TEXT";
        return "TEXT";
    }

    public static String ObjectToSQLParameter(Object o) {
        if(o == null) return "null";
        if(o.getClass() == String.class) return "'" + o + "'";
        if(o.getClass() == BigInteger.class) return ((BigInteger) o).longValue() + "";
        return o.toString();
    }
}
