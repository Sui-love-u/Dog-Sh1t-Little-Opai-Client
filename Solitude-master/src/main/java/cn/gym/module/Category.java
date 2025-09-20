package cn.gym.module;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 13:50
 */
public enum Category {
    Combat("K"),
    Player("N"),
    Movement("O"),
    Misc("Q"),
    Render("P"),
    Display("M");
    public String icon;
    Category(String icon){
        this.icon = icon;
    }
}
