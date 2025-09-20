package com.amaya.module.impl.misc;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(name = "AntiBot", category = Category.Misc)
public class AntiBot extends Module {
    private static final BooleanSetting entityID = new BooleanSetting("EntityID", false);
    private static final BooleanSetting sleep = new BooleanSetting("Sleep", false);
    private static final BooleanSetting noArmor = new BooleanSetting("NoArmor", false);
    private static final BooleanSetting height = new BooleanSetting("Height", false);
    private static final BooleanSetting ground = new BooleanSetting("Ground", false);
    private static final BooleanSetting dead = new BooleanSetting("Dead", false);
    private static final BooleanSetting health = new BooleanSetting("Health", false);
    private static final BooleanSetting hytGetNames = new BooleanSetting("HytGetName", false);
    private final BooleanSetting tips = new BooleanSetting("Quick Macro GetNameTips", false);
    private static final ModeSetting hytGetNameModes =
            new ModeSetting("Quick Macro GetNameMode","4v4/1v1", new String[]{"4v4/1v1", "32", "16"});
    private static final List<Integer> groundBotList = new ArrayList<>();
    private static final List<String> playerName = new ArrayList<>();

    @EventTarget
    public void onWorld(WorldEvent event) {
        clearAll();
    }

    private void clearAll() {
        playerName.clear();
    }

    @EventTarget
    public void onPacketReceive(PacketReceiveEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S14PacketEntity && ground.getValue()) {
            Entity entity = ((S14PacketEntity) event.getPacket()).getEntity(mc.theWorld);

            if (entity instanceof EntityPlayer) {
                if (((S14PacketEntity) event.getPacket()).onGround
                        && !groundBotList.contains(entity.getEntityId())) {
                    groundBotList.add(entity.getEntityId());
                }
            }
        }
        if (hytGetNames.getValue() && packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat) packet;
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("获得胜利!") || s02PacketChat.getChatComponent().getUnformattedText().contains("游戏开始 ...")) {
                clearAll();
            }
            switch (hytGetNameModes.getValue()) {
                case "4v4/1v1":
                case "32": {
                    Matcher matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?) 死了!)))").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if (matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            if (tips.getValue())
                                log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000);
                                    playerName.remove(name);
                                    if (tips.getValue())
                                        log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    if (matcher2.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) {
                        String name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            if (tips.getValue())
                                log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000);
                                    playerName.remove(name);
                                    if (tips.getValue())
                                        log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;
                }
                case "16": {
                    Matcher matcher = Pattern.compile("击败了 (.*?)!").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if (matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            if (tips.getValue())
                                log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    playerName.remove(name);
                                    if (tips.getValue())
                                        log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    if (matcher2.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) {
                        String name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    playerName.remove(name);
                                    log("§8[§c§l" + Amaya.Instance.getName() + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;

                }
            }
        }
    }

    public static boolean isServerBot(Entity entity) {
        if (Objects.requireNonNull(Amaya.Instance.moduleManager.getModule(AntiBot.class)).getState()) {
            if (entity instanceof EntityPlayer) {
                if (hytGetNames.getValue() && playerName.contains(entity.getName())) {
                    return true;
                }
                if (height.getValue() && (entity.height <= 0.5 || ((EntityPlayer) entity).isPlayerSleeping() || entity.ticksExisted < 80)) {
                    return true;
                }
                if (dead.getValue() && entity.isDead) {
                    return true;
                }
                if (health.getValue() && ((EntityPlayer) entity).getHealth() == 0.0F) {
                    return true;
                }
                if (sleep.getValue() && ((EntityPlayer) entity).isPlayerSleeping()) {
                    return true;
                }
                if (entityID.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) {
                    return true;
                }
                if (ground.getValue() && !groundBotList.contains(entity.getEntityId())) {
                    return true;
                }
                return noArmor.getValue() && (((EntityPlayer) entity).inventory.armorInventory[0] == null
                        && ((EntityPlayer) entity).inventory.armorInventory[1] == null
                        && ((EntityPlayer) entity).inventory.armorInventory[2] == null
                        && ((EntityPlayer) entity).inventory.armorInventory[3] == null);
            }
        }
        return false;
    }
    public static void log(Object message) {
        String text = String.valueOf("§f[§bX§f]§7 " + message);
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }
}