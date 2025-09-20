package cn.gym.module.impl.render;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.AttackEvent;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.render.sound.SoundUtil;
import cn.gym.utils.animations.impl.ContinualAnimation;
import cn.gym.value.impl.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public final class KillEffect extends Module {
    public KillEffect() {
        super("KillEffect", Category.Render);
    }

    private EntitySquid squid;
    private double percent = 0.0;
    private final ContinualAnimation anim = new ContinualAnimation();

    private final BooleanValue lightning = new BooleanValue("Lightning", true);

    private final BooleanValue explosion = new BooleanValue("Explosion", true);
    private final BooleanValue squidValue = new BooleanValue("Squid", true);
    private final BooleanValue bloodValue = new BooleanValue("Blood", true);
    private final BooleanValue soundEffect = new BooleanValue("Sound Effect", true);

    private EntityLivingBase target;

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (squidValue.getValue()) {
            if (squid != null) {
                if (mc.theWorld.loadedEntityList.contains(squid)) {
                    if (percent < 1) percent += Math.random() * 0.048;
                    if (percent >= 1) {
                        percent = 0.0;
                        for (int i = 0; i <= 8; i++) {
                            mc.effectRenderer.emitParticleAtEntity(squid, EnumParticleTypes.FLAME);
                        }
                        mc.theWorld.removeEntity(squid);
                        squid = null;
                        return;
                    }
                } else {
                    percent = 0.0;
                }
                double easeInOutCirc = easeInOutCirc(1 - percent);
                anim.animate((float) easeInOutCirc, 450);
                squid.setPositionAndUpdate(squid.posX, squid.posY + anim.getOutput() * 0.9, squid.posZ);
            }

            if (squid != null) {
                squid.squidPitch = 0F;
                squid.prevSquidPitch = 0F;
                squid.squidYaw = 0F;
                squid.squidRotation = 90F;
            }
        }
        if (this.target != null && !mc.theWorld.loadedEntityList.contains(this.target)) {
            if (this.lightning.getValue()) {
                final EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ);
                mc.theWorld.addEntityToWorld((int) (-Math.random() * 100000), entityLightningBolt);
                SoundUtil.playSound("ambient.weather.thunder");
            }
            if (target.getHealth() <= 0){
                if (this.soundEffect.getValue()) {
                    playSound(-8);
                }
            }
            if (this.explosion.getValue()) {
                for (int i = 0; i <= 8; i++) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.FLAME);
                }

                SoundUtil.playSound("item.fireCharge.use");
            }

            if (this.squidValue.getValue()) {
                squid = new EntitySquid(mc.theWorld);
                mc.theWorld.addEntityToWorld(-8, squid);
                squid.setPosition(target.posX, target.posY, target.posZ);
            }
            if (this.bloodValue.getValue()) {
                mc.theWorld.spawnParticle(
                        EnumParticleTypes.BLOCK_CRACK,
                        target.posX,
                        target.posY + target.height - 0.75,
                        target.posZ,
                        0.0,
                        0.0,
                        0.0,
                        Block.getStateId(Blocks.redstone_block.getDefaultState())
                );
            }

            this.target = null;
        }
    }
    public void playSound(float volume) {
        new Thread(() -> {
            AudioInputStream as;
            try {
                as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("solitude/sound/sb.wav"))
                        .getInputStream())));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public double easeInOutCirc(double x) {
        return x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        final Entity entity = event.getTargetEntity();

        if (entity instanceof EntityLivingBase) {
            target = (EntityLivingBase) entity;

        }
    }

    ;
}