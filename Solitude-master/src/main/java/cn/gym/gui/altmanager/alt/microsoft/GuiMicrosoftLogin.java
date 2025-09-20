package cn.gym.gui.altmanager.alt.microsoft;

import cn.gym.gui.mainmenu.VideoPlayer;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RoundedUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.*;
import java.io.IOException;

public final class GuiMicrosoftLogin extends GuiScreen {
    private volatile MicrosoftLogin microsoftLogin;
    private volatile boolean closed = false;

    private final GuiScreen parentScreen;

    public GuiMicrosoftLogin(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;

        final Thread thread = new Thread("MicrosoftLogin Thread") {
            @Override
            public void run() {
                try {
                    microsoftLogin = new MicrosoftLogin();

                    while (!closed) {
                        if (microsoftLogin.logged) {
                            IOUtils.closeQuietly(microsoftLogin);

                            closed = true;

                            microsoftLogin.setStatus("Login successful! " + microsoftLogin.getUserName());

                            mc.session = new Session(microsoftLogin.getUserName(), microsoftLogin.getUuid(), microsoftLogin.getAccessToken(), "mojang");

                            break;
                        }
                    }
                } catch (Throwable e) {
                    closed = true;

                    e.printStackTrace();

                    IOUtils.closeQuietly(microsoftLogin);

                    microsoftLogin.setStatus("Login failed! " + e.getClass().getName() + ":" + e.getMessage());
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 0) {
            if (microsoftLogin != null && !closed) {
                microsoftLogin.close();
                closed = true;
                IOUtils.closeQuietly(microsoftLogin);
            }

            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        //buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 50, "Back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            VideoPlayer.render(0, 0, width, height);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        // 使用RoundedUtil绘制半透明背景
        RoundedUtil.drawRound(0, 0, width, height, 0, new Color(0, 0, 0, 100));

        // 绘制自定义按钮
        int buttonX = width / 2 - 100;
        int buttonY = height / 2 + 50;
        boolean isHovered = mouseX >= buttonX && mouseX <= buttonX + 200 &&
                mouseY >= buttonY && mouseY <= buttonY + 20;

        // 按钮背景
        RoundedUtil.drawRound(
                buttonX, buttonY, 200, 20, 5,
                isHovered ? new Color(100, 100, 100, 200) : new Color(70, 70, 70, 200)
        );

        // 按钮边框
        RoundedUtil.drawRoundOutline(
                buttonX, buttonY, 200, 20, 5, 1,
                new Color(0, 0, 0, 0),
                isHovered ? new Color(180, 180, 180, 255) : new Color(120, 120, 120, 255)
        );

        // 按钮文字
        FontManager.Semibold.get(18).drawCenteredString(
                "Back",
                width / 2.0f,
                buttonY + 6,
                -1
        );

        // 其他绘制内容
        if (microsoftLogin == null) {
            FontManager.Semibold.get(20).drawCenteredStringWithShadow(
                    "Logging in...",
                    width / 2.0f,
                    height / 2.0f - 5f,
                    -1
            );
        } else {
            FontManager.Semibold.get(20).drawCenteredStringWithShadow(
                    microsoftLogin.getStatus(),
                    width / 2.0f,
                    height / 2.0f - 5f,
                    -1
            );
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int buttonX = width / 2 - 100;
        int buttonY = height / 2 + 50;
        if (mouseX >= buttonX && mouseX <= buttonX + 200 &&
                mouseY >= buttonY && mouseY <= buttonY + 20) {

            if (microsoftLogin != null && !closed) {
                microsoftLogin.close();
                closed = true;
                IOUtils.closeQuietly(microsoftLogin);
            }
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void onGuiClosed() {
        if (microsoftLogin != null) {
            microsoftLogin.close();
            closed = true;
            IOUtils.closeQuietly(microsoftLogin);
        }
        super.onGuiClosed();
    }
}