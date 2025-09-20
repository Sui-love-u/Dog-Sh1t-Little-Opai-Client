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
import com.amaya.module.setting.impl.ModeSetting;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.amaya.gui.clickgui.neverlose.NegativeClickGui.*;
import static org.lwjgl.opengl.GL11.glEnable;

/**
 * @Author: Guyuemang
 */
public class ModeComponent extends Component {
    private final ModeSetting setting;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final Animation open = new DecelerateAnimation(175, 1);
    private boolean opened;
    private final Map<String, DecelerateAnimation> select = new HashMap<>();
    public ModeComponent(ModeSetting setting) {
        this.setting = setting;
        setHeight(24);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, bgcolor4);
        FontManager.SEMIBOLD.get(17).drawString(setting.getName(),getX() + 6,getY() + 20,textcolor.getRGB());


        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        if (open.getOutput() > 0.1){
            float totalHeight = (float) ((setting.getModes().length * 20 + 2) * open.getOutput());
            float y = (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNegative().getPosY() + 49 ? INSTANCE.getNegative().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight());

            GlStateManager.translate(0,0,2f);
            if (setting.getModes().length > 6){
                GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
                glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(getX() + 94, y, 80f, getVisibleHeight());
            }

            RoundedUtil.drawRoundOutline(getX() + 94, getY() + 12 - getHalfTotalHeight(), 80f, totalHeight,2,.1f,bgcolor,bgcolor4);

            for (String str : setting.getModes()){
                select.putIfAbsent(str,new DecelerateAnimation(250, 1));
                select.get(str).setDirection(str.equals(setting.get()) ? Direction.FORWARDS : Direction.BACKWARDS);

                if (str.equals(setting.get())){

                    RoundedUtil.drawRound(getX() + 98, ((float) (getY() + 15 + (Arrays.asList(setting.getModes()).indexOf(str) * 20) * open.getOutput()) - getHalfTotalHeight()) + getScroll(), 72F,16f,2,
                            ColorUtil.applyOpacity(new Color(200,200,200)
                                    , (float) select.get(setting.get()).getOutput().floatValue()));
                }

                FontManager.SEMIBOLD.get(16).drawString(str,getX() + 104,getY() + 21 + (Arrays.asList(setting.getModes()).indexOf(str) * 20) * open.getOutput() - getHalfTotalHeight() + getScroll(),ColorUtil.interpolateColor2(textcolor.darker().darker(), textcolor, (float) select.get(str).getOutput().floatValue()));
            }

            if (setting.getModes().length > 6){

                RoundedUtil.drawRound(getX() + 172,
                        (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f) + Math.abs((getVisibleHeight() - ((getVisibleHeight() / totalHeight) * getVisibleHeight())) * (getScroll() / maxScroll))),
                        1f,
                        (getVisibleHeight() / totalHeight) * getVisibleHeight(),
                        2,
                        bgcolor6);
            }
            onScroll(30,mouseX,mouseY);
            maxScroll = Math.max(0, setting.getModes().length == 0 ? 0 : (setting.getModes().length - 6) * 20);

            if (setting.getModes().length > 6) {
                GL11.glPopAttrib();
            }

            GlStateManager.translate(0,0,-2f);
        } else {

            RoundedUtil.drawRoundOutline(getX() + 94,getY() + 13,80f,17,2,.1f,bgcolor,bgcolor4);

            FontManager.SEMIBOLD.get(16).drawString(setting.get(),getX() + 98,getY() + 15 + FontManager.SEMIBOLD.get(16).getMiddleOfBox(17),textcolor.getRGB());
        }
        super.drawScreen(mouseX, mouseY);
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (RenderUtil.isHovering(getX() + 94,getY() + 14,80f,20,mouseX,mouseY) && mouse == 1){
            opened = !opened;
        }
        if (opened){
            for (String str : setting.getModes()) {
                if (RenderUtil.isHovering(getX() + 98, ((getY() + 15 + Arrays.asList(setting.getModes()).indexOf(str) * 20) - getHalfTotalHeight()) + getScroll(), 52, 12, mouseX, mouseY) && mouse == 0) {
                    setting.set(str);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        float halfTotalHeight = (float) ((getSize() * 20 * open.getOutput()) / 2f);
        float y = (getY() + 12 - halfTotalHeight / 2f) < INSTANCE.getNegative().getPosY() + 49 ? INSTANCE.getNegative().getPosY() + 49 : (getY() + 12 - halfTotalHeight);
        float visibleHeight = getVisibleHeight();

        if (RenderUtil.isHovering(getX() + 94,
                y,
                80f,
                visibleHeight, mx, my)) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    private float getVisibleHeight() {
        return (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNegative().getPosY() + 49 ? MathHelper.clamp_double(getY() + 12 - getSize() * 20 * open.getOutput() / 2f - INSTANCE.getNegative().getPosY() + 49, 0, 999) : 122) * open.getOutput());
    }
    private float getHalfTotalHeight() {
        return (float) ((getSize() * 20 * open.getOutput()) / 2f);
    }
    private int getSize(){
        return Math.min(4, (setting.getModes().length - 1));
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
                (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNegative().getPosY() + 49 ? MathHelper.clamp_double(getY() + 12 - getHalfTotalHeight() - INSTANCE.getNegative().getPosY() + 49,0,999) : 122) * open.getOutput()), (int) mouseX, (int) mouseY);
    }
    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }
}
