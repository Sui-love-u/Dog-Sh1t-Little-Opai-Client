package com.amaya.module.impl.world;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.MoveInputEvent;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.player.Stuck;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.player.InventoryUtil;
import com.amaya.utils.player.ProjectileUtil;
import com.amaya.utils.player.Rise.PlayerUtil;
import com.amaya.utils.time.TimerUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import java.awt.*;

@ModuleInfo(name = "AutoPearl", category = Category.World)
public class AutoPearl
extends Module {
    private final BooleanSetting debugValue = new BooleanSetting("Debug", false);
    private static final double T = 10.0;
    private static final double T_MIN = 1.0E-4;
    private static final double ALPHA = 0.997;
    private CalculateThread calculateThread;
    private final TimerUtil timer = new TimerUtil();
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;

    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        if (this.calculating) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        if (!this.debugValue.getValue().booleanValue()) {
            return;
        }
        FontManager.REGULAR.get(12).drawString("assessment: " + new ProjectileUtil.EnderPearlPredictor(AutoPearl.mc.thePlayer.posX, AutoPearl.mc.thePlayer.posY, AutoPearl.mc.thePlayer.posZ, AutoPearl.mc.thePlayer.motionY - 0.01, AutoPearl.mc.thePlayer.motionY + 0.02).assessRotation(new Vector2f(AutoPearl.mc.thePlayer.rotationYaw, AutoPearl.mc.thePlayer.rotationPitch)), 20.0f, 20.0f, Color.WHITE.getRGB());
        FontManager.REGULAR.get(12).drawString("(" + AutoPearl.mc.thePlayer.rotationYaw + ", " + AutoPearl.mc.thePlayer.rotationPitch + ")", 20.0f, 30.0f, Color.WHITE.getRGB());
    }

    @EventTarget
    public void onMotion(MotionEvent event) throws InterruptedException {
        boolean overVoid;
        if (AutoPearl.mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
        }
        if (event.isPost() && this.calculating && (this.calculateThread == null || this.calculateThread.completed)) {
            this.calculating = false;
            Stuck.throwPearl(this.calculateThread.solution);
        }
        boolean bl = overVoid = !AutoPearl.mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30.0, true);
        if (!this.attempted && !AutoPearl.mc.thePlayer.onGround && overVoid && Amaya.Instance.fallDistanceManager.distance > 2.0f) {
            Amaya.Instance.fallDistanceManager.distance = 0.0f;
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                ItemStack stack = AutoPearl.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack == null || !(stack.getItem() instanceof ItemEnderPearl) || slot < 36) continue;
                this.bestPearlSlot = slot;
                if (this.debugValue.getValue().booleanValue()) {
                }
                if (this.bestPearlSlot - 36 == -37) continue;
                AutoPearl.mc.thePlayer.inventory.currentItem = this.bestPearlSlot - 36;
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            if (!(AutoPearl.mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                return;
            }
            this.calculating = true;
            this.calculateThread = new CalculateThread(AutoPearl.mc.thePlayer.posX, AutoPearl.mc.thePlayer.posY, AutoPearl.mc.thePlayer.posZ, 0.0, 0.0);
            this.calculateThread.start();
            if (Amaya.Instance.moduleManager.getModule(Scaffold.class).getState()) Amaya.Instance.moduleManager.getModule(Scaffold.class).setState(false);
            Amaya.Instance.moduleManager.getModule(Stuck.class).setState(true);
        }
    }

    private void putItemInSlot(int slot, int slotIn) {
        InventoryUtil.windowClick(mc, slotIn, slot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private class CalculateThread
    extends Thread {
        private int iteration;
        private boolean completed;
        private double temperature;
        private double energy;
        private double solutionE;
        private Vector2f solution;
        public boolean stop;
        private final ProjectileUtil.EnderPearlPredictor predictor;

        private CalculateThread(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            this.predictor = new ProjectileUtil.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
            this.completed = false;
        }

        @Override
        public void run() {
            TimerUtil timer = new TimerUtil();
            timer.reset();
            Vector2f current = this.solution = new Vector2f((float) MathUtils.getRandomInRange(-180, 180), (float)MathUtils.getRandomInRange(-90, 90));
            this.solutionE = this.energy = this.predictor.assessRotation(this.solution);
            while (this.temperature >= 1.0E-4 && !this.stop) {
                double assessment;
                double deltaE;
                Vector2f rotation = new Vector2f((float)((double)current.x + MathUtils.getRandomInRange(-this.temperature * 18.0, this.temperature * 18.0)), (float)((double)current.y + MathUtils.getRandomInRange(-this.temperature * 9.0, this.temperature * 9.0)));
                if (rotation.y > 90.0f) {
                    rotation.y = 90.0f;
                }
                if (rotation.y < -90.0f) {
                    rotation.y = -90.0f;
                }
                if ((deltaE = (assessment = this.predictor.assessRotation(rotation)) - this.energy) >= 0.0 || (double)MathUtils.getRandomInRange(0, 1) < Math.exp(-deltaE / this.temperature * 100.0)) {
                    this.energy = assessment;
                    current = rotation;
                    if (assessment > this.solutionE) {
                        this.solutionE = assessment;
                        this.solution = new Vector2f(rotation.x, rotation.y);
                    }
                }
                this.temperature *= 0.997;
                ++this.iteration;
            }
            this.completed = true;
        }
    }
}

