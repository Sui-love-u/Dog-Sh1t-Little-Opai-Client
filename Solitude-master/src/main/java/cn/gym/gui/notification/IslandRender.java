package cn.gym.gui.notification;

import cn.gym.Solitude;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.Instance;
import cn.gym.utils.animations.AnimationUtils;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.ContinualAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Deque;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/5/23
 */
public class IslandRender implements Instance {
    public static IslandRender INSTANCE = new IslandRender();

    public ContinualAnimation animatedX = new ContinualAnimation();
    public ContinualAnimation animatedY = new ContinualAnimation();
    public float x, y, width, height;
    private ScaledResolution sr;

    public String title, description;

    public IslandRender() {
        this.sr = new ScaledResolution(mc);
        if (mc.theWorld == null) {
            resetDisplay();
        }
    }

    public void rendershader(ScaledResolution sr){
        this.sr = sr;
        List<Notification> notifications = Solitude.Instance.getNotification().getNotifications();
        if (!notifications.isEmpty()) {
            Notification notification = notifications.get(notifications.size() - 1);
            if (!notification.getAnimation().finished(Direction.FORWARDS)) {
                renderNotification(notification);
                return;
            }
        }
        renderPersistentInfo();
    }
    public void render(ScaledResolution sr){
        this.sr = sr;

        List<Notification> notifications = Solitude.Instance.getNotification().getNotifications();
        if (!notifications.isEmpty()) {
            Notification notification = notifications.get(notifications.size() - 1);
            if (!notification.getAnimation().finished(Direction.FORWARDS)) {
                renderNotification(notification);
                return;
            }
        }
        renderPersistentInfo();
    }
    private void renderNotification(Notification notification) {
        title = notification.getMessage();
        description = notification.getTitle();
        width = FontManager.Bold.get(18).getStringWidth(title) + 35 + 20;
        height = 34;
        x = sr.getScaledWidth() / 2f;
        y = 30;

        runToXy(x, y);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        drawBackgroundAuto(1);
        notification.animations = AnimationUtils.animate(notification.animations,notification.getType() == notification.getType().SUCCESS? 10 : 9,0.9f);
        float progress = Math.min(notification.getTimer().getTime2() / notification.getTime(), 1);
        RoundedUtil.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2),
                width - 12, 5f, 2.5f,Interface.FirstColor.get().darker());
        RoundedUtil.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2),
                (width - 12) * progress, 5f, 2.5f, Interface.FirstColor.get());
        if (notification.getType() == notification.getType().SUCCESS){
            RoundedUtil.drawRound(x - width / 2 + 5,y - 9,35,18,8,Interface.FirstColor.get().darker());
            RenderUtil.drawCircleCGUI(x - width / 2 + 22 + notification.animations,y,14,Interface.FirstColor.get().getRGB());
        }else {
            RoundedUtil.drawRound(x - width / 2 + 5,y - 9,35,18,8,Interface.FirstColor.get().darker());
            RenderUtil.drawCircleCGUI(x - width / 2 + 22 - notification.animations,y,14,Interface.FirstColor.get().getRGB());
        }
        FontManager.Bold.get(18).drawString(title,x - width / 2 + 46,y + 2,Solitude.Instance.getModuleManager().getModule(Interface.class).color(0));
        FontManager.Bold.get(18).drawString(description,x - width / 2 + 46,y - 8,Solitude.Instance.getModuleManager().getModule(Interface.class).color(0));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }
    public static String getCurrentConnectionInfo() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
            return "SinglePlayer";
        } else if (mc.getCurrentServerData() != null) {
            return mc.getCurrentServerData().serverIP;
        } else {
            return "null";
        }
    }
    private void renderPersistentInfo() {
        String sb = "Solitude" + " | ";
        String sbs = getCurrentConnectionInfo();
        float sb2 = FontManager.Bold.get(24).getStringWidth(sb) + 10 + FontManager.Bold.get(14).getStringWidth(sbs);
        float sb3 = FontManager.Bold.get(24).getStringWidth(sb) + 6;
        width = sb2;
        height = 23;
        x = sr.getScaledWidth() / 2f;
        y = 20;
        runToXy(x, y);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        drawBackgroundAuto(0);
        FontManager.Bold.get(24).drawString("Solitude" + " | ",x - sb2 / 2 + 4,y - 6,Solitude.Instance.getModuleManager().getModule(Interface.class).color(0));
        FontManager.Bold.get(14).drawString(getCurrentConnectionInfo(),x + sb3 - width / 2,y - 5,Solitude.Instance.getModuleManager().getModule(Interface.class).color(0));
        FontManager.Bold.get(14).drawString("FPS"+mc.getDebugFPS(),x + sb3 - width / 2,y + 2,Solitude.Instance.getModuleManager().getModule(Interface.class).color(0));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }
    public float getRenderX(float x) {
        return x - width / 2;
    }

    public float getRenderY(float y) {
        return y - height / 2;
    }

    public void runToXy(float realX, float realY) {
        animatedX.animate(getRenderX(realX), 30);
        animatedY.animate(getRenderY(realY), 30);
    }
    public void drawBackgroundAuto(int identifier) {
        float renderHeight = ((y - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);
        RenderUtil.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1,
                ((x - animatedX.getOutput()) * 2) + 2, renderHeight + 2);
        RoundedUtil.drawRound(animatedX.getOutput(), animatedY.getOutput(),
                (x - animatedX.getOutput()) * 2, renderHeight, 8, new Color(1,1,1,100));
        RoundedUtil.drawRound(animatedX.getOutput(), animatedY.getOutput(),
                (x - animatedX.getOutput()) * 2, renderHeight, 8, ColorUtil.applyOpacity3(Solitude.Instance.getModuleManager().getModule(Interface.class).color(0),0.3f));
    }

    private void resetDisplay() {
        x = sr.getScaledWidth() / 2f;
        y = 20;
        width = 0;
        height = 0;
        title = "";
    }
}
