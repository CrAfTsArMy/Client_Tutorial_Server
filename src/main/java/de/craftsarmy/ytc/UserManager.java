package de.craftsarmy.ytc;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserManager {

    private static final ConcurrentHashMap<InetAddress, User> users = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<User, ConcurrentLinkedQueue<String>> dataMap = new ConcurrentHashMap<>();

    public static void login(User user, InetAddress address) {
        if (users.containsKey(address))
            logout(users.get(address).name());
        users.put(address, user);
        boolean nameExists = false;
        for (User u : dataMap.keySet())
            if (u.name().trim().equalsIgnoreCase(user.name().trim())) {
                nameExists = true;
                break;
            }
        if (!dataMap.containsKey(user) && !nameExists)
            dataMap.put(user, new ConcurrentLinkedQueue<>());
    }

    public static void logout(String name, InetAddress address) {
        users.remove(address);
        logout(name);
    }

    private static void logout(String name) {
        for (User user : dataMap.keySet())
            if (user.name().trim().equalsIgnoreCase(name.trim()))
                dataMap.remove(user);
    }

    public static void update(User user, ConcurrentLinkedQueue<String> data) {
        dataMap.put(user, data);
    }

    public static ConcurrentLinkedQueue<String> get(User user) {
        return get(user.name());
    }

    public static ConcurrentLinkedQueue<String> get(String name) {
        for (User user : dataMap.keySet())
            if (user.name().trim().equalsIgnoreCase(name.trim()))
                return dataMap.get(user);
        return null;
    }

    public static User get(InetAddress address) {
        return users.get(address);
    }

}
