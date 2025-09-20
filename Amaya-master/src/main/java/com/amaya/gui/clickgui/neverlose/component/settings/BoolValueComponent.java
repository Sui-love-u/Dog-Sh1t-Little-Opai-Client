package com.amaya.gui.clickgui.neverlose.component.settings;

import com.amaya.gui.clickgui.neverlose.Component;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;

import java.awt.*;

import static com.amaya.gui.clickgui.neverlose.NegativeClickGui.*;

/**
 * @Author: Guyuemang
 */
public class BoolValueComponent extends Component {
    private final BooleanSetting setting;
    private final Animation hover = new DecelerateAnimation(250,1);
    private final Animation enabled = new DecelerateAnimation(250,1);

    public BoolValueComponent(BooleanSetting setting){
        this.setting = setting;
        setHeight(24);
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(RenderUtil.isHovering(getX() + 153, getY() + 16, 20, 12,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        
        FontManager.SEMIBOLD.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, textcolor.getRGB());

        RoundedUtil.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, bgcolor4);

        RoundedUtil.drawRound(getX() + 153, getY() + 16, 20, 12, 6, new Color(ColorUtil.interpolateColor2(new Color(ColorUtil.interpolateColor2(bgcolor4, lineColor, (float) enabled.getOutput().floatValue())),
                new Color(ColorUtil.interpolateColor2(bgcolor6, bgcolor6, (float) enabled.getOutput().floatValue())).brighter().brighter(), (float) hover.getOutput().floatValue())));

        RenderUtil.drawCircleCGUI(getX() + 159 + 8 * (float) enabled.getOutput().floatValue(), getY() + 22, 12, ColorUtil.interpolateColor2(new Color(ColorUtil.interpolateColor2(lineColor, circlecolor, (float) enabled.getOutput().floatValue())),
                new Color(ColorUtil.interpolateColor2(bgcolor7.darker().darker(), bgcolor7, (float) enabled.getOutput().floatValue())).brighter().brighter(), (float) hover.getOutput().floatValue()));

    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 153, getY() + 16, 20, 12,mouseX,mouseY) && mouseButton == 0){
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

