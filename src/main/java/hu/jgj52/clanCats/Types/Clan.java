package hu.jgj52.clanCats.Types;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static hu.jgj52.clanCats.ClanCats.plugin;

public class Clan {
    private static Map<String, Clan> clans = new HashMap<>();
    public static Clan of(String id) {
        if (clans.containsKey(id)) {
            return clans.get(id);
        }

        Clan clan = new Clan(id);
        clans.put(id, clan);
        return clan;
    }
    @Nullable
    public static Clan fromPlayer(Player player) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.clans");
        if (section == null) return null;
        for (String key : section.getKeys(false)) {
            List<String> players = plugin.getConfig().getStringList("data.clans." + key + ".players");
            for (String uuid : players) {
                if (Objects.equals(UUID.fromString(uuid), player.getUniqueId())) {
                    return of(key);
                }
            }
        }
        return null;
    }
    private static void save() {
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    private final List<UUID> players = new ArrayList<>();
    private String name;
    private final String id;
    private final Map<UUID, Role> roles = new HashMap<>();
    private Clan (String id) {
        this.id = id;
        List<String> players = plugin.getConfig().getStringList("data.clans." + id + ".players");
        for (String player : players) {
            this.players.add(UUID.fromString(player));
            roles.put(UUID.fromString(player), Role.valueOf(plugin.getConfig().getString("data.clans." + id + ".roles." + player)));
        }
        this.name = plugin.getConfig().getString("data.clans." + id + ".name");
    }

    public String getName() {
        return name;
    }

    public List<OfflinePlayer> getPlayers() {
        List<OfflinePlayer> players = new ArrayList<>();
        for (UUID uuid : this.players) {
            players.add(Bukkit.getOfflinePlayer(uuid));
        }
        return players;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (OfflinePlayer op : getPlayers()) {
            if (op.getPlayer() != null) {
                players.add(op.getPlayer());
            }
        }
        return players;
    }

    public void setName(String name) {
        plugin.getConfig().set("data.clans." + id + ".name", name);
        save();
        this.name = name;
    }

    public void addPlayer(OfflinePlayer player) {
        List<String> players = plugin.getConfig().getStringList("data.clans." + id + ".players");
        players.add(player.getUniqueId().toString());
        plugin.getConfig().set("data.clans." + id + ".players", players);
        setRole(player, Role.MEMBER);
        save();
        this.players.add(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        addPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public void removePlayer(OfflinePlayer player) {
        List<String> players = plugin.getConfig().getStringList("data.clans." + id + ".players");
        players.remove(player.getUniqueId().toString());
        plugin.getConfig().set("data.clans." + id + ".players", players);
        save();
        this.players.remove(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        removePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public Role getRole(OfflinePlayer player) {
        return roles.get(player.getUniqueId());
    }

    public Role getRole(Player player) {
        return getRole(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public void setRole(OfflinePlayer player, Role role) {
        plugin.getConfig().set("data.clans." + id + ".roles." + player.getUniqueId(), role.name());
        save();
        roles.put(player.getUniqueId(), role);
    }

    public void setRole(Player player, Role role) {
        setRole(Bukkit.getOfflinePlayer(player.getUniqueId()), role);
    }

    public void disband() {
        plugin.getConfig().set("data.clans." + id, null);
        save();
    }
}
