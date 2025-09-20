package qwq.arcane.module.impl.misc;

import java.util.regex.Pattern;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.ModuleManager;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.player.InvManager;
import qwq.arcane.module.impl.player.Stealer;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/misc/AutoPlay.class */
public class AutoPlay extends Module {
    private final BoolValue toggleModule;
    public boolean display;
    private static final Pattern PATTERN_BEHAVIOR_EXCEPTION = Pattern.compile("玩家(.*?)在本局游戏中行为异常");
    private static final Pattern PATTERN_WIN_MESSAGE = Pattern.compile("你在地图(.*?)中赢得了(.*?)");
    private static final String TEXT_LIKE_OPTIONS = "      喜欢      一般      不喜欢";
    private static final String TEXT_BEDWARS_GAME_END = "[起床战争] Game 结束！感谢您的参与！";
    private static final String TEXT_COUNTDOWN = "开始倒计时: 1 秒";

    public AutoPlay() {
        super("AutoPlay", Category.Misc);
        this.toggleModule = new BoolValue("Toggle Module", true);
        this.display = false;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        ItemStack itemStack;
        if (!event.isPost() && (itemStack = mc.thePlayer.inventoryContainer.getSlot(44).getStack()) != null && itemStack.getDisplayName() != null && !itemStack.getDisplayName().contains("退出观战")) {
        }
    }

    @EventTarget
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            String text = ((S02PacketChat) packet).getChatComponent().getUnformattedText();
            if (!PATTERN_BEHAVIOR_EXCEPTION.matcher(text).find()) {
                if (PATTERN_WIN_MESSAGE.matcher(text).find() || (mc.thePlayer.isSpectator() && this.toggleModule.getValue().booleanValue())) {
                    toggleOffensiveModules(false);
                } else if (!text.contains(TEXT_LIKE_OPTIONS) && !text.contains(TEXT_BEDWARS_GAME_END) && text.contains(TEXT_COUNTDOWN)) {
                    checkAndTogglePlayerTracker();
                }
            }
        }
    }

    private void toggleOffensiveModules(boolean state) {
        ModuleManager moduleManager = Client.Instance.getModuleManager();
        ((InvManager) moduleManager.getModule(InvManager.class)).setState(state);
        ((Stealer) moduleManager.getModule(Stealer.class)).setState(state);
        ((KillAura) moduleManager.getModule(KillAura.class)).setState(state);
    }

    private void checkAndTogglePlayerTracker() {
        if (this.toggleModule.getValue().booleanValue()) {
            toggleOffensiveModules(true);
        }
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
}
