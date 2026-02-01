package hu.jgj52.clanCats.GUIs;

import hu.jgj52.clanCats.Types.Clan;
import hu.jgj52.clanCats.Utils.Message;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class MyClanGUI extends GUI {
    @Override
    protected boolean open(Inventory gui, Player player) {
        List<ItemStack> players = new ArrayList<>();
        Clan clan = Clan.fromPlayer(player);
        if (clan == null) {
            player.sendMessage(Message.getMessage("notInClan"));
            return false;
        }
        for (OfflinePlayer p : clan.getPlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§f" + p.getName());
            meta.setLore(List.of("§6" + clan.getRole(p).name()));
            head.setItemMeta(meta);
            players.add(head);
        }
        int i = 10;
        for (ItemStack p : players) {
            if (i == 43) break;
            gui.setItem(i, p);
            if (List.of(16, 25, 34).contains(i)) {
                i += 3;
            } else {
                i++;
            }

        }
        return true;
    }
}
