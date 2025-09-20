package qwq.arcane.module.impl.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.ChatComponentText;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.module.ModuleManager;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/AntiBot.class */
public class AntiBot extends Module {
    static List<Integer> bots = new ArrayList();
    static ModeValue mode = new ModeValue("Mode", "Hypixel", new String[]{"Hypixel", "Mineland"});
    static BoolValue AntiStaff = new BoolValue("AntiStaff", true);
    private static final List<Entity> invalid = new ArrayList();

    public static List<Entity> getInvalid() {
        return invalid;
    }

    public AntiBot() {
        super("AntiBot", Category.Combat);
    }

    public static boolean isBot(Entity entity) {
        if (((AntiBot) Client.Instance.getModuleManager().getModule(AntiBot.class)).getState() && mode.getValue().equals("Hypixel")) {
            if (entity.getDisplayName().getFormattedText().startsWith("§") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) {
                return false;
            }
            return true;
        }
        if (((AntiBot) Client.Instance.getModuleManager().getModule(AntiBot.class)).getState() && mode.getValue().equals("Mineland") && bots.contains(Integer.valueOf(entity.getEntityId()))) {
            return true;
        }
        return false;
    }

    @EventTarget
    public void pack(PacketReceiveEvent eventPacket) {
        if ((eventPacket.getPacket() instanceof S0CPacketSpawnPlayer) && KillAura.target != null && ((KillAura) Client.Instance.getModuleManager().getModule(KillAura.class)).getState()) {
            tellPlayer("Add");
            bots.add(Integer.valueOf(((S0CPacketSpawnPlayer) eventPacket.getPacket()).entityId));
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent eventPacket) {
        setsuffix(mode.getValue().toString());
        if (AntiStaff.get().booleanValue() && (eventPacket.getPacket() instanceof S0CPacketSpawnPlayer)) {
            S0CPacketSpawnPlayer var19 = (S0CPacketSpawnPlayer) eventPacket.getPacket();
            EntityPlayer var20 = (EntityPlayer) mc.theWorld.removeEntityFromWorld(var19.getEntityID());
            double var5 = var19.getX() / 32.0d;
            double var7 = var19.getY() / 32.0d;
            double var9 = var19.getZ() / 32.0d;
            double var11 = mc.thePlayer.posX - var5;
            double var13 = mc.thePlayer.posY - var7;
            double var15 = mc.thePlayer.posZ - var9;
            double var17 = Math.sqrt((var11 * var11) + (var13 * var13) + (var15 * var15));
            if (mc.theWorld.playerEntities.contains(var20) && var17 <= 17.0d && !var20.equals(mc.thePlayer) && var5 != mc.thePlayer.posX && var7 != mc.thePlayer.posY && var9 != mc.thePlayer.posZ) {
                mc.theWorld.removeEntity(var20);
                tellPlayer("[AntiBot]Staff Might Be Checking You!");
                ModuleManager moduleManager = new ModuleManager();
                ((KillAura) moduleManager.getModule(KillAura.class)).setState(false);
            }
        }
    }

    @EventTarget
    public void onSuffix(UpdateEvent event) {
        setsuffix(mode.get());
    }

    public static void tellPlayer(String message) {
        Mine.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§a[Distance] §r" + message));
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        bots.clear();
        invalid.clear();
    }

    @EventTarget
    public void onReload(WorldLoadEvent e) {
        bots.clear();
    }
}
