package qwq.arcane.gui.clickgui.arcane.component.settings;

import java.awt.Color;
import net.minecraft.util.MathHelper;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/component/settings/NumberComponent.class */
public class NumberComponent extends Component {
    private final NumberValue setting;
    private boolean dragging;
    private final Animation drag = new DecelerateAnimation(250, 1.0d);
    private float anim;

    public NumberComponent(NumberValue setting) {
        this.setting = setting;
        setHeight(30.0f);
        this.drag.setDirection(Direction.BACKWARDS);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.anim = RenderUtil.animate(this.anim, (float) ((145 * (this.setting.get().doubleValue() - this.setting.getMin())) / (this.setting.getMax() - this.setting.getMin())), 50.0f);
        float sliderWidth = this.anim;
        this.drag.setDirection(this.dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtil.drawRound(getX() + 10.0f, (getY() + getHeight()) - 4.0f, 145.0f, 1.0f, 0.0f, INSTANCE.getArcaneClickGui().linecolor);
        FontManager.Bold.get(18.0f).drawString(this.setting.getName(), getX() + 10.0f, getY() + 4.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 0.4f));
        FontManager.Bold.get(18.0f).drawString(this.setting.get().toString(), (getX() + 155.0f) - FontManager.Bold.get(18.0f).getStringWidth(this.setting.get().toString()), getY() + 4.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 0.4f));
        RoundedUtil.drawRound(getX() + 10.0f, getY() + 18.0f, 145, 2.0f, 2.0f, INSTANCE.getArcaneClickGui().versionColor);
        RoundedUtil.drawGradientHorizontal(getX() + 10.0f, getY() + 18.0f, sliderWidth, 2.0f, 2.0f, InterFace.color(1), new Color(-1));
        RoundedUtil.drawRound(getX() + 5.0f + sliderWidth, getY() + 17.0f, 8.0f, 4.0f, 1.0f, INSTANCE.getArcaneClickGui().fontcolor);
        if (this.dragging) {
            double difference = this.setting.getMax() - this.setting.getMin();
            double value = this.setting.getMin() + (MathHelper.clamp_double((mouseX - (getX() + 10.0f)) / 145, 0.0d, 1.0d) * difference);
            this.setting.setValue(Double.valueOf(MathUtils.incValue(value, this.setting.getStep())));
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 10.0f, getY() + 16.0f, 145, 6.0f, mouseX, mouseY) && mouseButton == 0) {
            this.dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
