package com.amaya.module.impl.misc;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.firend.FriendManager;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "MCF",category = Category.Misc)
public class MCF extends Module {
    private boolean wasDown;
    public MCF(){
        setState(true);
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (Minecraft.getMinecraft().inGameHasFocus) {
            boolean down = Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isKeyDown();

            if (down && !wasDown) {
                if (Minecraft.getMinecraft().objectMouseOver != null &&
                        Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityPlayer) {

                    EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().objectMouseOver.entityHit;
                    String name = StringUtils.stripControlCodes(player.getName());

                    if (FriendManager.isFriend(name)) {
                        FriendManager.removeFriend(name);
                    } else {
                        // 添加到好友列表
                        FriendManager.addFriend(name);
                    }
                }
                wasDown = true;
            } else if (!down) {
                wasDown = false;
            }
        }
    }
}
