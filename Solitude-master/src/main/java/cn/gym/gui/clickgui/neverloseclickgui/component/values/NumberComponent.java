package cn.gym.gui.clickgui.neverloseclickgui.component.values;

import cn.gym.gui.clickgui.Component;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.util.MathHelper;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/14 13:42
 */
public class NumberComponent extends Component {
    private final NumberValue setting;
    private boolean dragging;
    private final Animation drag = new DecelerateAnimation(250, 1);
    public NumberComponent(NumberValue setting) {
        this.setting = setting;
        setHeight(22);
        drag.setDirection(Direction.BACKWARDS);
    }
    private float anim;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        int w = 55;
        anim = RenderUtil.animate(anim, (float) (w * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin())), 50);
        float sliderWidth = anim;
        drag.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        FontManager.Semibold.get(18).drawString(setting.getName(), getX() + 5, getY() + 15 + 1.5f,ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) drag.getOutput().floatValue()));

        RoundedUtil.drawRoundOutline(getX() + 191 - 2 - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()), getY() + 15, FontManager.Semibold.get(18).getStringWidth(setting.get().toString()) + 4, 12, 2,0.5f, new Color(31, 31, 31), new Color(47, 47, 47));
        FontManager.Semibold.get(17).drawString(setting.get().toString(), getX() + 191 - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()), getY() + 15 + 3f,ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) drag.getOutput().floatValue()));

        RoundedUtil.drawRound(getX() + 130 - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()), getY() + 20, w, 2, 2, new Color(0x3F4241));
        RoundedUtil.drawRound(getX() + 130 - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()), getY() + 20, sliderWidth, 2, 2, new Color(0x3D6083));
        RenderUtil.drawCircleCGUI(getX() + 130 + sliderWidth - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()), getY() + 21, 6, ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) drag.getOutput().floatValue()));
        if (dragging) {
            final double difference = this.setting.getMax() - this.setting
                    .getMin(), //
                    value = this.setting.getMin() + MathHelper
                            .clamp_double((mouseX - (getX() + 130 - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()))) / w, 0, 1) * difference;
            setting.setValue((double) MathUtils.incValue(value, setting.getStep()));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 130 - FontManager.Semibold.get(18).getStringWidth(setting.get().toString()), getY() + 20 - 2, 55, 2 + 4,mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragging = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }
}
