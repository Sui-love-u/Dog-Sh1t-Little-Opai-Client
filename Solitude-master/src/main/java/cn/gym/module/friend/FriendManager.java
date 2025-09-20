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

import joptsimple.internal.Strings;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class FriendManager {
    private final List<Friend> friends = new ArrayList<>();


    public void add(final String name) {
        this.add(name, name);
    }

    public void remove(final String name) {
        for (final Friend friend : this.friends) {
            if (friend.getUsername().equalsIgnoreCase(name)) {
                this.friends.remove(friend);
                break;
            }
        }
    }

    public void add(final String name, final String alias) {
        this.friends.add(new Friend(name, alias));
    }

    public String getFriendsName() {
        final ArrayList<String> list = new ArrayList<>();
        for (final Friend friend : this.friends) {
            list.add(friend.getUsername());
        }
        return Strings.join(list.toArray(new String[0]), "");
    }

    public boolean isFriend(final String name) {
        for (final Friend friend : this.friends) {
            if (friend.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void toggle(final String name) {
        for (final Friend friend : this.friends) {
            if (!this.isFriend(name)) {
                this.friends.add(friend);
                sendMessage(name + " friended");
            } else {
                this.friends.removeIf(friend::equals);
                sendMessage(name + " unfriended");
            }
        }
    }
    public static void sendMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Solitude" + EnumChatFormatting.WHITE + EnumChatFormatting.GRAY + ">> " + EnumChatFormatting.WHITE + message));
    }
    public boolean isFriend(final EntityPlayer player) {
        return this.isFriend(player.getGameProfile().getName());
    }
}
