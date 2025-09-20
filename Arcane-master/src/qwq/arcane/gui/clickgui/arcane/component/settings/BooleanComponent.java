package qwq.arcane.gui.clickgui.arcane.component.settings;

import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/component/settings/BooleanComponent.class */
public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final Animation enabled = new DecelerateAnimation(250, 1.0d);
    private final Animation hover = new DecelerateAnimation(250, 1.0d);

    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        setHeight(22.0f);
        this.enabled.setDirection(setting.get().booleanValue() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.enabled.setDirection(this.setting.get().booleanValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        this.hover.setDirection(RenderUtil.isHovering(getX() + 172.0f, getY() + 15.0f, 22.0f, 12.0f, mouseX, mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtil.drawRound(getX() + 10.0f, (getY() + getHeight()) - 4.0f, 145.0f, 1.0f, 0.0f, INSTANCE.getArcaneClickGui().linecolor);
        FontManager.Bold.get(18.0f).drawString(this.setting.getName(), getX() + 10.0f, getY() + 4.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 0.4f));
        RoundedUtil.drawRound(getX() + 135.0f, getY() + 4.0f, 20.0f, 10.0f, 4.0f, ColorUtil.applyOpacity(InterFace.color(1), 0.4f));
        RenderUtil.drawCircleCGUI(getX() + 141.0f + (this.enabled.getOutput().floatValue() * 9.0f), getY() + 9.0f, 8.0f, InterFace.color(1).darker().getRGB());
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 135.0f, getY() + 4.0f, 20.0f, 10.0f, mouseX, mouseY) && mouseButton == 0) {
            this.setting.toggle();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
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
