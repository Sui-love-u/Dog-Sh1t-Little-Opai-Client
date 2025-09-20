/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.module.friend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Friend {
    private final String username;
    private String alias;

    public Friend(final String username) {
        this(username, username);
    }

    public Friend(final String alias, final String username) {
        this.alias = alias;
        this.username = username;
    }
}
