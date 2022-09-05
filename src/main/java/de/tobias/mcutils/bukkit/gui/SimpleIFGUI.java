package de.tobias.mcutils.bukkit.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import de.tobias.mcutils.shared.Resultable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class SimpleIFGUI extends Resultable {

    public HumanEntity clicker;
    public ChestGui gui;

    public SimpleIFGUI(int rows, String title) {
        gui = new ChestGui(rows, title);
        gui.setOnGlobalClick(event -> {
            event.setCancelled(true);
            clicker = event.getWhoClicked();
        });
    }

    public SimpleIFGUI showTo(Player p) {
        gui.show(p);
        return this;
    }

    private void close() {
        gui.getViewers().forEach(HumanEntity::closeInventory);
    }

}
