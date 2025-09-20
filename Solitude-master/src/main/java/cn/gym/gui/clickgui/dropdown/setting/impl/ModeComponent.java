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
import cn.gym.value.impl.ModeValue;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;

import java.awt.*;

public class ModeComponent extends Component {
    private final ModeValue setting;

    public ModeComponent(ModeValue setting) {
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0;
        float heightoff = 0;

        RoundedUtil.drawRound(getX() + offset, getY() + FontManager.Semibold.get(15).getHeight() + 2, getWidth() - 5, heightoff, 4, new Color(50, 50, 108, 200));
        FontManager.Semibold.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);

        for (String text : setting.getModes()) {
            float off = FontManager.Semibold.get(13).getStringWidth(text) + 2;
            if (offset + off >= (getWidth() - 5)) {
                offset = 0;
                heightoff += 8;
            }

            if (text.equals(setting.get())) {
                FontManager.Semibold.get(13).drawString(text, getX() + offset + 8, getY() + FontManager.Semibold.get(15).getHeight() + heightoff, Interface.FirstColor.get().getRGB());
            } else {
                FontManager.Semibold.get(13).drawString(text, getX() + offset + 8, getY() + FontManager.Semibold.get(15).getHeight() + heightoff,
                        Interface.FirstColor.get().darker().darker().getRGB());
            }

            offset += off;

        }

        setHeight(FontManager.Semibold.get(15).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 0;
        float heightoff = 0;
        for (String text : setting.getModes()) {
            float off = FontManager.Semibold.get(13).getStringWidth(text) + 2;
            if (offset + off >= (getWidth() - 5)) {
                offset = 0;
                heightoff += 8;
            }
            if (RenderUtil.isHovering(getX() + offset + 8, getY() + FontManager.Semibold.get(15).getHeight() + heightoff, FontManager.Semibold.get(13).getStringWidth(text), FontManager.Semibold.get(13).getHeight(), mouseX, mouseY) && mouse == 0) {
                setting.set(text);
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
