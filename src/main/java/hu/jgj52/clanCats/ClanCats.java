package hu.jgj52.clanCats;

import hu.jgj52.clanCats.Commands.ClanCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClanCats extends JavaPlugin {

    public static ClanCats plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("clan").setExecutor(new ClanCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
