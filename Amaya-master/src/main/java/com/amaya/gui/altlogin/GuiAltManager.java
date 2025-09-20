package com.amaya.gui.altlogin;

import com.amaya.gui.altlogin.util.MicrosoftLoginHandler;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.menu.VideoPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import com.amaya.gui.altlogin.util.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author KuChaZi
 * @Date 2024/6/30 22:03
 * @ClassName: GuiAltManager
 */
public final class GuiAltManager extends GuiScreen {
    private final GuiScreen parentScreen;
    private final ResourceLocation backgroundResource = new ResourceLocation("bloodline/gs1.png");
    private GuiTextField username;
    private final Button loginButton = new Button("CrackLogin", 0, 0, 0, 0);
    private final Button microsoftButton = new Button("Microsoft", 0, 0, 0, 0);
    private final Button randomButton = new Button("RandomName", 0, 0, 0, 0);
    private final Button backButton = new Button("Back", 0, 0, 0, 0);

    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        username = new GuiTextField(0, mc.fontRendererObj, 0, 0, 148, 20);
        username.setText("");
        super.initGui();
    }

    public List<String> logs = new ArrayList<>();

    private final MicrosoftLoginHandler microsoftLoginHandler = new MicrosoftLoginHandler();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        int tW = 180;
        int tH = 24;
        int hxX = this.width / 2 - tW / 2;
        int hxY = this.height / 2 - tH / 2 - 48;
        try {
            VideoPlayer.render(-5, -5, sr.getScaledWidth() + 10, sr.getScaledHeight() + 10);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        FontManager.REGULAR.get(30).drawCenteredString("Alt Manager", this.width / 2F, hxY - 8, Color.WHITE.getRGB());
        FontManager.REGULAR.get(20).drawCenteredString(EnumChatFormatting.YELLOW + "Username: " + EnumChatFormatting.RESET + mc.session.getUsername(),this.width / 2F, hxY + 170, Color.WHITE.getRGB());
        username.drawTextBox();
        username.xPosition = hxX;
        username.yPosition = hxY + 24;
        username.width = tW;
        username.height = tH;
        if (!username.isFocused() && username.getText().isEmpty()) {
            mc.fontRendererObj.drawStringWithShadow("Username", hxX + 4, hxY + 32, new Color(160,160,160).getRGB());
        }
        loginButton.drawButton(mouseX, mouseY);
        loginButton.update(hxX, hxY + tH * 2 + 4, tW, tH);
        microsoftButton.drawButton(mouseX, mouseY);
        microsoftButton.update(hxX, hxY + tH * 3 + 6, tW, tH);
        randomButton.drawButton(mouseX, mouseY);
        randomButton.update(hxX, hxY + tH * 4 + 8, tW, tH);
        backButton.drawButton(mouseX, mouseY);
        backButton.update(hxX, hxY + tH * 5 + 10, tW, tH);
        backButton.displayName = microsoftLoginHandler.logging ? EnumChatFormatting.RED + "Stop" : "Back";
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (microsoftLoginHandler.logging) {
                microsoftLoginHandler.stop();
                return;
            }
        }
        username.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        username.mouseClicked(mouseX, mouseY, mouseButton);
        backButton.clicked(mouseButton, () -> {
            if (microsoftLoginHandler.logging) {
                microsoftLoginHandler.stop();
            } else {
                mc.displayGuiScreen(parentScreen);
            }
        });
        if (microsoftLoginHandler.logging) {
            return;
        }
        loginButton.clicked(mouseButton, () -> {
            if (!username.getText().isEmpty()) {
                mc.session = new Session(username.getText(), "", "", "legacy");
            }
        });
        microsoftButton.clicked(mouseButton, () -> microsoftLoginHandler.start(this));
        randomButton.clicked(mouseButton, () -> {
            mc.session = new Session(randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_", 10), "", "", "legacy");
        });
    }

    public static String randomString(String pool, int length) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(pool.charAt(ThreadLocalRandom.current().nextInt(0, pool.length())));
        }
        return builder.toString();
    }

}
