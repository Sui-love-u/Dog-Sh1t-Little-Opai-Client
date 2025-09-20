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
import cn.gym.utils.animations.impl.SmoothStepAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.MultiBooleanValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MultiBoxComponent extends Component {
    private final MultiBooleanValue setting;
    private final Animation open = new DecelerateAnimation(175, 1);
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private boolean opened;
    private final Map<BooleanValue, DecelerateAnimation> select = new HashMap<>();
    public MultiBoxComponent(MultiBooleanValue setting) {
        this.setting = setting;
        setHeight(22);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Semibold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, -1);

        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        if (open.getOutput() > 0.1) {
            GlStateManager.translate(0, 0, 2f);
            float outlineY = getY() + 11 - getHalfTotalHeight();
            float outlineHeight = (float) ((setting.getValues().size() * 20 + 2) * open.getOutput());
            float y = (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLoseClickGui().getY() + 49 ? INSTANCE.getNeverLoseClickGui().getY() + 49 : (getY() + 12 - getHalfTotalHeight());

            if (setting.getValues().size() > 6){
                GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(getX() + 115,
                        y,
                        80f,
                        getVisibleHeight());
            }

            RoundedUtil.drawRoundOutline(getX() + 115, outlineY, 80f, outlineHeight, 2, .1f,new Color(31, 31, 31), new Color(47, 47, 47));

            for (BooleanValue boolValue : setting.getValues()) {
                select.putIfAbsent(boolValue,new DecelerateAnimation(250, 1));
                select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

                if (boolValue.get()) {
                    float boolValueY = (float) ((getY() + 15 + (setting.getValues().indexOf(boolValue) * 20) * open.getOutput()) - getHalfTotalHeight()) + getScroll();
                    RoundedUtil.drawRound(getX() + 119, boolValueY, 72, 16f, 2,
                            ColorUtil.applyOpacity(new Color(61, 61, 61),select.get(boolValue).getOutput().floatValue()));
                }
                Semibold.get(16).drawString(boolValue.getName(),getX() + 121, (getY() + 21 + (setting.getValues().indexOf(boolValue) * 20 * open.getOutput()) - getHalfTotalHeight()) + getScroll(),ColorUtil.interpolateColor2(Color.WHITE.darker().darker(), new Color(-1), (float) select.get(boolValue).getOutput().floatValue()));

            }

            onScroll(30,mouseX,mouseY);
            maxScroll = Math.max(0, setting.getValues().isEmpty() ? 0 : (setting.getValues().size() - 6) * 20);

            if (setting.getValues().size() > 6) {
                GL11.glPopAttrib();
            }
            GlStateManager.translate(0, 0, -2f);
        } else {
            RoundedUtil.drawRoundOutline(getX() + 115, getY() + 12, 80f, 17, 2, .1f,new Color(31, 31, 31), new Color(47, 47, 47));
            String enabledText = setting.isEnabled().isEmpty() ? "None" : (setting.isEnabled().length() > 15 ? setting.isEnabled().substring(0, 15) + "..." : setting.isEnabled());
            Semibold.get(16).drawString(enabledText, getX() + 119, getY() + 15 + Semibold.get(16).getMiddleOfBox(17), new Color(-1).getRGB());
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (RenderUtil.isHovering(getX() + 115,getY() + 15,80f,20,mouseX,mouseY) && mouse == 1){
            opened = !opened;
        }
        if (opened){
            for (BooleanValue boolValue : setting.getValues()) {
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
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLoseClickGui().getY() + 49 ? INSTANCE.getNeverLoseClickGui().getY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getNeverLoseClickGui().getY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getNeverLoseClickGui().getY() + 49,0,999) : 122)) * open.getOutput()), mx, my)) {
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
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLoseClickGui().getY() + 49 ? INSTANCE.getNeverLoseClickGui().getY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getNeverLoseClickGui().getY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getNeverLoseClickGui().getY() + 49,0,999) : 122)) * open.getOutput()), (int) mouseX, (int) mouseY);
    }
    private float getVisibleHeight() {
        return (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNeverLoseClickGui().getY() + 49 ? MathHelper.clamp_double(getY() + 12 - getSize() * 20 * open.getOutput() / 2f - INSTANCE.getNeverLoseClickGui().getY() + 49, 0, 999) : 122) * open.getOutput());
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
