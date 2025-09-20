package qwq.arcane.module.impl.combat;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.AttackEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/Wtap.class */
public class Wtap extends Module {
    private int ticks;
    private final TimerUtil wtapTimer;
    private final ModeValue wtapMode;

    public Wtap() {
        super("Wtap", Category.Combat);
        this.wtapTimer = new TimerUtil();
        this.wtapMode = new ModeValue("Wtap", "Wtap", new String[]{"Wtap", "Stap", "Shift tap", "Packet", "Legit"});
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (this.wtapTimer.hasReached(500.0d)) {
            this.wtapTimer.reset();
            this.ticks = 2;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setsuffix(this.wtapMode.get());
        switch (this.ticks) {
            case 1:
                switch (this.wtapMode.getValue()) {
                    case "Wtap":
                        mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                        break;
                    case "Stap":
                        mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                        mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
                        break;
                    case "Shift tap":
                        mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
                        break;
                    case "Packet":
                        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                        break;
                    case "Legit":
                        mc.thePlayer.setSprinting(true);
                        break;
                }
                this.ticks--;
                break;
            case 2:
                switch (this.wtapMode.getValue()) {
                    case "Wtap":
                        mc.gameSettings.keyBindForward.pressed = false;
                        break;
                    case "Stap":
                        mc.gameSettings.keyBindForward.pressed = false;
                        mc.gameSettings.keyBindBack.pressed = true;
                        break;
                    case "Shift tap":
                        mc.gameSettings.keyBindSneak.pressed = true;
                        break;
                    case "Packet":
                        PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        break;
                    case "Legit":
                        mc.thePlayer.setSprinting(false);
                        break;
                }
                this.ticks--;
                break;
        }
    }
}
