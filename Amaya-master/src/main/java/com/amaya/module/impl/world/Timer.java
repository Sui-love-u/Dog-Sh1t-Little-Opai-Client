package com.amaya.module.impl.world;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.Rise.MoveUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "Timer", category = Category.World)
public class Timer extends Module {

    // 计时器模式设置
    public final ModeSetting mode = new ModeSetting("Mode", "Normal",
            new String[]{"Normal", "Random", "Pulse", "Legit"});

    // 基础速度设置
    public final NumberSetting speed = new NumberSetting("Speed",()->!mode.is("Random") && !mode.is("Pulse"), 1.5, 0.1, 5.0, 0.05);
    // 随机模式设置
    public final NumberSetting minSpeed = new NumberSetting("Min Speed",()-> mode.is("Random"),1.2, 0.1, 5.0, 0.05);

    public final NumberSetting maxSpeed = new NumberSetting("Max Speed",()->mode.is("Random"), 2.0, 0.1, 5.0, 0.05);

    // 脉冲模式设置
    public final NumberSetting pulseSpeed = new NumberSetting("Pulse Speed",()->mode.is("Pulse"), 2.0, 0.1, 5.0, 0.05);

    public final NumberSetting pulseInterval = new NumberSetting("Pulse Interval",()->mode.is("Pulse"), 20.0, 5.0, 100.0, 1.0);

    // 高级设置
    public final BooleanSetting bypass = new BooleanSetting("Bypass", true);
    public final BooleanSetting randomize = new BooleanSetting("Randomize", true);
    public final BooleanSetting motionOnly = new BooleanSetting("Motion Only", false);

    // 内部变量
    private double currentSpeed = 1.0;
    private int pulseTicks = 0;
    private int randomChangeTicks = 0;
    private final Random random = new Random();

    @Override
    public void onEnable() {
        currentSpeed = 1.0;
        pulseTicks = 0;
        randomChangeTicks = 0;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (motionOnly.get()) return;

        updateTimerSpeed();
        applyTimerSpeed();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
            setSuffix(mode.get());    if (!motionOnly.get()) return;

        updateTimerSpeed();

        // 只在运动事件中应用计时器
        if (event.isPre()) {
            applyTimerSpeed();
        } else {
            mc.timer.timerSpeed = 1.0f;
        }
    }

    private void updateTimerSpeed() {
        switch (mode.get()) {
            case "Normal":
                currentSpeed = getBypassSpeed(speed.get());
                break;

            case "Random":
                if (randomChangeTicks <= 0) {
                    currentSpeed = getBypassSpeed(MathHelper.getRandomDoubleInRange(
                            ThreadLocalRandom.current(), minSpeed.get(), maxSpeed.get()));
                    randomChangeTicks = ThreadLocalRandom.current().nextInt(10, 30);
                } else {
                    randomChangeTicks--;
                }
                break;

            case "Pulse":
                pulseTicks++;
                if (pulseTicks >= pulseInterval.get()) {
                    pulseTicks = 0;
                }
                currentSpeed = pulseTicks < (pulseInterval.get() / 2) ?
                        getBypassSpeed(pulseSpeed.get()) : 1.0;
                break;

            case "Legit":
                // 更隐蔽的计时器变化
                if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
                    currentSpeed = getBypassSpeed(1.1 + (MoveUtil.getSpeed() * 0.05));
                } else {
                    currentSpeed = 1.0;
                }
                break;
        }

        // 添加随机变化
        if (randomize.get() && currentSpeed != 1.0) {
            currentSpeed *= ThreadLocalRandom.current().nextDouble(0.98, 1.02);
            currentSpeed = MathHelper.clamp_double(currentSpeed, 0.1, 5.0);
        }
    }

    private void applyTimerSpeed() {
        // 应用计时器速度，带有随机偏移
        float timerSpeed = (float) currentSpeed;

        if (randomize.get()) {
            timerSpeed *= ThreadLocalRandom.current().nextFloat(0.98f, 1.02f);
        }

        mc.timer.timerSpeed = MathHelper.clamp_float(timerSpeed, 0.1f, 5.0f);
    }

    private double getBypassSpeed(double baseSpeed) {
        if (!bypass.get()) return baseSpeed;

        // Grimac 反作弊绕过技术
        double modifiedSpeed = baseSpeed;

        // 添加非线性变化
        if (modifiedSpeed > 1.5) {
            modifiedSpeed = 1.5 + (modifiedSpeed - 1.5) * 0.8;
        }

        // 添加随机抖动
        if (randomize.get()) {
            modifiedSpeed += (random.nextDouble() - 0.5) * 0.05;
        }

        // 限制最终速度
        return MathHelper.clamp_double(modifiedSpeed, 0.1, 3.5);
    }

    // 随机发送无害数据包以混淆检测
    private void sendDummyPackets() {
        if (ThreadLocalRandom.current().nextInt(0, 100) < 20) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.01,
                    mc.thePlayer.posZ,
                    false
            ));
        }
    }
}