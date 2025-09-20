package cn.gym.module.impl.display;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.packet.PacketReceiveEvent;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.Category;
import cn.gym.module.ModuleWidget;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.ModeValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 11:22
 */
public class Session extends ModuleWidget {
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal","Custom","Solitude"});

    public Session() {
        super("Session",Category.Display);
        this.width = 100;
        this.height = 50;
    }
    public int lost = 0, killed = 0, won = 0;

    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof S02PacketChat) {
            S02PacketChat s02 = (S02PacketChat) event.getPacket();
            String xd = s02.getChatComponent().getUnformattedText();
            if (xd.contains("was killed by " + mc.thePlayer.getName())) {
                ++this.killed;
            }

            if (xd.contains("You Died! Want to play again?")) {
                ++lost;
            }
        }

        if (packet instanceof S45PacketTitle && ((S45PacketTitle) packet).getType().equals(S45PacketTitle.Type.TITLE)) {
            String unformattedText = ((S45PacketTitle) packet).getMessage().getUnformattedText();
            if (unformattedText.contains("VICTORY!")) {
                ++this.won;
            }
            if (unformattedText.contains("GAME OVER!") || unformattedText.contains("DEFEAT!") || unformattedText.contains("YOU DIED!")) {
                ++this.lost;
            }
        }
    }

    @Override
    public void onShader(Shader2DEvent event) {
        int x = (int) renderX;
        int y = (int) renderY;
        switch (modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 52 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 65, new Color(255, 255, 255 ,255));
                break;
            case "Normal":
                RoundedUtil.drawRound(x, y, 48 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 65, Interface.radius.get().intValue(), new Color(0, 0, 0, 255));
                break;
            case "Custom":
                RenderUtil.drawRect(x, y, 130, 62, new Color(0, 0, 0, 255));
                break;
        }
    }

    Interface setting = Solitude.Instance.getModuleManager().getModule(Interface.class);
    private final DecimalFormat bpsFormat = new DecimalFormat("0.00");
    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;
        switch (modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 52 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 65, new Color(255, 255, 255 ,100));
                RenderUtil.drawRect(x, y, 52 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 15, new Color(255, 255, 255 ,100));
                RenderUtil.renderPlayer2D(mc.thePlayer, x + 5, y + 19, 35,0, -1);
                FontManager.Semibold.get(18).drawCenteredString("Session", x + (48 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime())) / 2, y + 5, -1);
                FontManager.Semibold.get(18).drawString("Played Time:" + RenderUtil.sessionTime(), x + 44, y + 21, -1);
                FontManager.Semibold.get(18).drawString("kill:" + killed, x + 44, y + 33, -1);
                FontManager.Semibold.get(18).drawString("win:" + won, x + 44, y + 45, -1);
                FontManager.Semibold.get(18).drawString("FPS:" + mc.getDebugFPS(), x + 82, y + 33, -1);
                FontManager.Semibold.get(18).drawString("BPS:" + bpsFormat.format(Interface.getBPS()), x + 82, y + 45, -1);
                RenderUtil.drawRect(x + 5, y + 57, 40 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 5, new Color(100, 100, 100,190));
                RenderUtil.drawRect(x + 5, y + 57, 40 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()) * MathHelper.clamp_float(mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth(), 0, 1), 5, new Color(255, 255, 255 ,100));
                break;
            case "Custom":
                RenderUtil.drawRect(x, y, 130, 62, new Color(0, 0, 0, 100));
                FontManager.Mc.get(20).drawString("Session", x + 5, y + 5, -1);
                FontManager.Mc.get(18).drawString("Played Time:" + RenderUtil.sessionTime(), x + 4, y + 27, -1);
                FontManager.Mc.get(18).drawString("kill:" + killed, x + 4, y + 39, -1);
                FontManager.Mc.get(18).drawString("FPS:" + mc.getDebugFPS(), x + 62, y + 39, -1);
                FontManager.Mc.get(18).drawString("BPS:" + bpsFormat.format(Interface.getBPS()), x + 62, y + 51, -1);
                FontManager.Mc.get(18).drawString("win:" + won, x + 4, y + 51, -1);
                break;
            case "Normal":
                RoundedUtil.drawRound(x, y, 48 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 65, Interface.radius.get().intValue(), new Color(0, 0, 0, 89));
                RenderUtil.startGlScissor(x - 2, y - 1, 190, 20);
                RoundedUtil.drawRound(x, y, 48 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 30, Interface.radius.get().intValue(), ColorUtil.applyOpacity(new Color(setting.color(1)), (float) 0.3f));
                RenderUtil.stopGlScissor();
                RenderUtil.renderPlayer2D(mc.thePlayer, x + 5, y + 25, 35, 12, -1);
                FontManager.Bold.get(20).drawString("Session", x + 5, y + 5, -1);
                FontManager.Semibold.get(18).drawString("Played Time:" + RenderUtil.sessionTime(), x + 44, y + 27, -1);
                FontManager.Semibold.get(18).drawString("kill:" + killed, x + 44, y + 39, -1);
                FontManager.Semibold.get(18).drawString("win:" + won, x + 44, y + 51, -1);
                break;
        }
        this.width = 48 + Semibold.get(18).getStringWidth("Played Time:" + RenderUtil.sessionTime());
        this.height = 65;
    }

    @Override
    public boolean shouldRender() {
        return getState() && setting.getState();
    }
}
