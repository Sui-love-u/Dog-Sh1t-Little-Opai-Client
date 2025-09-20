package qwq.arcane.gui.clickgui.arcane.component.settings;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.animations.impl.SmoothStepAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/component/settings/ModeComponent.class */
public class ModeComponent extends Component {
    private final ModeValue setting;
    private float rawScroll;
    private float scroll;
    private boolean opened;
    private float maxScroll = Float.MAX_VALUE;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0.0d, Direction.BACKWARDS);
    private final Animation open = new DecelerateAnimation(175, 1.0d);
    private final Map<String, DecelerateAnimation> select = new HashMap();

    public ModeComponent(ModeValue setting) {
        this.setting = setting;
        setHeight(38.0f);
        this.open.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(getX() + 10.0f, (getY() + getHeight()) - 4.0f, 145.0f, 1.0f, 0.0f, INSTANCE.getArcaneClickGui().linecolor);
        FontManager.Bold.get(18.0f).drawString(this.setting.getName(), getX() + 10.0f, getY() + 4.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 0.4f));
        this.open.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
        if (this.open.getOutput().doubleValue() > 0.1d) {
            float totalHeight = (float) (((this.setting.getModes().length * 20) + 2) * this.open.getOutput().doubleValue());
            float y = (getY() + 12.0f) - getHalfTotalHeight() < ((float) (INSTANCE.getArcaneClickGui().getY() + 49)) ? INSTANCE.getArcaneClickGui().getY() + 49 : (getY() + 12.0f) - getHalfTotalHeight();
            GlStateManager.translate(0.0f, 0.0f, 2.0f);
            RoundedUtil.drawRound(getX() + 10.0f, getY() + 32.0f, 145.0f, totalHeight, 2.0f, INSTANCE.getArcaneClickGui().smallbackgroundColor2);
            for (String str : this.setting.getModes()) {
                this.select.putIfAbsent(str, new DecelerateAnimation(250, 1.0d));
                this.select.get(str).setDirection(str.equals(this.setting.get()) ? Direction.FORWARDS : Direction.BACKWARDS);
                if (str.equals(this.setting.get())) {
                    RoundedUtil.drawRound(getX() + 12.0f, ((float) (getY() + 34.0f + (Arrays.asList(this.setting.getModes()).indexOf(str) * 20 * this.open.getOutput().doubleValue()))) + getScroll(), 141.0f, 18.0f, 2.0f, new Color(ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().backgroundColor.getRGB(), this.select.get(this.setting.get()).getOutput().floatValue())));
                }
                FontManager.Bold.get(16.0f).drawString(str, getX() + 14.0f, getY() + 40.0f + (Arrays.asList(this.setting.getModes()).indexOf(str) * 20 * this.open.getOutput().doubleValue()) + getScroll(), ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), this.select.get(str).getOutput().floatValue()));
            }
            onScroll(30, mouseX, mouseY);
            this.maxScroll = Math.max(0, this.setting.getModes().length == 0 ? 0 : (this.setting.getModes().length - 6) * 20);
            if (this.setting.getModes().length > 6) {
                GL11.glPopAttrib();
            }
            GlStateManager.translate(0.0f, 0.0f, -2.0f);
        }
        RoundedUtil.drawRound(getX() + 10.0f, getY() + 14.0f, 145.0f, 14.0f, 2.0f, INSTANCE.getArcaneClickGui().smallbackgroundColor2);
        FontManager.Bold.get(16.0f).drawString(this.setting.get(), getX() + 14.0f, getY() + 15.0f + FontManager.Bold.get(16.0f).getMiddleOfBox(17.0f), ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 1.0f));
        FontManager.Icon.get(16.0f).drawString("U", getX() + 145.0f, getY() + 20.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 1.0f));
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (RenderUtil.isHovering(getX() + 10.0f, getY() + 14.0f, 145.0f, 14.0f, mouseX, mouseY) && mouse == 1) {
            this.opened = !this.opened;
        }
        if (this.opened) {
            for (String str : this.setting.getModes()) {
                if (RenderUtil.isHovering(getX() + 12.0f, ((float) (getY() + 34.0f + (Arrays.asList(this.setting.getModes()).indexOf(str) * 20 * this.open.getOutput().doubleValue()))) + getScroll(), 141.0f, 18.0f, mouseX, mouseY) && mouse == 0) {
                    this.setting.set(str);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    public void onScroll(int ms, int mx, int my) {
        this.scroll = (float) (this.rawScroll - this.scrollAnimation.getOutput().doubleValue());
        float halfTotalHeight = (float) (((getSize() * 20) * this.open.getOutput().doubleValue()) / 2.0d);
        float y = (getY() + 12.0f) - (halfTotalHeight / 2.0f) < ((float) (INSTANCE.getArcaneClickGui().getY() + 49)) ? INSTANCE.getArcaneClickGui().getY() + 49 : (getY() + 12.0f) - halfTotalHeight;
        float visibleHeight = getVisibleHeight();
        if (RenderUtil.isHovering(getX() + 115.0f, y, 80.0f, visibleHeight, mx, my)) {
            this.rawScroll += Mouse.getDWheel() * 20.0f;
        }
        this.rawScroll = Math.max(Math.min(0.0f, this.rawScroll), -this.maxScroll);
        this.scrollAnimation = new SmoothStepAnimation(ms, this.rawScroll - this.scroll, Direction.BACKWARDS);
    }

    private float getVisibleHeight() {
        return (float) ((((double) (getY() + 12.0f)) - ((((double) (getSize() * 20)) * this.open.getOutput().doubleValue()) / 2.0d) < ((double) (INSTANCE.getArcaneClickGui().getY() + 49)) ? MathHelper.clamp_double((((getY() + 12.0f) - (((getSize() * 20) * this.open.getOutput().doubleValue()) / 2.0d)) - INSTANCE.getArcaneClickGui().getY()) + 49.0d, 0.0d, 999.0d) : 122.0d) * this.open.getOutput().doubleValue());
    }

    private float getHalfTotalHeight() {
        return (float) (((getSize() * 20) * this.open.getOutput().doubleValue()) / 2.0d);
    }

    private int getSize() {
        return Math.min(4, this.setting.getModes().length - 1);
    }

    public float getScroll() {
        this.scroll = (float) (this.rawScroll - this.scrollAnimation.getOutput().doubleValue());
        return this.scroll;
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isHovered(float mouseX, float mouseY) {
        if (this.opened) {
            if (RenderUtil.isHovering(getX() + 115.0f, (getY() + 12.0f) - getHalfTotalHeight() < ((float) (INSTANCE.getArcaneClickGui().getY() + 49)) ? INSTANCE.getArcaneClickGui().getY() + 49 : (getY() + 12.0f) - getHalfTotalHeight(), 80.0f, (float) ((((double) (getY() + 12.0f)) - ((((double) (getSize() * 20)) * this.open.getOutput().doubleValue()) / 2.0d) < ((double) (INSTANCE.getArcaneClickGui().getY() + 49)) ? MathHelper.clamp_double((((getY() + 12.0f) - getHalfTotalHeight()) - INSTANCE.getArcaneClickGui().getY()) + 49.0f, 0.0d, 999.0d) : 122.0d) * this.open.getOutput().doubleValue()), (int) mouseX, (int) mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
