package de.tobias.mcutils.bungee;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

@SuppressWarnings("unused")
public class BungeeMigrationHelpers {

    public static BaseComponent[] parseLegacyText(String msg) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
