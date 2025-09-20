package com.amaya.gui.mainmenu;

import com.amaya.Amaya;
import com.amaya.gui.altlogin.GuiAltManager;
import com.amaya.gui.splash.SplashScreen;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.animations.impl.LayeringAnimation;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import com.amaya.utils.render.menu.VideoPlayer;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/4/22
 */
public class MainMenu extends GuiScreen {
    List<Button> buttons = Arrays.asList(
            new Button("Single Player","B"),
            new Button("Multi Player","C"),
            new Button("Alt Manager","D"),
            new Button("Options","E"),
            new Button("Shutdown","F")
    );
    public static Animation animation = new DecelerateAnimation(300, 1);

    private float currentX = 0f;
    private float currentY = 0f;

    @Override
    public void initGui() {
        if (SplashScreen.menu) {
            RenderUtil.showNotification("Amaya INFO","欢迎使用Amaya客户端！");
        }
        if (mc.gameSettings.guiScale != 2) {
            Amaya.prevGuiScale = mc.gameSettings.guiScale;
            Amaya.updateGuiScale = true;
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        RoundedUtil.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, new Color(145, 145, 145));
        float xDiff = ((mouseX - sr.getScaledHeight() / 2f) - this.currentX) / sr.getScaleFactor();
        float yDiff = ((mouseY - sr.getScaledWidth() / 2f) - this.currentY) / sr.getScaleFactor();
        this.currentX += xDiff * 0.3f;
        this.currentY += yDiff * 0.3f;

        GlStateManager.translate(this.currentX / 150, this.currentY / 30, 0.0f);
        try {
            VideoPlayer.render(-5, -5, sr.getScaledWidth() + 10, sr.getScaledHeight() + 10);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        GlStateManager.translate(-this.currentX / 150, -this.currentY / 30, 0.0f);
        FontManager.ICON.get(100).drawCenteredString("A", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 130, new Color(200, 255, 255).getRGB());
        FontManager.SEMIBOLD.get(40).drawCenteredString("Amaya", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 90, new Color(200, 255, 255).getRGB());

        //按钮专属背景
        RoundedUtil.drawRound(sr.getScaledWidth() / 2 - 80, sr.getScaledHeight() / 2 - 55, 160, 175, 14, new Color(0, 0, 0, 120));

        float count = 0;
        if (SplashScreen.menu == false) {
            for (Button button : buttons) {
                button.x = sr.getScaledWidth() / 2;
                button.y = sr.getScaledHeight() / 2 - 40 + count;
                button.width = 160;
                button.height = 35;
                button.clickAction = () -> {
                    switch (button.name) {
                        case "Single Player": {
                            LayeringAnimation.play(new GuiSelectWorld(this));
                        }
                        break;
                        case "Multi Player": {
                            LayeringAnimation.play(new GuiMultiplayer(this));
                        }
                        break;
                        case "Alt Manager": {
                            LayeringAnimation.play(new GuiAltManager(this));
                        }
                        break;
                        case "Options": {
                            LayeringAnimation.play(new GuiOptions(this, mc.gameSettings));
                        }
                        break;
                        case "Shutdown": {
                            mc.shutdown();
                        }
                        break;
                    }
                };
                count += 35;
                button.drawScreen(mouseX, mouseY);
            }
        }
        if (SplashScreen.menu) {
            boolean hovered = RenderUtil.isHovering(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), mouseX, mouseY) && Mouse.isButtonDown(0);
            RoundedUtil.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, new Color(241, 244, 243));
            FontManager.ICON.get(100).drawCenteredString("A", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 130, new Color(89, 165, 245).getRGB());
            FontManager.REGULAR.get(26).drawCenteredString("Welcome to Amaya Client", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, new Color(92, 92, 92).getRGB());
            FontManager.REGULAR.get(18).drawCenteredString("Click anywhere to close", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 20, new Color(92, 92, 92).getRGB());
            if (hovered) {
                animation.setDirection(Direction.BACKWARDS);
                SplashScreen.menu = false;
            }
        } else {
            RenderUtil.drawRect(0, 0, sr.getScaledWidth() * animation.getOutput().floatValue(), sr.getScaledHeight(), new Color(241, 244, 243).getRGB());
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> {button.mouseClicked(mouseX, mouseY, mouseButton);});
    }

    class Button {
        String name;
        String icon;
        public float x, y, width, height;
        public Runnable clickAction;
        private Animation hoverAnimation = new DecelerateAnimation(1000, 1);;

        public Button(String name,String icon){
            this.name = name;
            this.icon = icon;
        }

        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = RenderUtil.isHovering(x - 80,y - 15,width,35, mouseX, mouseY);
            Color rectColor = new Color(35, 37, 43, 100);
            rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f),this.hoverAnimation.getOutput().floatValue());
            hoverAnimation.setDirection(hovered ? Direction.BACKWARDS : Direction.FORWARDS);
            if (hovered) {
                RoundedUtil.drawRound(x - 80, y - 15, width, 35, 14, rectColor);
            }
            FontManager.SEMIBOLD.get(20).drawCenteredString(name,x + 5,y,new Color(200, 255, 255).getRGB());
            FontManager.ICON.get(30).drawCenteredString(icon,x - 5 - FontManager.SEMIBOLD.get(20).getStringWidth(name) / 2  ,y,new Color(200, 255, 255).getRGB());
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(x - 80,y - 15,width,35, mouseX, mouseY);
            if (hovered) clickAction.run();
        }
    }
}
