package com.amaya.firend;

import java.util.HashSet;
import java.util.Set;

/**
 * @author IDeal_Studio
 * @since 8/4/2024
 */
public class FriendManager {
    private static Set<String> friends = new HashSet<>();
    
    public static void addFriend(String username) {
        friends.add(username.toLowerCase());
    }
    
    public static void removeFriend(String username) {
        friends.remove(username.toLowerCase());
    }
    
    public static boolean isFriend(String username) {
        return friends.contains(username.toLowerCase());
    }
    
    public Set<String> getFriends() {
        return new HashSet<>(friends);
    }
}