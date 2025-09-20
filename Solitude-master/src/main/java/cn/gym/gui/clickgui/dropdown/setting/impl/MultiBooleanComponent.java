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
package cn.gym.gui.clickgui.dropdown.setting.impl;

import cn.gym.gui.clickgui.Component;
import cn.gym.module.impl.render.Interface;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.MultiBooleanValue;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.EaseOutSine;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MultiBooleanComponent extends Component {
    private final MultiBooleanValue setting;
    private final Map<BooleanValue, EaseOutSine> select = new HashMap<>();

    public MultiBooleanComponent(MultiBooleanValue setting) {
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 8;
        float heightoff = 0;

        RoundedUtil.drawRound(getX() + offset, getY() + FontManager.Semibold.get(15).getHeight() + 2, getWidth() - 5, heightoff, 4, new Color(128, 128, 128));
        FontManager.Semibold.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);

        for (BooleanValue boolValue : setting.getValues()) {
            float off = FontManager.Semibold.get(13).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5) {
                offset = 8;
                heightoff += FontManager.Semibold.get(13).getHeight() + 2;
            }
            select.putIfAbsent(boolValue, new EaseOutSine(250, 1));
            select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

            FontManager.Semibold.get(13).drawString(boolValue.getName(), getX() + offset, getY() + FontManager.Semibold.get(15).getHeight() + 2 + heightoff, ColorUtil.interpolateColor2(Interface.FirstColor.get().darker().darker(), Interface.FirstColor.get().brighter(), (float) select.get(boolValue).getOutput().floatValue()));

            offset += off;
        }

        setHeight(FontManager.Semibold.get(15).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 8;
        float heightoff = 0;
        for (BooleanValue boolValue : setting.getValues()) {
            float off = FontManager.Semibold.get(13).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5) {
                offset = 8;
                heightoff += FontManager.Semibold.get(13).getHeight() + 2;
            }
            if (RenderUtil.isHovering(getX() + offset, getY() + FontManager.Semibold.get(15).getHeight() + 2 + heightoff, FontManager.Semibold.get(13).getStringWidth(boolValue.getName()), FontManager.Semibold.get(13).getHeight(), mouseX, mouseY) && mouse == 0) {
                boolValue.set(!boolValue.get());
            }
            offset += off;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }
}
