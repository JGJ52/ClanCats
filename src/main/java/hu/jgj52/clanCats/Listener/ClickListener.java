package hu.jgj52.clanCats.Listener;

import hu.jgj52.clanCats.GUIs.MyClanGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getHolder() instanceof MyClanGUI) {
            event.setCancelled(true);
        }
    }
}
