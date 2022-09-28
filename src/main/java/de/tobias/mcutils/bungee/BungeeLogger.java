package de.tobias.mcutils.bungee;

import de.tobias.mcutils.templates.Logger;
import net.md_5.bungee.api.ProxyServer;

@SuppressWarnings("unused")
public class BungeeLogger extends Logger {

    public BungeeLogger(String pPrefix) {
        super(pPrefix);
    }
    public BungeeLogger(String pPrefix, Logger logger) {
        super(pPrefix, logger);
    }

    @Override
    public void info(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(BungeeMigrationHelpers.parseLegacyText(prefix + "§7[§2INFO§7] §7" + msg.replaceAll("§x", "§7")));
    }

    @Override
    public void warn(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(BungeeMigrationHelpers.parseLegacyText(prefix + "§7[§6WARN§7] §e" + msg.replaceAll("§x", "§e")));
    }

    @Override
    public void error(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(BungeeMigrationHelpers.parseLegacyText(prefix + "§7[§4ERROR§7] §c" + msg.replaceAll("§x", "§c")));
    }

    @Override
    public void debug(String msg) {
        if(debug) ProxyServer.getInstance().getConsole().sendMessage(BungeeMigrationHelpers.parseLegacyText(prefix + "§7[§5DEBUG§7] §d" + msg.replaceAll("§x", "§d")));
    }
}
