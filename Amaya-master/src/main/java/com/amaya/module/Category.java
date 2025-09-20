package com.amaya.module;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public enum Category {
    Combat("G"),
    Player("M"),
    Movement("H"),
    Misc("I"),
    World("J"),
    Render("K"),
    Display("L"),
    Search("S");
    public String icon;
    Category(String icon){
        this.icon = icon;
    }
}
