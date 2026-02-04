package hu.jgj52.clanCats.Listener;

import hu.jgj52.clanCats.Types.Clan;
import hu.jgj52.clanCats.Utils.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Player enemy)) return;

        if (Clan.fromPlayer(player) == Clan.fromPlayer(enemy)) {
            event.setCancelled(true);
            enemy.sendMessage(Message.getMessage("cantAttackTeammate"));
        }
    }
}
