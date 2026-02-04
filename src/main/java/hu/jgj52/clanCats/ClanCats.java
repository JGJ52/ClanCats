package hu.jgj52.clanCats;

import hu.jgj52.clanCats.Commands.ClanCommand;
import hu.jgj52.clanCats.Listener.ClickListener;
import hu.jgj52.clanCats.Listener.PlayerDamageListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClanCats extends JavaPlugin {

    public static ClanCats plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        if (!(getConfig().getInt("maxPlayersInClan") >= 2 && getConfig().getInt("maxPlayersInClan") <= 28)) {
            getLogger().info("§cIn config, maxPlayersInClan has to be a number from 2 to 28");
            getServer().getPluginManager().disablePlugin(this);
        }

        getCommand("clan").setExecutor(new ClanCommand());

        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
