package de.tobias.mcutils.bukkit.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.tobias.mcutils.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmGUI extends SimpleIFGUI {

    public Boolean accepted;

    public ConfirmGUI(String title, String confirmName, String declineName) {
        super(3, title);
        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        gui.addPane(background);

        StaticPane reasons = new StaticPane(0, 1, 9, 1);
        gui.addPane(reasons);

        reasons.addItem(new GuiItem(new ItemBuilder(Material.GREEN_WOOL).setDisplayName(confirmName).build(), (event) -> handleEvent(event, true)), 1, 0);
        reasons.addItem(new GuiItem(new ItemBuilder(Material.RED_WOOL).setDisplayName(declineName).build(), (event) -> handleEvent(event, false)), 7, 0);
    }

    private void handleEvent(InventoryClickEvent event, Boolean accept) {
        accepted = accept;
        result();
    }
}
