package hu.jgj52.clanCats.Utils;

public class Replacer {
    public static String value(String msg, String value) {
        return msg.replaceAll("%value%", value);
    }
}
