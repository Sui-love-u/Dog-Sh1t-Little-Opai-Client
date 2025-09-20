package qwq.arcane.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import qwq.arcane.Client;
import qwq.arcane.gui.alt.GuiAccountManager;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.animations.impl.LayeringAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/MainMenu.class */
public class MainMenu extends GuiScreen {
    private static Animation progress4Anim;
    List<Button> buttons = Arrays.asList(new Button("Single Player", "B"), new Button("Multi Player", "E"), new Button("Alt Manager", "D"), new Button("Options", "O"), new Button("Shut down", "R"));
    List<Button2> buttons2 = Arrays.asList(new Button2("Discord", "V"), new Button2("Kook", "W"), new Button2("Bilibili", "X"), new Button2("YouTube", "Y"), new Button2("Shop", "Z"));
    private Animation fadeInAnimation = new DecelerateAnimation(1000, 1.0d).setDirection(Direction.FORWARDS);
    int alpha = 0;

    @Override // net.minecraft.client.gui.GuiScreen
    public void initGui() {
        progress4Anim = new DecelerateAnimation(5000, 1.0d).setDirection(Direction.BACKWARDS);
        if (mc.gameSettings.guiScale != 2) {
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawImage(new ResourceLocation("nothing/background.png"), 0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());

        RoundedUtil.drawRound(this.width - 200, this.height - 50, 180.0f, 30.0f, 5.0f, new Color(35, 37, 43, 150));
        RoundedUtil.drawRound(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight(), 0.0f, new Color(0, 0, 0, 50));
        FontManager.Icon.get(100.0f).drawString("I", ((sr.getScaledWidth() / 2) - (FontManager.Icon.get(100.0f).getStringWidth("I") / 2)) + 8, (sr.getScaledHeight() / 2) - 130, new Color(109, 213, 250).getRGB());
        FontManager.Bold.get(50.0f).drawCenteredString(Client.name, sr.getScaledWidth() / 2, (sr.getScaledHeight() / 2) - 90, new Color(109, 213, 250).getRGB());
        RoundedUtil.drawRound((sr.getScaledWidth() / 2) - 130, sr.getScaledHeight() - 45, 260.0f, 40.0f, 6.0f, new Color(0, 0, 0, 120));
        FontManager.Bold.get(18.0f).drawCenteredString(Client.name + " " + Client.version, sr.getScaledWidth() / 2, sr.getScaledHeight() - 38, new Color(255, 255, 255).getRGB());
        FontManager.Bold.get(18.0f).drawCenteredString("OptiFine_1.8.9_HD_U_M6_pre2", sr.getScaledWidth() / 2, sr.getScaledHeight() - 28, new Color(255, 255, 255).getRGB());
        FontManager.Bold.get(18.0f).drawCenteredString("Made by Guyuemang", sr.getScaledWidth() / 2, sr.getScaledHeight() - 18, new Color(255, 255, 255).getRGB());
        RoundedUtil.drawRound((sr.getScaledWidth() / 2) - 80, (sr.getScaledHeight() / 2) - 55, 160.0f, 175.0f, 14.0f, new Color(0, 0, 0, 120));
        float count = 0.0f;
        for (Button button : this.buttons) {
            button.x = sr.getScaledWidth() / 2;
            button.y = ((sr.getScaledHeight() / 2) - 40) + count;
            button.width = 160.0f;
            button.height = 35.0f;
            button.clickAction = () -> {
                switch (button.name) {
                    case "Single Player":
                        LayeringAnimation.play(new GuiSelectWorld(this));
                        break;
                    case "Multi Player":
                        LayeringAnimation.play(new GuiMultiplayer(this));
                        break;
                    case "Alt Manager":
                        LayeringAnimation.play(new GuiAccountManager(this));
                        break;
                    case "Options":
                        LayeringAnimation.play(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Shut down":
                        mc.shutdown();
                        break;
                }
            };
            count += 35.0f;
            button.drawScreen(mouseX, mouseY);
        }
        float count2 = 0.0f;
        for (Button2 buttons2 : this.buttons2) {
            buttons2.x = (this.width - 200) + count2;
            buttons2.y = this.height - 42;
            buttons2.width = FontManager.Icon.get(40.0f).getStringWidth(buttons2.icon);
            buttons2.height = 15.0f;
            buttons2.clickAction = () -> {
                switch (buttons2.name) {
                    case "Discord":
                        try {
                            URI uri = new URI("https://discord.gg/yh3nqbnupB");
                            try {
                                Desktop.getDesktop().browse(uri);
                                return;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (URISyntaxException e2) {
                            throw new RuntimeException(e2);
                        }
                    case "Kook":
                        try {
                            URI uri2 = new URI("https://kook.vip/Adb58B");
                            try {
                                Desktop.getDesktop().browse(uri2);
                                return;
                            } catch (IOException e3) {
                                throw new RuntimeException(e3);
                            }
                        } catch (URISyntaxException e4) {
                            throw new RuntimeException(e4);
                        }
                    case "Bilibili":
                        try {
                            URI uri3 = new URI("https://space.bilibili.com/1068486349?spm_id_from=333.1007.0.0");
                            try {
                                Desktop.getDesktop().browse(uri3);
                                return;
                            } catch (IOException e5) {
                                throw new RuntimeException(e5);
                            }
                        } catch (URISyntaxException e6) {
                            throw new RuntimeException(e6);
                        }
                    case "YouTube":
                        try {
                            URI uri4 = new URI("https://www.youtube.com/channel/UCZCkwH9GU1u-t34jma9XrlA");
                            try {
                                Desktop.getDesktop().browse(uri4);
                                return;
                            } catch (IOException e7) {
                                throw new RuntimeException(e7);
                            }
                        } catch (URISyntaxException e8) {
                            throw new RuntimeException(e8);
                        }
                    case "Shop":
                        try {
                            URI uri5 = new URI("https://kw.atrishop.top/item?id=210");
                            try {
                                Desktop.getDesktop().browse(uri5);
                                return;
                            } catch (IOException e9) {
                                throw new RuntimeException(e9);
                            }
                        } catch (URISyntaxException e10) {
                            throw new RuntimeException(e10);
                        }
                    default:
                        return;
                }
            };
            count2 += FontManager.Icon.get(40.0f).getStringWidth(buttons2.icon) + 15;
            buttons2.drawScreen(mouseX, mouseY);
        }
        float progress = this.fadeInAnimation.getOutput().floatValue();
        this.alpha = (int) (255.0f * (1.0f - progress));
        RenderUtil.drawRect(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, this.alpha).getRGB());
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.buttons.forEach(button -> {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        });
        this.buttons2.forEach(button2 -> {
            button2.mouseClicked(mouseX, mouseY, mouseButton);
        });
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/gui/MainMenu$Button.class */
    class Button {
        String name;
        String icon;
        public float x;
        public float y;
        public float width;
        public float height;
        public Runnable clickAction;
        private Animation hoverAnimation = new DecelerateAnimation(1000, 1.0d);

        public Button(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }

        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = RenderUtil.isHovering(this.x - 80.0f, this.y - 15.0f, this.width, 35.0f, mouseX, mouseY);
            Color rectColor = new Color(35, 37, 43, 100);
            Color rectColor2 = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f), this.hoverAnimation.getOutput().floatValue());
            this.hoverAnimation.setDirection(hovered ? Direction.BACKWARDS : Direction.FORWARDS);
            if (hovered) {
                RoundedUtil.drawRound(this.x - 80.0f, this.y - 15.0f, this.width, 35.0f, 14.0f, rectColor2);
            }
            FontManager.Bold.get(20.0f).drawCenteredString(this.name, this.x + 5.0f, this.y, new Color(89, 139, 184).getRGB());
            FontManager.Icon.get(30.0f).drawCenteredString(this.icon, (this.x - 5.0f) - (FontManager.Bold.get(20.0f).getStringWidth(this.name) / 2), this.y, new Color(89, 139, 184).getRGB());
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(this.x - 80.0f, this.y - 15.0f, this.width, 35.0f, mouseX, mouseY);
            if (hovered) {
                this.clickAction.run();
            }
        }
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/gui/MainMenu$Button2.class */
    class Button2 {
        String name;
        String icon;
        public float x;
        public float y;
        public float width;
        public float height;
        public Runnable clickAction;
        private Animation hoverAnimation = new DecelerateAnimation(1000, 1.0d);

        public Button2(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }

        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = RenderUtil.isHovering(((this.x + 11.0f) - (FontManager.Icon.get(40.0f).getStringWidth(this.icon) / 2)) + (this.width / 2.0f), this.y - 1.0f, this.width, this.height, mouseX, mouseY);
            Color rectColor = new Color(35, 37, 43, 150);
            Color rectColor2 = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f), this.hoverAnimation.getOutput().floatValue());
            this.hoverAnimation.setDirection(hovered ? Direction.BACKWARDS : Direction.FORWARDS);
            if (hovered) {
                RoundedUtil.drawRound(((this.x + 11.0f) - (FontManager.Icon.get(40.0f).getStringWidth(this.icon) / 2)) + (this.width / 2.0f), this.y - 1.0f, this.width, this.height, 3.0f, rectColor2);
            }
            if (hovered) {
                FontManager.Semibold.get(20.0f).drawCenteredString(this.name, this.x + 13.0f + (this.width / 2.0f), this.y - 15.0f, rectColor2.brighter().brighter().brighter().brighter().getRGB());
            }
            FontManager.Icon.get(40.0f).drawString(this.icon, this.x + 13.0f, this.y, new Color(-1).getRGB());
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(((this.x + 11.0f) - (FontManager.Icon.get(40.0f).getStringWidth(this.icon) / 2)) + (this.width / 2.0f), this.y - 1.0f, this.width, this.height, mouseX, mouseY);
            if (hovered) {
                this.clickAction.run();
            }
        }
    }
}
