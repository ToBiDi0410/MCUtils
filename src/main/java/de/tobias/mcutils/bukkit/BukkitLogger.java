package de.tobias.mcutils.bukkit;

import de.tobias.mcutils.templates.Logger;
import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class BukkitLogger extends Logger {

    public BukkitLogger(String pPrefix) {
        super(pPrefix);
    }

    @Override
    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§7[§2INFO§7] §7" + msg.replaceAll("§x", "§7"));
    }

    @Override
    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§7[§6WARN§7] §e" + msg.replaceAll("§x", "§7"));
    }

    @Override
    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§7[§4ERROR§7] §c" + msg.replaceAll("§x", "§c"));
    }

    @Override
    public void debug(String msg) {
        if(debug) Bukkit.getConsoleSender().sendMessage(prefix + "§7[§5DEBUG§7] §d" + msg.replaceAll("§x", "§d"));
    }
}
