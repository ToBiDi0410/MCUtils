package de.tobias.mcutils.bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    ItemStack is;

    public ItemBuilder(Material m) {
        is = new ItemStack(m);
    }

    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(String... lore){
        ItemMeta im = is.getItemMeta();
        ArrayList<String> finalArray = new ArrayList<>();
        for(String loreText : lore) finalArray.addAll(List.of(loreText.split("\n")));
        im.setLore(finalArray);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        ItemMeta im = is.getItemMeta();
        List<String> loreA = im.getLore() == null ? new ArrayList<String>() : im.getLore();
        loreA.add(lore);
        im.setLore(loreA);
        is.setItemMeta(im);
        return this;
    }

    public ItemStack build() {
        return is;
    }
}
