package hu.jgj52.clanCats.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static hu.jgj52.clanCats.ClanCats.plugin;

public abstract class GUI implements InventoryHolder {
    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(this, 54, plugin.getConfig().getString("guis." + getClass().getSimpleName() + ".title"));

        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        for (int i = 0; i < 54; i++) {
            if (i <= 9 || List.of(17, 18, 26, 27, 35, 36).contains(i) || i >= 44) {
                gui.setItem(i, outline);
            } else {
                gui.setItem(i, inline);
            }
        }

        if (open(gui, player)) {
            player.openInventory(gui);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    protected abstract boolean open(Inventory gui, Player player);

    private String getMessage(String message) {
        return plugin.getConfig().getString("guis." + getClass().getSimpleName() + "." + message);
    }
}
