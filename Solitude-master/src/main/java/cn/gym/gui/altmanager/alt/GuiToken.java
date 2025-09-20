package cn.gym.gui.altmanager.alt;

import cn.gym.Solitude;
import cn.gym.gui.altmanager.alt.microsoft.GuiMicrosoftLogin;
import cn.gym.gui.altmanager.alt.microsoft.MicrosoftLogin;
import cn.gym.gui.mainmenu.MainMenu;
import cn.gym.gui.mainmenu.VideoPlayer;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.animations.impl.LayeringAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GuiToken extends GuiScreen {
    private String status = null;
    private GuiButton cancelButton = null;
    private final GuiScreen previousScreen;
    private GuiTextField sessionField = null;
    List<GuiToken.Button> buttons = Arrays.asList(
            new GuiToken.Button("Token Login"),
            new GuiToken.Button("Microsoft Login"),
            new GuiToken.Button("Back")
    );

    public static Animation animation = new DecelerateAnimation(300, 1);

    public GuiToken(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        sessionField = new GuiTextField(1, fontRendererObj, width / 2 - 70, height / 2 - 40, 140, 20);
        sessionField.setMaxStringLength(32767);
        sessionField.setFocused(true);

    }
    private volatile MicrosoftLogin microsoftLogin;
    private volatile boolean closed = false;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            VideoPlayer.render(0, 0, width, height);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        ScaledResolution sr = new ScaledResolution(mc);

        RoundedUtil.drawRound(sr.getScaledWidth() / 2 - 80, sr.getScaledHeight() / 2 - 55, 160, 145, 14, new Color(255, 255, 255,100));
        FontManager.ICON.get(100).drawCenteredString("A", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 130, new Color(35, 37, 38).getRGB());
        FontManager.Semibold.get(40).drawCenteredString("AltManager", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 90, new Color(35, 37, 38).getRGB());

        float count = 0;
        for (GuiToken.Button button : buttons) {
            button.x = sr.getScaledWidth() / 2;
            button.y = sr.getScaledHeight() / 2 + count;
            button.width = 160;
            button.height = 35;
            button.clickAction = () -> {
                switch (button.name) {
                    case "Token Login": {
                        try {
                            String token = sessionField.getText();
                            String[] playerInfo = getProfileInfo(token);
                            SessionManager.setSession(new Session(playerInfo[0], playerInfo[1], token, "mojang"));
                            status = "§2Logged in as " + playerInfo[0];
                        } catch (Exception e) {
                            status = "§4Invalid token";
                        }
                    }
                    break;
                    case "Microsoft Login": {
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
                    break;
                    case "Back": {
                        LayeringAnimation.play(new MainMenu());
                    }
                    break;
                }
            };
            if (microsoftLogin == null) {
                FontManager.Semibold.get(20).drawCenteredString(
                        "MicrosoftLogin:Null",
                        width / 2.0f,
                        height / 2.0f - 55,
                        new Color(35, 37, 38).getRGB()
                );
            } else {
                FontManager.Semibold.get(20).drawCenteredString(
                        microsoftLogin.getStatus(),
                        width / 2.0f,
                        height / 2.0f - 55,
                        new Color(35, 37, 38).getRGB()
                );
            }
            count += 35;
            button.drawScreen(mouseX, mouseY);
        }
        sessionField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (status != null) {
            FontManager.Bold.get(18).drawCenteredString(status, (float) width / 2, (float) height / 2 - 15, -1);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> {button.mouseClicked(mouseX, mouseY, mouseButton);});
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        sessionField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_ESCAPE) {
            actionPerformed(cancelButton);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button != null && button.id == 0) {
            if (microsoftLogin != null && !closed) {
                microsoftLogin.close();
                closed = true;
                IOUtils.closeQuietly(microsoftLogin);
            }
            mc.displayGuiScreen(null);
        }
        if (button != null && button.id == 2) {
            mc.displayGuiScreen(new GuiMicrosoftLogin(this));
        }
        if (button != null && button.id == 1) {
            try {
                String token = sessionField.getText();
                String[] playerInfo = getProfileInfo(token);
                SessionManager.setSession(new Session(playerInfo[0], playerInfo[1], token, "mojang"));
                status = "§2Logged in as " + playerInfo[0];
            } catch (Exception e) {
                status = "§4Invalid token";
            }
        }
    }

    public static String[] getProfileInfo(String token) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
        request.setHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse response = client.execute(request);
        String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        String IGN = jsonObject.get("name").getAsString();
        String UUID = jsonObject.get("id").getAsString();
        return new String[]{IGN, UUID};
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
    class Button {
        String name;
        String icon;
        public float x, y, width, height;
        public Runnable clickAction;
        private Animation hoverAnimation = new DecelerateAnimation(1000, 1);;

        public Button(String name){
            this.name = name;
        }
        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = RenderUtil.isHovering(x - 80,y - 15,width,35, mouseX, mouseY);
            Color rectColor = new Color(35, 37, 43, 100);
            rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f),this.hoverAnimation.getOutput().floatValue());
            hoverAnimation.setDirection(hovered ? Direction.BACKWARDS : Direction.FORWARDS);
            if (hovered) {
                RoundedUtil.drawRound(x - 80, y - 15, width, 35, 14, rectColor);
            }
            FontManager.Semibold.get(20).drawCenteredString(name,x,y,new Color(35, 37, 38).getRGB());
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(x - 80,y - 15,width,35, mouseX, mouseY);
            if (hovered) clickAction.run();
        }
    }
}