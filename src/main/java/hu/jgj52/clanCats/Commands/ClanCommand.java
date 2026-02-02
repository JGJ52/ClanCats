package hu.jgj52.clanCats.Commands;

import hu.jgj52.clanCats.GUIs.MyClanGUI;
import hu.jgj52.clanCats.Types.Clan;
import hu.jgj52.clanCats.Types.Role;
import hu.jgj52.clanCats.Utils.Message;
import hu.jgj52.clanCats.Utils.Replacer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static hu.jgj52.clanCats.ClanCats.plugin;

public class ClanCommand implements CommandExecutor, TabCompleter {
    private final Map<String, Function<Context, Boolean>> subcommands = new HashMap<>();
    private final Map<Player, Set<Clan>> invites = new HashMap<>();

    private record Context(Player player, Clan clan, String[] args) {}

    public ClanCommand() {
        subcommands.put("create", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            String[] args = context.args();
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
                return true;
            } else {
                player.sendMessage(Message.getMessage("alreadyInClan"));
                return true;
            }
        });
        subcommands.put("disband", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            if (clan != null) {
                clan.disband();
                player.sendMessage(Message.getMessage("disbanded"));
                return true;
            } else {
                player.sendMessage(Message.getMessage("notInClan"));
                return true;
            }
        });
        subcommands.put("invite", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            String[] args = context.args();
            if (clan != null) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(Replacer.value(Message.getMessage("noPlayer"), args[1]));
                    return true;
                }
                if (invites.containsKey(target)) {
                    invites.get(target).add(clan);
                } else {
                    invites.put(target, new HashSet<>(Set.of(clan)));
                }
                player.sendMessage(Message.getMessage("inviteSent"));
                Component acceptButton = Component.text(Message.getMessage("acceptInviteButton"))
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/clan accept " + clan.getName()));
                target.sendMessage(MiniMessage.miniMessage().deserialize(Replacer.value(Message.getMessage("gotInvite"), clan.getName()), Placeholder.component("accept", acceptButton)));
                return true;
            } else {
                player.sendMessage(Message.getMessage("notInClan"));
                return true;
            }
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.getMessage("notPlayer"));
            return true;
        }

        Clan clan = Clan.fromPlayer(player);

        if (args.length > 0) {
            for (String subcommand : subcommands.keySet()) {
                if (subcommand.equals(args[0])) {
                    if (sender.hasPermission("clancats.command.clan." + subcommand)) {
                        return subcommands.get(subcommand).apply(new Context(player, clan, args));
                    } else {
                        player.sendMessage(Message.getMessage("noPerm"));
                        return true;
                    }
                }
            }
        } else {
            new MyClanGUI().open(player);
            return true;
        }
        player.sendMessage(Message.getMessage("scNotFound"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            for (String subcommand : subcommands.keySet()) {
                if (sender.hasPermission("clancats.command.clan." + subcommand)) {
                    complete.add(subcommand);
                }
            }
        }
        return complete;
    }
}
