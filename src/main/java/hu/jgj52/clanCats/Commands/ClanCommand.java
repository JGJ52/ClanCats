package hu.jgj52.clanCats.Commands;

import hu.jgj52.clanCats.GUIs.MyClanGUI;
import hu.jgj52.clanCats.Types.Clan;
import hu.jgj52.clanCats.Types.Role;
import hu.jgj52.clanCats.Utils.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static hu.jgj52.clanCats.ClanCats.plugin;

public class ClanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.getMessage("notPlayer"));
            return true;
        }

        Clan clan = Clan.fromPlayer(player);

        if (args.length > 0) {
            if (args[0].equals("create")) {
                if (clan == null) {
                    if (args.length < 2) {
                        player.sendMessage(Message.getMessage("noArgs"));
                        return true;
                    }
                    List<String> pls = new ArrayList<>();
                    pls.add(player.getUniqueId().toString());
                    ConfigurationSection clans = plugin.getConfig().getConfigurationSection("data.clans");
                    if (clans == null) return false;
                    Collection<Integer> cls = new ArrayList<>();
                    for (String key : clans.getKeys(false)) {
                        cls.add(Integer.parseInt(key));
                    }
                    String id = cls.isEmpty() ? "1" : String.valueOf(Collections.max(cls) + 1);
                    plugin.getConfig().set("data.clans." + id + ".players", pls);
                    plugin.getConfig().set("data.clans." + id + ".name", args[1].replaceAll("§", "&"));
                    plugin.getConfig().set("data.clans." + id + ".roles." + player.getUniqueId(), Role.OWNER.name());
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    player.sendMessage(Message.getMessage("clanCreated"));
                } else {
                    player.sendMessage(Message.getMessage("alreadyInClan"));
                    return true;
                }
            }
        } else {
            new MyClanGUI().open(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
