package de.tobias.mcutils.shared.DatabaseUtils;

import java.util.UUID;

@SuppressWarnings("unused")
public class DatabaseObjectTableEntry<ContentType> {

    private String id;
    private DatabaseObjectTable<ContentType> table;
    private Object data;

    public DatabaseObjectTableEntry() {
        id = UUID.randomUUID().toString();
        table = null;
    }

    public void SET_FROM_SQL(String uuid, DatabaseObjectTable<ContentType> pTable) {
        id = uuid;
        table = pTable;
    }

    public String getID() {
        return id;
    }

    public boolean save() {
        if(table == null) return false;
        return table.update((ContentType) this);
    }

    public boolean create(DatabaseObjectTable<ContentType> pTable) {
        table = pTable;
        return save();
    }

    public boolean drop() {
        if(table == null) return false;
        return table.drop((ContentType) this);
    }
}
