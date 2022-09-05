package de.tobias.mcutils.shared;

@SuppressWarnings("unused")
public class RuntimeDetector {

    public static boolean isBukkit() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isBungee() {
        try {
            Class.forName("net.md_5.bungee.api.ProxyServer");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
