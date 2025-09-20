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
import com.amaya.module.impl.render.ClickGui;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.EnumSetting;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.animations.impl.SmoothStepAnimation;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.amaya.gui.clickgui.neverlose.NegativeClickGui.*;

public class MultiBoxComponent extends Component {
    private final EnumSetting setting;
    private final Animation open = new DecelerateAnimation(175, 1);
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private boolean opened;
    private final Map<BooleanSetting, DecelerateAnimation> select = new HashMap<>();
    public MultiBoxComponent(EnumSetting setting) {
        this.setting = setting;
        setHeight(24);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, bgcolor4);

        FontManager.SEMIBOLD.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, textcolor.getRGB());

        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        if (open.getOutput() > 0.1) {
            GlStateManager.translate(0, 0, 2f);
            float outlineY = getY() + 11 - getHalfTotalHeight();
            float outlineHeight = (float) ((setting.getValues().size() * 20 + 2) * open.getOutput());
            float y = (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNegative().getPosY() + 49 ? INSTANCE.getNegative().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight());

            if (setting.getValues().size() > 6){
                GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RoundedUtil.scissor(getX() + 94,
                        y,
                        80f,
                        getVisibleHeight());
            }

            RoundedUtil.drawRoundOutline(getX() + 94, outlineY, 80f, outlineHeight, 2, .1f,bgcolor,bgcolor4);

            for (BooleanSetting boolValue : setting.getValues()) {
                select.putIfAbsent(boolValue,new DecelerateAnimation(250, 1));
                select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

                if (boolValue.get()) {
                    float boolValueY = (float) ((getY() + 14 + (setting.getValues().indexOf(boolValue) * 20) * open.getOutput()) - getHalfTotalHeight()) + getScroll();
                    RoundedUtil.drawRound(getX() + 98, boolValueY, 72, 16f, 2,
                            ColorUtil.applyOpacity(bgcolor
                                    , (float) select.get(boolValue).getOutput().floatValue()));
                }
                FontManager.SEMIBOLD.get(16).drawString(boolValue.getName(),getX() + 104, (getY() + 21 + (setting.getValues().indexOf(boolValue) * 20 * open.getOutput()) - getHalfTotalHeight()) + getScroll(),ColorUtil.interpolateColor2(ClickGui.Darkmode.getValue()? textcolor.darker().darker():new Color(166,166,166), textcolor, (float) select.get(boolValue).getOutput().floatValue()));

            }
            if (setting.getValues().size() > 6){
                RoundedUtil.drawRound(getX() + 172,
                        (float) (getY() + 12 - getSize() * 20 * open.getOutput() / 2f) + Math.abs((getVisibleHeight() - ((getVisibleHeight() / outlineHeight) * getVisibleHeight())) * (getScroll() / maxScroll)),
                        1f,
                        (getVisibleHeight() / outlineHeight) * getVisibleHeight(),
                        2,
                        bgcolor7.darker());
            }
            onScroll(30,mouseX,mouseY);
            maxScroll = Math.max(0, setting.getValues().isEmpty() ? 0 : (setting.getValues().size() - 6) * 20);

            if (setting.getValues().size() > 6) {
                GL11.glPopAttrib();
            }
            GlStateManager.translate(0, 0, -2f);
        } else {
            RoundedUtil.drawRoundOutline(getX() + 94, getY() + 12, 80f, 17, 2, .1f,bgcolor,bgcolor4);
            String enabledText = setting.isEnabled().isEmpty() ? "None" : (setting.isEnabled().length() > 15 ? setting.isEnabled().substring(0, 15) + "..." : setting.isEnabled());
            FontManager.SEMIBOLD.get(16).drawString(enabledText, getX() + 98, getY() + 15 + FontManager.SEMIBOLD.get(16).getMiddleOfBox(17), textcolor.getRGB());
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (RenderUtil.isHovering(getX() + 94,getY() + 14,80f,20,mouseX,mouseY) && mouse == 1){
            opened = !opened;
        }
        if (opened){
            for (BooleanSetting boolValue : setting.getValues()) {
                if (RenderUtil.isHovering(getX() + 98, (float) ((getY() + 15 + setting.getValues().indexOf(boolValue) * 20) - ((Math.min(4,(setting.getValues().size() - 1)) * 20) * open.getOutput()) / 2f) + getScroll(), 72, 12, mouseX, mouseY) && mouse == 0) {
                    boolValue.set(!boolValue.get());
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (RenderUtil.isHovering(getX() + 94,
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNegative().getPosY() + 49 ? INSTANCE.getNegative().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getNegative().getPosY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getNegative().getPosY() + 49,0,999) : 122)) * open.getOutput()), mx, my)) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }
    @Override
    public boolean isHovered(float mouseX, float mouseY) {
        return opened && RenderUtil.isHovering(getX() + 94,
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNegative().getPosY() + 49 ? INSTANCE.getNegative().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getNegative().getPosY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getNegative().getPosY() + 49,0,999) : 122)) * open.getOutput()), (int) mouseX, (int) mouseY);
    }
    private float getVisibleHeight() {
        return (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNegative().getPosY() + 49 ? MathHelper.clamp_double(getY() + 12 - getSize() * 20 * open.getOutput() / 2f - INSTANCE.getNegative().getPosY() + 49, 0, 999) : 122) * open.getOutput());
    }
    private float getHalfTotalHeight() {
        return (float) ((getSize() * 20 + 2) * open.getOutput() / 2f);
    }
    private int getSize(){
        return Math.min(4, (setting.getValues().size() - 1));
    }
    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }
}
