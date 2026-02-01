package hu.jgj52.clanCats.Utils;

import static hu.jgj52.clanCats.ClanCats.plugin;

public class Message {
    public static String getMessage(String msg) {
        return plugin.getConfig().getString("messages." + msg);
    }
}
