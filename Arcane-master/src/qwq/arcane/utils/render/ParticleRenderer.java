package qwq.arcane.utils.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.time.TimerUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/ParticleRenderer.class */
public class ParticleRenderer implements Instance {
    public static int rendered;
    private static boolean sentParticles;
    public static final List<Particle> particles = new ArrayList();
    public static final TimerUtil timer = new TimerUtil();

    public static void renderParticle(EntityLivingBase target, float x, float y) {
        int rgb;
        for (Particle p : particles) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (p.opacity > 4) {
                p.render2D();
            }
        }
        if (timer.hasTimeElapsed(16L)) {
            for (Particle p2 : particles) {
                p2.updatePosition();
                if (p2.opacity < 1) {
                    particles.remove(p2);
                }
            }
            timer.reset();
        }
        if (target.hurtTime == 9 && !sentParticles) {
            for (int i = 0; i <= 10; i++) {
                Particle particle = new Particle();
                float f = x + 20.0f;
                float f2 = y + 20.0f;
                float fRandom = (float) ((Math.random() - 0.5d) * 2.0d * 1.4d);
                float fRandom2 = (float) ((Math.random() - 0.5d) * 2.0d * 1.4d);
                float fRandomizeDouble = (float) MathUtils.randomizeDouble(4.0d, 5.0d);
                if (i % 2 == 0) {
                    rgb = InterFace.color(i * 100).getRGB();
                } else {
                    rgb = InterFace.color((-i) * 100).getRGB();
                }
                particle.init(f, f2, fRandom, fRandom2, fRandomizeDouble, rgb);
                particles.add(particle);
            }
            sentParticles = true;
        }
        if (target.hurtTime == 8) {
            sentParticles = false;
        }
    }
}
