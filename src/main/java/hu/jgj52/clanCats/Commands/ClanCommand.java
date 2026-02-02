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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.mustcall.qual.Owning;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static hu.jgj52.clanCats.ClanCats.plugin;

public class ClanCommand implements CommandExecutor, TabCompleter {
    private final Map<String, Function<Context, Boolean>> subcommands = new HashMap<>();
    private final Map<String, Function<Context, List<String>>> subcommandsub = new HashMap<>();
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
            } else {
                player.sendMessage(Message.getMessage("alreadyInClan"));
            }
            return true;
        });
        subcommands.put("disband", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            if (clan != null) {
                if (clan.getRole(player) == Role.OWNER) {
                    clan.disband();
                    player.sendMessage(Message.getMessage("disbanded"));
                } else {
                    player.sendMessage(Message.getMessage("noPerm"));
                }
            } else {
                player.sendMessage(Message.getMessage("notInClan"));
            }
            return true;
        });
        subcommands.put("invite", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            String[] args = context.args();
            if (clan != null) {
                if (clan.getPlayers().size() > plugin.getConfig().getInt("maxPlayersInClan")) {
                    player.sendMessage(Message.getMessage("clanFull"));
                    return true;
                }
                if (List.of(Role.OWNER, Role.ADMIN).contains(clan.getRole(player))) {
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
                } else {
                    player.sendMessage(Message.getMessage("noPerm"));
                }
            } else {
                player.sendMessage(Message.getMessage("notInClan"));
            }
            return true;
        });
        subcommands.put("accept", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            String[] args = context.args();
            if (clan != null) {
                player.sendMessage(Message.getMessage("alreadyInClan"));
                return true;
            }
            for (Clan c : invites.get(player)) {
                if (args[1].equals(c.getName())) {
                    invites.get(player).remove(c);
                    c.addPlayer(player);
                    for (Player p : c.getOnlinePlayers()) {
                        p.sendMessage(Replacer.value(Message.getMessage("joined"), player.getName()));
                    }
                    break;
                }
            }
            return true;
        });
        subcommands.put("promote", context -> {
           Player player = context.player();
           Clan clan = context.clan();
           String[] args = context.args();
           if (clan == null) {
               player.sendMessage(Message.getMessage("notInClan"));
               return true;
           }
           if (args.length < 2) {
               player.sendMessage(Message.getMessage("noArgs"));
               return true;
           }
           Player target = Bukkit.getPlayer(args[1]);
           if (target == null) {
               player.sendMessage(Replacer.value(Message.getMessage("noPlayer"), args[1]));
               return true;
           }
           if (clan.getRole(player) == Role.MEMBER) {
               player.sendMessage(Message.getMessage("noPerm"));
               return true;
           }
           if (clan.getRole(target) == Role.MEMBER && (clan.getRole(player) == Role.ADMIN || clan.getRole(player) == Role.OWNER)) {
               clan.setRole(target, Role.ADMIN);
               for (Player p : clan.getOnlinePlayers()) {
                   p.sendMessage(Replacer.value(Message.getMessage("promoted"), target.getName()));
               }
               return true;
           }
           return true;
        });
        subcommands.put("demote", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            String[] args = context.args();
            if (clan == null) {
                player.sendMessage(Message.getMessage("notInClan"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(Message.getMessage("noArgs"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Replacer.value(Message.getMessage("noPlayer"), args[1]));
                return true;
            }
            if (clan.getRole(player) != Role.OWNER) {
                player.sendMessage(Message.getMessage("noPerm"));
                return true;
            }
            if (clan.getRole(target) == Role.ADMIN && clan.getRole(player) == Role.OWNER) {
                clan.setRole(target, Role.MEMBER);
                for (Player p : clan.getOnlinePlayers()) {
                    p.sendMessage(Replacer.value(Message.getMessage("demoted"), target.getName()));
                }
                return true;
            }
            return true;
        });
        subcommands.put("transfer", context -> {
           Player player = context.player();
           Clan clan = context.clan();
           String[] args = context.args();
            if (clan == null) {
                player.sendMessage(Message.getMessage("notInClan"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(Message.getMessage("noArgs"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Replacer.value(Message.getMessage("noPlayer"), args[1]));
                return true;
            }
            if (clan.getRole(player) != Role.OWNER) {
                player.sendMessage(Message.getMessage("noPerm"));
                return true;
            }
            if (clan.getRole(target) != Role.OWNER) {
                clan.setRole(target, Role.OWNER);
                clan.setRole(player, Role.ADMIN);
                for (Player p : clan.getOnlinePlayers()) {
                    p.sendMessage(Replacer.value(Message.getMessage("transferred"), target.getName()));
                }
            }
            return true;
        });
        subcommands.put("kick", context -> {
            Player player = context.player();
            Clan clan = context.clan();
            String[] args = context.args();
            if (clan == null) {
                player.sendMessage(Message.getMessage("notInClan"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(Message.getMessage("noArgs"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Replacer.value(Message.getMessage("noPlayer"), args[1]));
                return true;
            }
            if ((clan.getRole(player) != Role.OWNER && (clan.getRole(target) == Role.ADMIN || clan.getRole(target) == Role.OWNER)) || (clan.getRole(player) == Role.MEMBER) || clan.getRole(player) == clan.getRole(target)) { // idk what im doing
                player.sendMessage(Message.getMessage("noPerm"));
                return true;
            }
            clan.removePlayer(target);
            for (Player p : clan.getOnlinePlayers()) {
                p.sendMessage(Replacer.value(Message.getMessage("kicked"), target.getName()));
            }
            return true;
        });

        subcommandsub.put("invite", ctx -> null);
        subcommandsub.put("accept", ctx -> {
            List<String> complete = new ArrayList<>();
            if (invites.containsKey(ctx.player())) {
                for (Clan c : invites.get(ctx.player())) {
                    complete.add(c.getName());
                }
            }
            return complete;
        });
        subcommandsub.put("promote", ctx -> {
           List<String> complete = new ArrayList<>();
           int value = switch (ctx.clan().getRole(ctx.player())) {
               case MEMBER -> 1;
               case ADMIN -> 2;
               case OWNER -> 3;
           };
            for (OfflinePlayer op : ctx.clan().getPlayers()) {
                int opValue = switch (ctx.clan().getRole(op)) {
                    case MEMBER -> 1;
                    case ADMIN -> 2;
                    case OWNER -> 3;
                };
                if (value > opValue && opValue != 2) {
                    complete.add(op.getName());
                }
            }
           return complete;
        });
        subcommandsub.put("demote", ctx -> {
           List<String> complete = new ArrayList<>();
           if (ctx.clan().getRole(ctx.player()) == Role.OWNER) {
               for (OfflinePlayer op : ctx.clan().getPlayers()) {
                   if (ctx.clan().getRole(op) == Role.ADMIN) {
                       complete.add(op.getName());
                   }
               }
           }
           return complete;
        });
        subcommandsub.put("transfer", ctx -> {
           List<String> complete = new ArrayList<>();
           for (OfflinePlayer player : ctx.clan().getPlayers()) {
               if (ctx.clan().getRole(ctx.player()) != Role.OWNER) {
                    complete.add(player.getName());
               }
           }
           return complete;
        });
        subcommandsub.put("kick", ctx -> {
           List<String> complete = new ArrayList<>();
            int value = switch (ctx.clan().getRole(ctx.player())) {
                case MEMBER -> 1;
                case ADMIN -> 2;
                case OWNER -> 3;
            };
            for (OfflinePlayer op : ctx.clan().getPlayers()) {
                int opValue = switch (ctx.clan().getRole(op)) {
                    case MEMBER -> 1;
                    case ADMIN -> 2;
                    case OWNER -> 3;
                };
                if (value > opValue) {
                    complete.add(op.getName());
                }
            }
           return complete;
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
        } else if (args.length == 2) {
            for (String subcommand : subcommands.keySet()) {
                if (args[0].equals(subcommand)) {
                    if (subcommandsub.containsKey(subcommand)) {
                        if (sender instanceof Player player) {
                            return subcommandsub.get(subcommand).apply(new Context(player, Clan.fromPlayer(player), args));
                        }
                    }
                    break;
                }
            }
        }
        return complete;
    }
}
