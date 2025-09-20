package qwq.arcane.gui.clickgui.dropdown.setting.impl;

import java.awt.Color;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/setting/impl/ModeComponent.class */
public class ModeComponent extends Component {
    private final ModeValue setting;

    public ModeComponent(ModeValue setting) {
        this.setting = setting;
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0.0f;
        float heightoff = 0.0f;
        RoundedUtil.drawRound(getX() + 0.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, getWidth() - 5.0f, 0.0f, 4.0f, new Color(50, 50, 108, 200));
        FontManager.Bold.get(15.0f).drawString(this.setting.getName(), getX() + 4.0f, getY(), -1);
        for (String text : this.setting.getModes()) {
            float off = FontManager.Bold.get(13.0f).getStringWidth(text) + 2;
            if (offset + off >= getWidth() - 5.0f) {
                offset = 0.0f;
                heightoff += 8.0f;
            }
            if (text.equals(this.setting.get())) {
                FontManager.Bold.get(13.0f).drawString(text, getX() + offset + 8.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + heightoff, InterFace.mainColor.get().brighter().brighter().getRGB());
            } else {
                FontManager.Bold.get(13.0f).drawString(text, getX() + offset + 8.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + heightoff, InterFace.mainColor.get().brighter().getRGB());
            }
            offset += off;
        }
        setHeight(FontManager.Bold.get(15.0f).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 0.0f;
        float heightoff = 0.0f;
        for (String text : this.setting.getModes()) {
            float off = FontManager.Bold.get(13.0f).getStringWidth(text) + 2;
            if (offset + off >= getWidth() - 5.0f) {
                offset = 0.0f;
                heightoff += 8.0f;
            }
            if (RenderUtil.isHovering(getX() + offset + 8.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + heightoff, FontManager.Bold.get(13.0f).getStringWidth(text), FontManager.Bold.get(13.0f).getHeight(), mouseX, mouseY) && mouse == 0) {
                this.setting.set(text);
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
