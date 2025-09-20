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
import cn.gym.module.impl.render.ESP;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.ColorValue;
import net.minecraft.util.MathHelper;
import java.awt.*;

public class ColorPickerComponent extends Component {
    private final ColorValue setting;
    private final Animation open = new DecelerateAnimation(250, 1);
    private boolean opened, pickingHue, picking, pickingAlpha;

    public ColorPickerComponent(ColorValue setting) {
        this.setting = setting;
        this.setHeight(22);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        setHeight((float) (24 + 90 * open.getOutput()));

        Semibold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, -1);
        RenderUtil.drawCircle(getX() + 188, getY() + 22, 0, 360, 7, 2, true, (setting.isRainbow() ? INSTANCE.getModuleManager().getModule(Interface.class).getRainbow(0) : setting.get().getRGB()));
        ESP.resetColor();
        //picker
        if (open.getOutput() > 0) {
            float colorAlpha = 1;
            float gradientWidth = 80;
            float gradientHeight = (float) (80 * open.getOutput());
            float gradientX = getX() + 115;
            float gradientY = getY() + 34;
            float[] hsb = {setting.getHue(), setting.getSaturation(), setting.getBrightness()};
            //RenderUtil.drawRect(getX() + 64,getY() + 34,
            //        10, 78 * open.getOutput(), Color.BLACK.getRGB());
            RenderUtil.drawRect(getX() + 97, getY() + 34,
                    10, (float) (78 * open.getOutput()), Color.BLACK.getRGB());
            for (float i = 0; i <= 78 * open.getOutput(); i++) {
                RenderUtil.drawRect(getX() + 98, getY() + 34 + i, 8, 1, Color.getHSBColor((float) (i / 78 * open.getOutput()), 1f, 1f).getRGB());
            }
            RenderUtil.drawRect(getX() + 97, (float) (getY() + 34 + (setting.isRainbow() ? INSTANCE.getModuleManager().getModule(Interface.class).getRainbowHSB(0)[0] : setting.getHue()) * 78 * open.getOutput()),
                    10, 1, Color.WHITE.getRGB());
            for (float i = 0; i <= 78 * open.getOutput(); i++) {
                RenderUtil.drawRect(getX() + 85, getY() + 34 + i, 8, 1, ColorUtil.applyOpacity(new Color(setting.isRainbow() ? (INSTANCE.getModuleManager().getModule(Interface.class).getRainbow(0)) : (Color.HSBtoRGB(setting.getHue(), setting.getSaturation(), setting.getBrightness()))), setting.getAlpha() - i / 78).getRGB());
            }
            RenderUtil.drawRect(getX() + 84, (float) (getY() + 34 + (1 - setting.getAlpha()) * 78 * open.getOutput()),
                    10, 1, Color.WHITE.getRGB());

            float pickerY = (gradientY + 2) + (gradientHeight * (1 - hsb[2]));
            float pickerX = (gradientX) + (gradientWidth * hsb[1] - 1);
            pickerY = Math.max(Math.min(gradientY + gradientHeight - 2, pickerY), gradientY);
            pickerX = Math.max(Math.min(gradientX + gradientWidth - 2, pickerX), gradientX + 2);

            if (pickingHue) {
                setting.setHue(MathHelper.clamp_float((mouseY - (getY() + 34)) / 78, 0, 1));
            }
            if (pickingAlpha) {
                setting.setAlpha(MathHelper.clamp_float(1 - ((mouseY - (getY() + 34)) / 78), 0, 1));
            }
            if (picking) {
                setting.setBrightness(MathHelper.clamp_float(1 - ((mouseY - gradientY) / 80), 0, 1));
                setting.setSaturation(MathHelper.clamp_float((mouseX - gradientX) / 80, 0, 1));
            }

            Color firstColor = (setting.isRainbow() ? new Color(INSTANCE.getModuleManager().getModule(Interface.class).getRainbow(0)) : ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 1), colorAlpha));
            RoundedUtil.drawRound(gradientX, gradientY, gradientWidth, gradientHeight, 2,
                    ColorUtil.applyOpacity(firstColor, colorAlpha));
            Color secondColor = Color.getHSBColor(hsb[0], 0, 1);
            RoundedUtil.drawGradientHorizontal(gradientX, gradientY, gradientWidth, gradientHeight, 2 + .5f,
                    ColorUtil.applyOpacity(secondColor, colorAlpha),
                    ColorUtil.applyOpacity(secondColor, 0));
            Color thirdColor = Color.getHSBColor(hsb[0], 1, 0);
            RoundedUtil.drawGradientVertical(gradientX, gradientY, gradientWidth, gradientHeight, 2,
                    ColorUtil.applyOpacity(thirdColor, 0),
                    ColorUtil.applyOpacity(thirdColor, colorAlpha));

            RenderUtil.drawCircle((int) pickerX, (int) pickerY, 0, 360, 2, .1f, false, -1);

        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 181, getY() + 15f, 14, 14, mouseX, mouseY) && mouseButton == 1) {
            opened = !opened;
        }
        if (opened) {
            if (mouseButton == 0) {
                if (RenderUtil.isHovering(getX() + 98, getY() + 34, 8, 78, mouseX, mouseY)) {
                    pickingHue = true;
                }
                if (RenderUtil.isHovering(getX() + 115, getY() + 34, 80, 80, mouseX, mouseY)) {
                    picking = true;
                }
                if (RenderUtil.isHovering(getX() + 85, getY() + 34, 8, 78, mouseX, mouseY)) {
                    pickingAlpha = true;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            pickingHue = false;
            picking = false;
            pickingAlpha = false;
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
