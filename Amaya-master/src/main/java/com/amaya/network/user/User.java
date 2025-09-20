package com.amaya.network.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author IDeal_Studio
 * @since 8/4/2024
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    String username;
    String password;
    String rank;
    Date expiryDate;
    String avatarData;

    public User(String username, String rank, String avatarData) {
        this.username = username;
        this.rank = rank;
        this.avatarData = avatarData;
    }
}