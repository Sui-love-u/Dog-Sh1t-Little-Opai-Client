package cn.gym.module.impl.combat;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.TickEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.time.TimerUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:55
 */
public class AutoClicker extends Module {
    private final NumberValue minAps = new NumberValue("Min Aps", 10, 1, 20,1);
    private final NumberValue maxAps = new NumberValue("Max Aps", 12, 1, 20,1);
    private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", true);
    private final TimerUtil clickTimer = new TimerUtil();

    public AutoClicker() {
        super("AutoClicker",Category.Combat);
    }

    @Override
    public void onEnable(){
        clickTimer.reset();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (breakBlocks.get() && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            return;

        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (clickTimer.hasTimeElapsed(1000 / MathUtils.nextInt(minAps.get().intValue(), maxAps.get().intValue()))) {
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                clickTimer.reset();
            }
        }
    }
}
