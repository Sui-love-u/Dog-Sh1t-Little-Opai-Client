/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.gui.clickgui.neverloseclickgui.component.values;

import cn.gym.gui.clickgui.Component;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.BooleanValue;

import java.awt.*;

public class BooleanComponent extends Component {
    private final BooleanValue setting;
    private final Animation enabled = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);
    public BooleanComponent(BooleanValue setting) {
        this.setting = setting;
        setHeight(22);
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(RenderUtil.isHovering(getX() + 172, getY() + 15, 22, 12,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        FontManager.Semibold.get(18).drawString(setting.getName(), getX() + 5, getY() + 15 + 1.5f, ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) enabled.getOutput().floatValue()));
        RoundedUtil.drawRound(getX() + 172, getY() + 15, 22, 12, 5.5f, setting.get() ? new Color(ColorUtil.interpolateColor2(new Color(0x2B445D),new Color(0x3D6083),(float) hover.getOutput().floatValue())) : new Color(ColorUtil.interpolateColor2(new Color(0x2F3130),new Color(0x3F4241),(float) hover.getOutput().floatValue())));
        RenderUtil.drawCircleCGUI(getX() + 179 + enabled.getOutput().floatValue() * 9f, getY() + 21, 12,  ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) enabled.getOutput().floatValue()));

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 172, getY() + 15, 22, 12,mouseX,mouseY) && mouseButton == 0){
            setting.toggle();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
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
