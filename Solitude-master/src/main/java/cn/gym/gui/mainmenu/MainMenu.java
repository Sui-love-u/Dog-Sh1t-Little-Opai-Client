package cn.gym.gui.mainmenu;

import cn.gym.Solitude;
import cn.gym.gui.altmanager.alt.GuiToken;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.animations.impl.LayeringAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import net.minecraft.client.gui.*;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 22:25
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

    @Override
    public void initGui() {
        if (mc.gameSettings.guiScale != 2) {
            Solitude.prevGuiScale = mc.gameSettings.guiScale;
            Solitude.updateGuiScale = true;
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        try {
            VideoPlayer.render(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        RoundedUtil.drawRound(sr.getScaledWidth() / 2 - 80, sr.getScaledHeight() / 2 - 55, 160, 175, 14, new Color(255, 255, 255,100));

        FontManager.ICON.get(100).drawCenteredString("A", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 130, new Color(35, 37, 38).getRGB());
        FontManager.Semibold.get(40).drawCenteredString(Solitude.Name, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 90, new Color(35, 37, 38).getRGB());

        RoundedUtil.drawRound(sr.getScaledWidth() / 2 - 130, sr.getScaledHeight() - 47, 260, 40, 6, new Color(221, 228, 255, 100));
        FontManager.Semibold.get(18).drawCenteredString(Solitude.Name + " " + Solitude.Version, sr.getScaledWidth() / 2, sr.getScaledHeight() - 40, new Color(35, 37, 38).getRGB());
        FontManager.Semibold.get(18).drawCenteredString("OptiFine_1.8.9_HD_U_M6_pre2", sr.getScaledWidth() / 2, sr.getScaledHeight() - 30, new Color(35, 37, 38).getRGB());
        FontManager.Semibold.get(18).drawCenteredString("Made by Guyuemang", sr.getScaledWidth() / 2, sr.getScaledHeight() - 20, new Color(35, 37, 38).getRGB());

        float count = 0;
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
                        LayeringAnimation.play(new GuiToken(this));
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> {button.mouseClicked(mouseX, mouseY, mouseButton);});
    }

    @Override
    public void onGuiClosed() {
        if (Solitude.updateGuiScale) {
            mc.gameSettings.guiScale = Solitude.prevGuiScale;
            Solitude.updateGuiScale = false;
        }
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
            FontManager.Semibold.get(20).drawCenteredString(name,x + 5,y,new Color(35, 37, 38).getRGB());
            FontManager.ICON.get(30).drawCenteredString(icon,x - 5 - FontManager.Semibold.get(20).getStringWidth(name) / 2  ,y,new Color(35, 37, 38).getRGB());
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(x - 80,y - 15,width,35, mouseX, mouseY);
            if (hovered) clickAction.run();
        }
    }
}
