package qwq.arcane.gui.clickgui.dropdown.setting.impl;

import java.awt.Color;
import net.minecraft.util.MathHelper;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/setting/impl/SliderComponent.class */
public class SliderComponent extends Component {
    private final NumberValue setting;
    private float anim;
    private boolean dragging;

    public SliderComponent(NumberValue setting) {
        this.setting = setting;
        setHeight((FontManager.Bold.get(15.0f).getHeight() * 2) + FontManager.Bold.get(15.0f).getHeight() + 2);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontManager.Bold.get(15.0f).drawString(this.setting.getName(), getX() + 4.0f, getY(), -1);
        this.anim = RenderUtil.animate(this.anim, (float) (((getWidth() - 8.0f) * (this.setting.get().doubleValue() - this.setting.getMin())) / (this.setting.getMax() - this.setting.getMin())), 15.0f);
        float sliderWidth = this.anim;
        RoundedUtil.drawRound(getX() + 4.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, getWidth() - 8.0f, 2.0f, 2.0f, new Color(1, 1, 1));
        RoundedUtil.drawGradientHorizontal(getX() + 4.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, sliderWidth, 2.0f, 2.0f, InterFace.mainColor.get(), InterFace.mainColor.get().brighter());
        RenderUtil.drawCircleCGUI(getX() + 4.0f + sliderWidth, getY() + FontManager.Bold.get(15.0f).getHeight() + 3.0f, 6.0f, -1);

        FontManager.Bold.get(15.0f).drawString(String.valueOf(this.setting.getMin()), getX() + 2.0f, getY() + (FontManager.Bold.get(15.0f).getHeight() * 2) + 2.0f, new Color(160, 160, 160).getRGB());
        FontManager.Bold.get(15.0f).drawCenteredString(String.valueOf(this.setting.get()), getX() + (getWidth() / 2.0f), getY() + (FontManager.Bold.get(15.0f).getHeight() * 2) + 2.0f, -1);
        FontManager.Bold.get(15.0f).drawString(String.valueOf(this.setting.getMax()), ((getX() - 2.0f) + getWidth()) - FontManager.Bold.get(15.0f).getStringWidth(String.valueOf(this.setting.getMax())), getY() + 2.0f + (FontManager.Bold.get(15.0f).getHeight() * 2) + 2.0f, new Color(160, 160, 160).getRGB());

        if (this.dragging) {
            double difference = this.setting.getMax() - this.setting.getMin();
            double value = this.setting.getMin() + (MathHelper.clamp_float((mouseX - getX()) / getWidth(), 0.0f, 1.0f) * difference);
            this.setting.setValue(Double.valueOf(MathUtils.incValue(value, this.setting.getStep())));
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && RenderUtil.isHovering(getX() + 2.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, getWidth(), 6.0f, mouseX, mouseY)) {
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

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
