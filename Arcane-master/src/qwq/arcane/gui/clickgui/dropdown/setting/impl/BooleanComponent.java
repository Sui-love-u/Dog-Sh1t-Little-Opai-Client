package qwq.arcane.gui.clickgui.dropdown.setting.impl;

import java.awt.Color;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.SmoothStepAnimation;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/setting/impl/BooleanComponent.class */
public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final SmoothStepAnimation toggleAnimation = new SmoothStepAnimation(175, 1.0d);

    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        this.toggleAnimation.setDirection(Direction.BACKWARDS);
        setHeight(FontManager.Bold.get(15.0f).getHeight() + 5);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.toggleAnimation.setDirection(this.setting.getValue().booleanValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        FontManager.Bold.get(15.0f).drawString(this.setting.getName(), getX() + 4.0f, getY() + 2.5f, new Color(234, 234, 234).getRGB());
        RoundedUtil.drawRound((getX() + getWidth()) - 15.5f, getY() + 2.5f, 13.0f, 6.0f, 2.7f, InterFace.mainColor.get().brighter());
        RenderUtil.drawCircleCGUI(((getX() + getWidth()) - 12.5f) + (7.0f * this.toggleAnimation.getOutput().floatValue()), getY() + 5.5f, 7.0f, new Color(219, 226, 239).getRGB());
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering((getX() + getWidth()) - 15.5f, getY() + 4.0f, 13.0f, 6.0f, mouseX, mouseY) && mouseButton == 0) {
            this.setting.set(Boolean.valueOf(!this.setting.get().booleanValue()));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
