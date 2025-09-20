package qwq.arcane.gui.clickgui.arcane.component.settings;

import java.awt.Color;
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
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.MultiBooleanValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/component/settings/MultiBoxComponent.class */
public class MultiBoxComponent extends Component {
    private final MultiBooleanValue setting;
    private float rawScroll;
    private float scroll;
    private boolean opened;
    private final Animation open = new DecelerateAnimation(175, 1.0d);
    private float maxScroll = Float.MAX_VALUE;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0.0d, Direction.BACKWARDS);
    private final Map<BoolValue, DecelerateAnimation> select = new HashMap();

    public MultiBoxComponent(MultiBooleanValue setting) {
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
            GlStateManager.translate(0.0f, 0.0f, 2.0f);
            float y = getY() + 11.0f + getHalfTotalHeight();
            float outlineHeight = (float) (((this.setting.getValues().size() * 20) + 2) * this.open.getOutput().doubleValue());
            float y2 = (getY() + 12.0f) + getHalfTotalHeight() < ((float) (INSTANCE.getArcaneClickGui().getY() + 49)) ? INSTANCE.getArcaneClickGui().getY() + 49 : getY() + 12.0f + getHalfTotalHeight();
            RoundedUtil.drawRound(getX() + 10.0f, getY() + 32.0f, 145.0f, outlineHeight, 2.0f, INSTANCE.getArcaneClickGui().smallbackgroundColor2);
            for (BoolValue boolValue : this.setting.getValues()) {
                this.select.putIfAbsent(boolValue, new DecelerateAnimation(250, 1.0d));
                this.select.get(boolValue).setDirection(boolValue.get().booleanValue() ? Direction.FORWARDS : Direction.BACKWARDS);
                if (boolValue.get().booleanValue()) {
                    float boolValueY = ((float) (getY() + 34.0f + (this.setting.getValues().indexOf(boolValue) * 20 * this.open.getOutput().doubleValue()))) + getScroll();
                    RoundedUtil.drawRound(getX() + 12.0f, boolValueY, 141.0f, 18.0f, 2.0f, new Color(ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().backgroundColor.getRGB(), this.select.get(boolValue).getOutput().floatValue())));
                }
                FontManager.Bold.get(16.0f).drawString(boolValue.getName(), getX() + 14.0f, getY() + 40.0f + (this.setting.getValues().indexOf(boolValue) * 20 * this.open.getOutput().doubleValue()) + getScroll(), ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), this.select.get(boolValue).getOutput().floatValue()));
            }
            onScroll(30, mouseX, mouseY);
            this.maxScroll = Math.max(0, this.setting.getValues().isEmpty() ? 0 : (this.setting.getValues().size() - 6) * 20);
            if (this.setting.getValues().size() > 6) {
                GL11.glPopAttrib();
            }
            GlStateManager.translate(0.0f, 0.0f, -2.0f);
        }
        RoundedUtil.drawRound(getX() + 10.0f, getY() + 14.0f, 145.0f, 14.0f, 2.0f, INSTANCE.getArcaneClickGui().smallbackgroundColor2);
        String enabledText = this.setting.isEnabled().isEmpty() ? "None" : this.setting.isEnabled().length() > 30 ? this.setting.isEnabled().substring(0, 30) + "..." : this.setting.isEnabled();
        FontManager.Bold.get(16.0f).drawString(enabledText, getX() + 14.0f, getY() + 15.0f + FontManager.Bold.get(16.0f).getMiddleOfBox(17.0f), ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 1.0f));
        FontManager.Icon.get(16.0f).drawString("U", getX() + 145.0f, getY() + 20.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 1.0f));
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (RenderUtil.isHovering(getX() + 10.0f, getY() + 14.0f, 145.0f, 14.0f, mouseX, mouseY) && mouse == 1) {
            this.opened = !this.opened;
        }
        if (this.opened) {
            for (BoolValue boolValue : this.setting.getValues()) {
                if (RenderUtil.isHovering(getX() + 12.0f, ((float) (getY() + 34.0f + (this.setting.getValues().indexOf(boolValue) * 20 * this.open.getOutput().doubleValue()))) + getScroll(), 141.0f, 18.0f, mouseX, mouseY) && mouse == 0) {
                    boolValue.set(Boolean.valueOf(!boolValue.get().booleanValue()));
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    public void onScroll(int ms, int mx, int my) {
        this.scroll = (float) (this.rawScroll - this.scrollAnimation.getOutput().doubleValue());
        if (RenderUtil.isHovering(getX() + 94.0f, (getY() + 12.0f) - getHalfTotalHeight() < ((float) (INSTANCE.getArcaneClickGui().getY() + 49)) ? INSTANCE.getArcaneClickGui().getY() + 49 : (getY() + 12.0f) - getHalfTotalHeight(), 80.0f, (float) ((((double) (getY() + 12.0f)) - ((((double) (getSize() * 20)) * this.open.getOutput().doubleValue()) / 2.0d) < ((double) (INSTANCE.getArcaneClickGui().getY() + 49)) ? MathHelper.clamp_float((((getY() + 12.0f) - getHalfTotalHeight()) - INSTANCE.getArcaneClickGui().getY()) + 49.0f, 0.0f, 999.0f) : 122.0f) * this.open.getOutput().doubleValue()), mx, my)) {
            this.rawScroll += Mouse.getDWheel() * 20.0f;
        }
        this.rawScroll = Math.max(Math.min(0.0f, this.rawScroll), -this.maxScroll);
        this.scrollAnimation = new SmoothStepAnimation(ms, this.rawScroll - this.scroll, Direction.BACKWARDS);
    }

    public float getScroll() {
        this.scroll = (float) (this.rawScroll - this.scrollAnimation.getOutput().doubleValue());
        return this.scroll;
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isHovered(float mouseX, float mouseY) {
        if (this.opened) {
            if (RenderUtil.isHovering(getX() + 94.0f, (getY() + 12.0f) - getHalfTotalHeight() < ((float) (INSTANCE.getArcaneClickGui().getY() + 49)) ? INSTANCE.getArcaneClickGui().getY() + 49 : (getY() + 12.0f) - getHalfTotalHeight(), 80.0f, (float) ((((double) (getY() + 12.0f)) - ((((double) (getSize() * 20)) * this.open.getOutput().doubleValue()) / 2.0d) < ((double) (INSTANCE.getArcaneClickGui().getY() + 49)) ? MathHelper.clamp_float((((getY() + 12.0f) - getHalfTotalHeight()) - INSTANCE.getArcaneClickGui().getY()) + 49.0f, 0.0f, 999.0f) : 122.0f) * this.open.getOutput().doubleValue()), (int) mouseX, (int) mouseY)) {
                return true;
            }
        }
        return false;
    }

    private float getVisibleHeight() {
        return (float) ((((double) (getY() + 12.0f)) - ((((double) (getSize() * 20)) * this.open.getOutput().doubleValue()) / 2.0d) < ((double) (INSTANCE.getArcaneClickGui().getY() + 49)) ? MathHelper.clamp_double((((getY() + 12.0f) - (((getSize() * 20) * this.open.getOutput().doubleValue()) / 2.0d)) - INSTANCE.getArcaneClickGui().getY()) + 49.0d, 0.0d, 999.0d) : 122.0d) * this.open.getOutput().doubleValue());
    }

    private float getHalfTotalHeight() {
        return (float) (((getSize() * 20) * this.open.getOutput().doubleValue()) / 2.0d);
    }

    private int getSize() {
        return Math.min(4, this.setting.getValues().size() - 1);
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
