package qwq.arcane.module.impl.player;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.MultiBooleanValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/player/Stealer.class */
public class Stealer extends Module {
    private NumberValue delay;
    private BoolValue c0FPacketConfirmTransaction;
    private MultiBooleanValue container;
    public final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> setSlots;
    private final LinkedBlockingQueue<Packet<?>> packets;
    public static final TimerUtil timer = new TimerUtil();
    private int nextDelay;
    private boolean hasItems;

    public Stealer() {
        super("Stealer", Category.Player);
        this.delay = new NumberValue("Delay", 3.0d, 0.0d, 15.0d, 1.0d);
        this.c0FPacketConfirmTransaction = new BoolValue("C0FPacketConfirmTransaction", false);
        this.container = new MultiBooleanValue("Container", Arrays.asList(new BoolValue("Chest", true), new BoolValue("Furnace", true)));
        this.setSlots = new LinkedBlockingQueue<>();
        this.packets = new LinkedBlockingQueue<>();
        this.nextDelay = 0;
        this.hasItems = false;
    }

    @EventTarget
    public void onWorld(WorldLoadEvent e) {
        setState(false);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setsuffix(String.valueOf(this.delay.get()));
        if (((Scaffold) INSTANCE.getModuleManager().getModule(Scaffold.class)).getState()) {
            return;
        }
        if (KillAura.target != null) {
            return;
        }
        if ((mc.thePlayer.openContainer instanceof ContainerFurnace) && this.container.isEnabled("Furnace")) {
            ContainerFurnace furnace = (ContainerFurnace) mc.thePlayer.openContainer;
            this.hasItems = false;
            int i = 0;
            while (true) {
                if (i >= furnace.tileFurnace.getSizeInventory()) {
                    break;
                }
                if (furnace.tileFurnace.getStackInSlot(i) == null) {
                    i++;
                } else {
                    this.hasItems = true;
                    break;
                }
            }
            for (int i2 = 0; i2 < furnace.tileFurnace.getSizeInventory(); i2++) {
                if (furnace.tileFurnace.getStackInSlot(i2) != null && timer.delay(this.nextDelay)) {
                    mc.playerController.windowClick(furnace.windowId, i2, 0, 1, mc.thePlayer);
                    this.nextDelay = (int) (this.delay.get().floatValue() * 10.0f * MathHelper.getRandomDoubleInRange(0.75d, 1.25d));
                    timer.reset();
                }
            }
            if (!this.hasItems) {
                if (timer.delay(this.delay.get().floatValue())) {
                    mc.thePlayer.closeScreen();
                    return;
                }
                return;
            }
        }
        if ((mc.thePlayer.openContainer instanceof ContainerChest) && this.container.isEnabled("Chest")) {
            ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
            this.hasItems = false;
            int i3 = 0;
            while (true) {
                if (i3 >= container.getLowerChestInventory().getSizeInventory()) {
                    break;
                }
                if (container.getLowerChestInventory().getStackInSlot(i3) == null) {
                    i3++;
                } else {
                    this.hasItems = true;
                    break;
                }
            }
            for (int i4 = 0; i4 < container.getLowerChestInventory().getSizeInventory(); i4++) {
                if (container.getLowerChestInventory().getStackInSlot(i4) != null && timer.delay(this.nextDelay)) {
                    mc.playerController.windowClick(container.windowId, i4, 0, 1, mc.thePlayer);
                    this.nextDelay = (int) (this.delay.get().floatValue() * 10.0f * MathHelper.getRandomDoubleInRange(0.75d, 1.25d));
                    timer.reset();
                }
            }
            if (!this.hasItems && timer.delay(this.delay.get().floatValue())) {
                mc.thePlayer.closeScreen();
            }
        }
    }
}
