package cn.gym.gui.clickgui.neverloseclickgui.component.values;

import cn.gym.gui.clickgui.Component;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.animations.impl.SmoothStepAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.ModeValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glEnable;

/**
 * @Author：Guyuemang
 * @Date：2025/6/14 14:03
 */
public class ModeComponent extends Component {
    private final ModeValue setting;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final Animation open = new DecelerateAnimation(175, 1);
    private boolean opened;
    private final Map<String, DecelerateAnimation> select = new HashMap<>();
    public ModeComponent(ModeValue setting) {
        this.setting = setting;
        setHeight(22);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Semibold.get(17).drawString(setting.getName(),getX() + 6,getY() + 20,-1);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        if (open.getOutput() > 0.1){
            float totalHeight = (float) ((setting.getModes().length * 20 + 2) * open.getOutput());
            float y = (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLoseClickGui().getY() + 49 ? INSTANCE.getNeverLoseClickGui().getY() + 49 : (getY() + 12 - getHalfTotalHeight());

            GlStateManager.translate(0,0,2f);
            if (setting.getModes().length > 6){
                GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
                glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(getX() + 115, y, 80f, getVisibleHeight());
            }

            RoundedUtil.drawRoundOutline(getX() + 115, getY() + 12 - getHalfTotalHeight(), 80f, totalHeight,2,.1f,new Color(31, 31, 31), new Color(47, 47, 47));
            for (String str : setting.getModes()){
                select.putIfAbsent(str,new DecelerateAnimation(250, 1));
                select.get(str).setDirection(str.equals(setting.get()) ? Direction.FORWARDS : Direction.BACKWARDS);

                if (str.equals(setting.get())){
                    RoundedUtil.drawRound(getX() + 119, ((float) (getY() + 15 + (Arrays.asList(setting.getModes()).indexOf(str) * 20) * open.getOutput()) - getHalfTotalHeight()) + getScroll(), 72F,16f,2,
                            new Color(ColorUtil.applyOpacity(new Color(61, 61, 61).getRGB() , (float) select.get(setting.get()).getOutput().floatValue())));
                }
                Semibold.get(16).drawString(str,getX() + 121,getY() + 21 + (Arrays.asList(setting.getModes()).indexOf(str) * 20) * open.getOutput() - getHalfTotalHeight() + getScroll(),ColorUtil.interpolateColor2(Color.WHITE.darker().darker(), new Color(-1), (float) select.get(str).getOutput().floatValue()));
            }

            onScroll(30,mouseX,mouseY);
            maxScroll = Math.max(0, setting.getModes().length == 0 ? 0 : (setting.getModes().length - 6) * 20);

            if (setting.getModes().length > 6) {
                GL11.glPopAttrib();
            }

            GlStateManager.translate(0,0,-2f);
        } else {
            RoundedUtil.drawRoundOutline(getX() + 115,getY() + 13,80f,17,2,.1f,new Color(31, 31, 31), new Color(47, 47, 47));
            Semibold.get(16).drawString(setting.get(),getX() + 119,getY() + 15 + Semibold.get(16).getMiddleOfBox(17),-1);
        }
        super.drawScreen(mouseX, mouseY);
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (RenderUtil.isHovering(getX() + 115,getY() + 14,80f,20,mouseX,mouseY) && mouse == 1){
            opened = !opened;
        }
        if (opened){
            for (String str : setting.getModes()) {
                if (RenderUtil.isHovering(getX() + 115, ((getY() + 15 + Arrays.asList(setting.getModes()).indexOf(str) * 20) - getHalfTotalHeight()) + getScroll(), 52, 12, mouseX, mouseY) && mouse == 0) {
                    setting.set(str);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        float halfTotalHeight = (float) ((getSize() * 20 * open.getOutput()) / 2f);
        float y = (getY() + 12 - halfTotalHeight / 2f) < INSTANCE.getNeverLoseClickGui().getY() + 49 ? INSTANCE.getNeverLoseClickGui().getY() + 49 : (getY() + 12 - halfTotalHeight);
        float visibleHeight = getVisibleHeight();

        if (RenderUtil.isHovering(getX() + 115,
                y,
                80f,
                visibleHeight, mx, my)) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    private float getVisibleHeight() {
        return (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNeverLoseClickGui().getY() + 49 ? MathHelper.clamp_double(getY() + 12 - getSize() * 20 * open.getOutput() / 2f - INSTANCE.getNeverLoseClickGui().getY() + 49, 0, 999) : 122) * open.getOutput());
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
        return opened && RenderUtil.isHovering(getX() + 115,
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLoseClickGui().getY() + 49 ? INSTANCE.getNeverLoseClickGui().getY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNeverLoseClickGui().getY() + 49 ? MathHelper.clamp_double(getY() + 12 - getHalfTotalHeight() - INSTANCE.getNeverLoseClickGui().getY() + 49,0,999) : 122) * open.getOutput()), (int) mouseX, (int) mouseY);
    }
    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }
}
