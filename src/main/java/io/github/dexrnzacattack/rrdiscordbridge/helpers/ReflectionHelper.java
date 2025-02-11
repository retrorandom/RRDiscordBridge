package io.github.dexrnzacattack.rrdiscordbridge.helpers;

public class ReflectionHelper {
    public static boolean isMotdSupported = doesMethodExist("org.bukkit.Server", "getMotd");

    public static boolean isServerNameSupported = doesMethodExist("org.bukkit.Server", "getServerName");

    public static boolean isServerIconSupported = doesMethodExist("org.bukkit.Server", "getServerIcon");

    public static boolean isServerOperatorsSupported = doesMethodExist("org.bukkit.Server", "getOperators");

    /** Does a method exist? I take strings here to let this method do all the shit. */
    public static boolean doesMethodExist(String clazz, String method) {
        try {
            Class<?> clazzz = Class.forName(clazz);
            clazzz.getMethod(method);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    /** Does a class exist? I take strings here to let this method do all the creation. */
    public static boolean doesClassExist(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
