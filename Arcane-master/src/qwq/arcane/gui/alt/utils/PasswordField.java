package qwq.arcane.gui.alt.utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/utils/PasswordField.class */
public final class PasswordField extends GuiTextField {
    public PasswordField(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x, y, width, height);
    }

    @Override // net.minecraft.client.gui.GuiTextField
    public void drawTextBox() {
        String s = getText();
        setText(getText());
        super.drawTextBox();
        setText(s);
    }
}
