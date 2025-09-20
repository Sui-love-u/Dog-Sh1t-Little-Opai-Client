package qwq.arcane.module.impl.display;

import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.MathHelper;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.player.AttackEvent;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.fontrender.FontRenderer;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/Session.class */
public class Session extends ModuleWidget {
    public ModeValue modeValue;
    public int lost;
    public int killed;
    public int won;
    private final DecimalFormat bpsFormat;

    public Session() {
        super("Session", Category.Display);
        this.modeValue = new ModeValue("Mode", "Normal", new String[]{"Normal", "Custom", "Solitude"});
        this.lost = 0;
        this.killed = 0;
        this.won = 0;
        this.bpsFormat = new DecimalFormat("0.00");
        this.width = 100.0f;
        this.height = 50.0f;
    }

    @EventTarget
    public void onAttackEvent(AttackEvent event) {
        if (event.getTargetEntity().isDead) {
            this.killed++;
        }
    }

    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            S02PacketChat s02 = (S02PacketChat) event.getPacket();
            String xd = s02.getChatComponent().getUnformattedText();
            if (xd.contains("was killed by " + mc.thePlayer.getName())) {
                this.killed++;
            }
            if (xd.contains("You Died! Want to play again?")) {
                this.lost++;
            }
        }
        if ((packet instanceof S45PacketTitle) && ((S45PacketTitle) packet).getType().equals(S45PacketTitle.Type.TITLE)) {
            String unformattedText = ((S45PacketTitle) packet).getMessage().getUnformattedText();
            if (unformattedText.contains("VICTORY!")) {
                this.won++;
            }
            if (unformattedText.contains("GAME OVER!") || unformattedText.contains("DEFEAT!") || unformattedText.contains("YOU DIED!")) {
                this.lost++;
            }
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void onShader(Shader2DEvent event) {
        int x;
        int y;
        x = (int) this.renderX;
        y = (int) this.renderY;
        switch (this.modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 52 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 65.0f, new Color(255, 255, 255, 255));
                break;
            case "Normal":
                float stringWidth = 48 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime());
                InterFace interFace = INTERFACE;
                RoundedUtil.drawRound(x, y, stringWidth, 65.0f, InterFace.radius.get().intValue(), new Color(0, 0, 0, 255));
                break;
            case "Custom":
                RoundedUtil.drawRound(x, y, 130.0f, 56.0f, 6.0f, new Color(255, 255, 255, 255));
                break;
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void render() {
        int x;
        int y;
        x = (int) this.renderX;
        y = (int) this.renderY;
        switch (this.modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 52 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 65.0f, new Color(255, 255, 255, 100));
                RenderUtil.drawRect(x, y, 52 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 15.0f, new Color(255, 255, 255, 100));
                RenderUtil.renderPlayer2D(mc.thePlayer, x + 5, y + 19, 35.0f, 0.0f, -1);
                FontManager.Bold.get(18.0f).drawCenteredString("Session", x + ((48 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime())) / 2), y + 5, -1);
                FontManager.Bold.get(18.0f).drawString("Played Time:" + RenderUtil.sessionTime(), x + 44, y + 21, -1);
                FontManager.Bold.get(18.0f).drawString("kill:" + this.killed, x + 44, y + 33, -1);
                FontManager.Bold.get(18.0f).drawString("win:" + this.won, x + 44, y + 45, -1);
                FontRenderer fontRenderer = FontManager.Bold.get(18.0f);
                Mine mine = mc;
                fontRenderer.drawString("FPS:" + Mine.getDebugFPS(), x + 82, y + 33, -1);
                FontRenderer fontRenderer2 = FontManager.Bold.get(18.0f);
                DecimalFormat decimalFormat = this.bpsFormat;
                InterFace interFace = INTERFACE;
                fontRenderer2.drawString("BPS:" + decimalFormat.format(InterFace.getBPS()), x + 82, y + 45, -1);
                RenderUtil.drawRect(x + 5, y + 57, 40 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime()), 5.0f, new Color(100, 100, 100, 190));
                RenderUtil.drawRect(x + 5, y + 57, 40.0f + (Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime()) * MathHelper.clamp_float(mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth(), 0.0f, 1.0f)), 5.0f, new Color(255, 255, 255, 100));
                break;
            case "Custom":
                RoundedUtil.drawRound(x, y, 130.0f, 56.0f, 6.0f, new Color(255, 255, 255, 80));
                RenderUtil.startGlScissor(x - 2, y + 42, 134, 20);
                RoundedUtil.drawRound(x, y + 34, 130.0f, 22.0f, 6.0f, new Color(255, 255, 255, 100));
                RenderUtil.stopGlScissor();
                Bold.get(18.0f).drawString("Played Time:" + RenderUtil.sessionTime(), x + 4, (y + 62) - 16, -1);
                RenderUtil.renderPlayer2D(mc.thePlayer, x + 5, y + 4, 35.0f, 8.0f, -1);
                Bold.get(18.0f).drawString("kill:" + this.killed, x + 44, y + 10, -1);
                FontRenderer fontRenderer3 = Bold.get(18.0f);
                Mine mine2 = mc;
                fontRenderer3.drawString("FPS:" + Mine.getDebugFPS(), x + 79, y + 10, -1);
                FontRenderer fontRenderer4 = Bold.get(18.0f);
                DecimalFormat decimalFormat2 = this.bpsFormat;
                InterFace interFace2 = INTERFACE;
                fontRenderer4.drawString("BPS:" + decimalFormat2.format(InterFace.getBPS()), x + 44, y + 25, -1);
                Bold.get(18.0f).drawString("win:" + this.won, x + 44 + Bold.get(18.0f).getStringWidth("BPS:10.0.0") + 4, y + 25, -1);
                break;
            case "Normal":
                float stringWidth = 48 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime());
                InterFace interFace3 = INTERFACE;
                RoundedUtil.drawRound(x, y, stringWidth, 65.0f, InterFace.radius.get().intValue(), new Color(0, 0, 0, 89));
                RenderUtil.startGlScissor(x - 2, y - 1, 190, 20);
                float stringWidth2 = 48 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime());
                InterFace interFace4 = INTERFACE;
                float fIntValue = InterFace.radius.get().intValue();
                InterFace interFace5 = setting;
                RoundedUtil.drawRound(x, y, stringWidth2, 30.0f, fIntValue, ColorUtil.applyOpacity(new Color(InterFace.colors(1)), 0.3f));
                RenderUtil.stopGlScissor();
                RenderUtil.renderPlayer2D(mc.thePlayer, x + 5, y + 25, 35.0f, 14.0f, -1);
                FontManager.Bold.get(20.0f).drawString("Session", x + 5, y + 5, -1);
                FontManager.Bold.get(18.0f).drawString("Played Time:" + RenderUtil.sessionTime(), x + 44, y + 27, -1);
                FontManager.Bold.get(18.0f).drawString("kill:" + this.killed, x + 44, y + 39, -1);
                FontManager.Bold.get(18.0f).drawString("win:" + this.won, x + 44, y + 51, -1);
                break;
        }
        this.width = 48 + Bold.get(18.0f).getStringWidth("Played Time:" + RenderUtil.sessionTime());
        this.height = 65.0f;
    }

    @Override // qwq.arcane.module.ModuleWidget
    public boolean shouldRender() {
        return getState() && setting.getState();
    }
}
