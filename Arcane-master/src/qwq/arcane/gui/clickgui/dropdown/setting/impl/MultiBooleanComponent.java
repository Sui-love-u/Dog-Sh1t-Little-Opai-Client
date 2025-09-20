package qwq.arcane.gui.clickgui.dropdown.setting.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.EaseOutSine;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.MultiBooleanValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/setting/impl/MultiBooleanComponent.class */
public class MultiBooleanComponent extends Component {
    private final MultiBooleanValue setting;
    private final Map<BoolValue, EaseOutSine> select = new HashMap();

    public MultiBooleanComponent(MultiBooleanValue setting) {
        this.setting = setting;
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 8.0f;
        float heightoff = 0.0f;
        RoundedUtil.drawRound(getX() + 8.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, getWidth() - 5.0f, 0.0f, 4.0f, new Color(128, 128, 128));
        FontManager.Bold.get(15.0f).drawString(this.setting.getName(), getX() + 4.0f, getY(), -1);
        for (BoolValue boolValue : this.setting.getValues()) {
            float off = FontManager.Bold.get(13.0f).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5.0f) {
                offset = 8.0f;
                heightoff += FontManager.Bold.get(13.0f).getHeight() + 2;
            }
            this.select.putIfAbsent(boolValue, new EaseOutSine(250, 1.0d));
            this.select.get(boolValue).setDirection(boolValue.get().booleanValue() ? Direction.FORWARDS : Direction.BACKWARDS);
            FontManager.Bold.get(13.0f).drawString(boolValue.getName(), getX() + offset, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f + heightoff, ColorUtil.interpolateColor2(InterFace.mainColor.get().brighter(), InterFace.mainColor.get().brighter().brighter(), this.select.get(boolValue).getOutput().floatValue()));
            offset += off;
        }
        setHeight(FontManager.Bold.get(15.0f).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 8.0f;
        float heightoff = 0.0f;
        for (BoolValue boolValue : this.setting.getValues()) {
            float off = FontManager.Bold.get(13.0f).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5.0f) {
                offset = 8.0f;
                heightoff += FontManager.Bold.get(13.0f).getHeight() + 2;
            }
            if (RenderUtil.isHovering(getX() + offset, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f + heightoff, FontManager.Bold.get(13.0f).getStringWidth(boolValue.getName()), FontManager.Bold.get(13.0f).getHeight(), mouseX, mouseY) && mouse == 0) {
                boolValue.set(Boolean.valueOf(!boolValue.get().booleanValue()));
            }
            offset += off;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
