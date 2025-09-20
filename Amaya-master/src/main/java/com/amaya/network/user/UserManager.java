package com.amaya.network.user;

import com.amaya.firend.FriendManager;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author IDeal_Studio
 * @since 8/4/2024
 */
@Getter
@Setter
public class UserManager {
    private User user = new User("Unknown", null, null, Date.from(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), "");
    private Map<String, User> inGameUsers = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private FriendManager friendManager = new FriendManager();

    public FriendManager getFriendManager() {
        return friendManager;
    }
    private String token;

    public void addUser(String username, User user) {
        users.put(username, user);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void setUserAvatar(String username, String avatarData) {
        User user = getUser(username);
        if (user != null) {
            user.setAvatarData(avatarData);
        } else {
            user = new User(username, null, avatarData);
            addUser(username, user);
        }
    }

//    public static void setUserRank(String username, String rank) {
////        if (Client.USER_NAME.equals("IDeal")){
////            user.setRank("dev");
////        }else if (!(Client.USER_NAME.equals("IDeal"))){
////            user.setRank("user");
////        }
//        if (!username.equals("IDeal")) {
//            setUserRank(username, rank);
//        }
//        if (username.equals("IDeal")) {
//            setUserRank("IDeal", "Dev");
//        }
//    }
    public void updateUserRank(String username, String rank) {
        User user = getUser(username);
        if (user != null) {
            user.setRank(rank);
        }
    }

    public void addInGameUser(String inGameName, User user) {
        inGameUsers.put(inGameName, user);
    }

    public User getInGameUser(String inGameName) {
        return inGameUsers.get(inGameName);
    }

    public void update(String rank) {
        user = new User(user.getUsername(), null, rank, null, "");
    }
}
