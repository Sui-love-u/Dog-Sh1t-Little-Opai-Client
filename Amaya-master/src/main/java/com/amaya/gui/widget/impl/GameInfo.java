package com.amaya.gui.widget.impl;

import com.amaya.Amaya;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.gui.widget.Widget;
import com.amaya.module.impl.player.AutoPlay;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
public class GameInfo extends Widget {
    public GameInfo() {
        super("Session");
        resetTimer();
    }
    private com.amaya.module.impl.display.GameInfo value = Amaya.Instance.moduleManager.getModule(com.amaya.module.impl.display.GameInfo.class);

    @Override
    public void onShader(Shader2DEvent event) {
        int x = (int) renderX;
        int y = (int) renderY;
        RoundedUtil.drawRound(x,y, 140, 65, value.radius.get().intValue(), new Color(0, 0, 0, 255));
    }


    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;
        RoundedUtil.drawRound(x,y, 140, 65, value.radius.get().intValue(), new Color(0, 0, 0, 89));
        RenderUtil.startGlScissor(x - 2,y - 1, 159, 20);
        RoundedUtil.drawRound(x,y, 140, 30, value.radius.get().intValue(), new Color(50, 50, 50, 255));
        RenderUtil.stopGlScissor();
        RenderUtil.renderPlayer2D(mc.thePlayer, x + 5, y + 25, 35, 12, -1);
        FontManager.BOLD.get(20).drawString("Session",x + 5,y + 5,-1);
        FontManager.REGULAR.get(18).drawString("Played Time:" + getTime(),x + 44,y + 27,-1);
        FontManager.REGULAR.get(18).drawString("kill:" + value.kills,x + 44,y + 39,-1);
        FontManager.REGULAR.get(18).drawString("win:" + AutoPlay.sb,x + 44,y + 51,-1);
        this.width = 140;
        this.height = 65;
    }
    private long startTime;
    private void resetTimer() {
        this.startTime = System.currentTimeMillis();
    }
    private String getTime() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;

        int seconds = (int) (elapsed / 1000) % 60;
        int minutes = (int) (elapsed / (1000 * 60)) % 60;

        return String.format("%02dm %02ds", minutes, seconds);
    }
    @Override
    public boolean shouldRender() {
        return value.getState();
    }
}
