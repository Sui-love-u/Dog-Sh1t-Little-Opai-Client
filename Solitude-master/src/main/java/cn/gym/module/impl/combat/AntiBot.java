package cn.gym.module.impl.combat;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.events.impl.packet.PacketReceiveEvent;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.ModuleManager;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:55
 */
public class AntiBot extends Module {
    static List<Integer> bots = new ArrayList<>();
    static ModeValue mode = new ModeValue("Mode","Hypixel",new String[]{"Hypixel","Mineland"});
    static BooleanValue AntiStaff = new BooleanValue("AntiStaff", true);
    private static final List<Entity> invalid = new ArrayList<>();
    public static List<Entity> getInvalid() {
        return invalid;
    }

    public AntiBot() {
        super("AntiBot", Category.Combat);
    }

    public static boolean isBot(Entity entity) {
        if (Solitude.Instance.getModuleManager().getModule(AntiBot.class).getState() && mode.getValue().equals("Hypixel")) {
            if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) {
                return false;
            }
            return true;
        }
        if (Solitude.Instance.getModuleManager().getModule(AntiBot.class).getState() && mode.getValue().equals("Mineland")) {
            if (bots.contains(entity.getEntityId())) {
                return true;
            }
        }
        return false;
    }

    @EventTarget
    public void pack(PacketReceiveEvent eventPacket) {
        if (eventPacket.getPacket() instanceof S0CPacketSpawnPlayer) {
            if (KillAura.target != null && Solitude.Instance.getModuleManager().getModule(KillAura.class).getState()) {
                tellPlayer("Add");
                bots.add(((S0CPacketSpawnPlayer) eventPacket.getPacket()).entityId);
            }
        }
    }
    @EventTarget
    public void onPacket(PacketSendEvent eventPacket) {
        setSuffix(mode.getValue().toString());
        if (AntiStaff.get()) {
            if (eventPacket.getPacket() instanceof S0CPacketSpawnPlayer) {
                S0CPacketSpawnPlayer var19 = (S0CPacketSpawnPlayer) eventPacket.getPacket();

                EntityPlayer var20 = (EntityPlayer) this.mc.theWorld.removeEntityFromWorld(var19.getEntityID());
                double var5 = (double) var19.getX() / 32.0D;
                double var7 = (double) var19.getY() / 32.0D;
                double var9 = (double) var19.getZ() / 32.0D;
                double var11 = this.mc.thePlayer.posX - var5;
                double var13 = this.mc.thePlayer.posY - var7;
                double var15 = this.mc.thePlayer.posZ - var9;
                double var17 = Math.sqrt(var11 * var11 + var13 * var13 + var15 * var15);
                if (this.mc.theWorld.playerEntities.contains(var20) && var17 <= 17.0D && !var20.equals(this.mc.thePlayer) && var5 != this.mc.thePlayer.posX && var7 != this.mc.thePlayer.posY && var9 != this.mc.thePlayer.posZ) {
                    this.mc.theWorld.removeEntity(var20);
//                    Notifications.getManager( ).post( "AntiBot", "Staff might be checking you!(detected sus bot)" );
                    tellPlayer("[AntiBot]Staff Might Be Checking You!");
                    ModuleManager moduleManager = new ModuleManager();
                    moduleManager.getModule(KillAura.class).setState(false);
                }
            }

        }
    }
    public static void tellPlayer(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("\247a[Distance] \247r" + message));
    }

    @Override
    public void onDisable() {
        bots.clear();
        invalid.clear();
    }

    @EventTarget
    public void onReload(WorldEvent e) {
        bots.clear();
    }
}
