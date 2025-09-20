package qwq.arcane.gui.alt.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import qwq.arcane.gui.alt.utils.AltLoginThread;
import qwq.arcane.gui.alt.utils.PasswordField;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/gui/GuiAltCracked.class */
public final class GuiAltCracked extends GuiScreen {
    private PasswordField password;
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    private GuiTextField username;

    public GuiAltCracked(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
                break;
            case 1:
                mc.displayGuiScreen(this.previousScreen);
                break;
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int x2, int y2, float partialTicks) {
        drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        drawCenteredString(mc.fontRendererObj, "Alt Login", this.width / 2, 20, -1);
        drawCenteredString(mc.fontRendererObj, this.thread == null ? EnumChatFormatting.GRAY + "Idle..." : this.thread.getStatus(), this.width / 2, 29, -1);
        if (this.username.getText().isEmpty()) {
            drawString(mc.fontRendererObj, "Username / E-Mail", (this.width / 2) - 96, 66, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            drawString(mc.fontRendererObj, "Password", (this.width / 2) - 96, 106, -7829368);
        }
        super.drawScreen(x2, y2, partialTicks);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void initGui() {
        int y = (this.height / 4) + 24;
        this.buttonList.add(new GuiButton(0, (this.width / 2) - 100, y + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, (this.width / 2) - 100, y + 72 + 12 + 24, "Back"));
        this.username = new GuiTextField(y, mc.fontRendererObj, (this.width / 2) - 100, 60, 200, 20);
        this.password = new PasswordField(20, mc.fontRendererObj, (this.width / 2) - 100, 100, 200, 20);
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
        }
        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }
        if (character == '\r') {
            actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        } catch (IOException e) {
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}
