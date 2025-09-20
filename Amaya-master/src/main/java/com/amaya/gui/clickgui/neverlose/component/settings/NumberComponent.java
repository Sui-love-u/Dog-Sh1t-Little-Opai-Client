/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package com.amaya.gui.clickgui.neverlose.component.settings;

import com.amaya.gui.clickgui.neverlose.Component;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.util.MathHelper;

import static com.amaya.gui.clickgui.neverlose.NegativeClickGui.*;

/**
 * @Author: Guyuemang
 */
public class NumberComponent extends Component {
    private final NumberSetting setting;
    private float anim;
    private boolean dragging;
    private final Animation drag = new DecelerateAnimation(250, 1);
    public NumberComponent(NumberSetting setting) {
        this.setting = setting;
        setHeight(24);
        drag.setDirection(Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        
        RoundedUtil.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, bgcolor4);

        RoundedUtil.drawRound(getX() + 90, getY() + 22, 60, 2, 1, bgcolor6);

        RoundedUtil.drawRoundOutline(getX() + 154, getY() + 18, 20, 10, 2, .1f, bgcolor4, lineColor);

        anim = RenderUtil.animate(anim, (float) (60 * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin())), 50);
        float sliderWidth = anim;
        drag.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtil.drawRound(getX() + 90, getY() + 22, sliderWidth, 2, 1, bgcolor6);

        RoundedUtil.drawCircle(getX() + 90 + sliderWidth, getY() + 23, 0, 360, (float) 3, .1f, true, circlecolor.getRGB());
        FontManager.SEMIBOLD.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, ColorUtil.interpolateColor2(textcolor.darker().darker(), textcolor, (float) drag.getOutput().floatValue()));
        FontManager.SEMIBOLD.get(12).drawCenteredString((int) ((setting).get() * 100.0D) / 100.0D + "", getX() + 164, getY() + 22, textcolor.getRGB());

        if (dragging) {
            final double difference = this.setting.getMax() - this.setting
                    .getMin(), //
                    value = this.setting.getMin() + MathHelper
                            .clamp_double((mouseX - (getX() + 90)) / 60, 0, 1) * difference;
            setting.setValue((double) MathUtils.incValue(value, setting.getStep()));
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 90, getY() + 18, 60, 10,mouseX, mouseY) && mouseButton == 0) {
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
