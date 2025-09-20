package qwq.arcane.module.impl.visuals;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.AttackEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.utils.animations.impl.ContinualAnimation;
import qwq.arcane.utils.render.SoundUtil;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/KillEffect.class */
public class KillEffect extends Module {
    private EntitySquid squid;
    private double percent;
    private final ContinualAnimation anim;
    private final BoolValue lightning;
    private final BoolValue explosion;
    private final BoolValue squidValue;
    private final BoolValue bloodValue;
    private final BoolValue soundEffect;
    private EntityLivingBase target;

    public KillEffect() {
        super("KillEffect", Category.Visuals);
        this.percent = 0.0d;
        this.anim = new ContinualAnimation();
        this.lightning = new BoolValue("Lightning", true);
        this.explosion = new BoolValue("Explosion", true);
        this.squidValue = new BoolValue("Squid", true);
        this.bloodValue = new BoolValue("Blood", true);
        this.soundEffect = new BoolValue("Sound Effect", true);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (this.squidValue.getValue().booleanValue()) {
            if (this.squid != null) {
                if (mc.theWorld.loadedEntityList.contains(this.squid)) {
                    if (this.percent < 1.0d) {
                        this.percent += Math.random() * 0.048d;
                    }
                    if (this.percent >= 1.0d) {
                        this.percent = 0.0d;
                        for (int i = 0; i <= 8; i++) {
                            mc.effectRenderer.emitParticleAtEntity(this.squid, EnumParticleTypes.FLAME);
                        }
                        mc.theWorld.removeEntity(this.squid);
                        this.squid = null;
                        return;
                    }
                } else {
                    this.percent = 0.0d;
                }
                double easeInOutCirc = easeInOutCirc(1.0d - this.percent);
                this.anim.animate((float) easeInOutCirc, 450);
                this.squid.setPositionAndUpdate(this.squid.posX, this.squid.posY + (this.anim.getOutput() * 0.9d), this.squid.posZ);
            }
            if (this.squid != null) {
                this.squid.squidPitch = 0.0f;
                this.squid.prevSquidPitch = 0.0f;
                this.squid.squidYaw = 0.0f;
                this.squid.squidRotation = 90.0f;
            }
        }
        if (this.target != null && !mc.theWorld.loadedEntityList.contains(this.target)) {
            if (this.lightning.getValue().booleanValue()) {
                EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, this.target.posX, this.target.posY, this.target.posZ);
                mc.theWorld.addEntityToWorld((int) ((-Math.random()) * 100000.0d), entityLightningBolt);
                SoundUtil.playSound("ambient.weather.thunder");
            }
            if (this.target.getHealth() <= 0.0f && this.soundEffect.getValue().booleanValue()) {
                playSound(-8.0f);
            }
            if (this.explosion.getValue().booleanValue()) {
                for (int i2 = 0; i2 <= 8; i2++) {
                    mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.FLAME);
                }
                SoundUtil.playSound("item.fireCharge.use");
            }
            if (this.squidValue.getValue().booleanValue()) {
                this.squid = new EntitySquid(mc.theWorld);
                mc.theWorld.addEntityToWorld(-8, this.squid);
                this.squid.setPosition(this.target.posX, this.target.posY, this.target.posZ);
            }
            if (this.bloodValue.getValue().booleanValue()) {
                mc.theWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.target.posX, (this.target.posY + this.target.height) - 0.75d, this.target.posZ, 0.0d, 0.0d, 0.0d, Block.getStateId(Blocks.redstone_block.getDefaultState()));
            }
            this.target = null;
        }
    }

    public void playSound(float volume) {
        new Thread(() -> {
            try {
                AudioInputStream as = AudioSystem.getAudioInputStream(new BufferedInputStream((InputStream) Objects.requireNonNull(Mine.getMinecraft().getResourceManager().getResource(new ResourceLocation("solitude/sound/sb.wav")).getInputStream())));
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
        return x < 0.5d ? (1.0d - Math.sqrt(1.0d - Math.pow(2.0d * x, 2.0d))) / 2.0d : (Math.sqrt(1.0d - Math.pow(((-2.0d) * x) + 2.0d, 2.0d)) + 1.0d) / 2.0d;
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        Entity entity = event.getTargetEntity();
        if (entity instanceof EntityLivingBase) {
            this.target = (EntityLivingBase) entity;
        }
    }
}
