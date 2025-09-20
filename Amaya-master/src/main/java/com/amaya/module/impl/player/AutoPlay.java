package com.amaya.module.impl.player;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "AutoPlay", category = Category.Player)
public class AutoPlay extends Module {

    boolean strA = false;
    boolean strB = false;
    boolean safe = false;
    public static int sb = 0;

    @EventTarget
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        Object packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat) packet;
            String text = s02PacketChat.getChatComponent().getUnformattedText();


            if (text.contains("开始倒计时: 1 秒")) {
                Amaya.Instance.moduleManager.getModule(ChestStealer.class).setState(true);
                Amaya.Instance.moduleManager.getModule(InvManager.class).setState(true);
                Amaya.Instance.moduleManager.getModule(KillAura.class).setState(true);

            }

            if (text.contains("你现在是观察者状态. 按E打开菜单.")) {
                safe = true;
                strA = true;
                Amaya.Instance.moduleManager.getModule(ChestStealer.class).setState(false);
                Amaya.Instance.moduleManager.getModule(InvManager.class).setState(false);
                Amaya.Instance.moduleManager.getModule(KillAura.class).setState(false);
            }


            if (packet instanceof S45PacketTitle) {
                S45PacketTitle s45PacketTitle = (S45PacketTitle) packet;
                if (s45PacketTitle.getType() == S45PacketTitle.Type.TITLE) {
                    String title = s45PacketTitle.getMessage().getFormattedText();
                    Matcher matcher5 = Pattern.compile("花雨庭").matcher(title);
                    Matcher matcher6 = Pattern.compile("VICTORY").matcher(title);
                    if (matcher5.find()) {
                        safe = false;
                        strA = false;
                        strB = false;
                        Amaya.Instance.moduleManager.getModule(ChestStealer.class).setState(false);
                        Amaya.Instance.moduleManager.getModule(InvManager.class).setState(false);
                        Amaya.Instance.moduleManager.getModule(KillAura.class).setState(false);
                    }
                    if (matcher6.find()) {
                        Amaya.Instance.moduleManager.getModule(ChestStealer.class).setState(false);
                        Amaya.Instance.moduleManager.getModule(InvManager.class).setState(false);
                        Amaya.Instance.moduleManager.getModule(KillAura.class).setState(false);
                        sb ++;
                    }
                    if (title.equals("VICTORY")) {
                        sb ++;
                        safe = true;
                        strB = true;
                    }
                    if (safe && strA && !title.equals("VICTORY")) {
                        sb ++;
                        strA = false;
                        strB = false;
                    }
                }
            }

        }
    }
}
