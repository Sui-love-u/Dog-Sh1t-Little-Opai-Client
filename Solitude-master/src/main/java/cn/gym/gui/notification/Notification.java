package cn.gym.gui.notification;

import cn.gym.Solitude;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.Instance;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.animations.impl.EaseOutSine;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.utils.time.Timer;
import cn.gym.utils.time.TimerUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@Getter
public class Notification implements Instance {
    private ResourceLocation image;
    private final String message;
    private final String title;
    private final Timer timer;

    @Getter
    private final float time;

    private final Type type;

    private double lastY, posY, width, height, animationX;
    private int color;
    private final int imageWidth;
    private final long stayTime;

    private boolean finished;

    public final Animation animation = new DecelerateAnimation(380, 1, Direction.BACKWARDS);
    private final Animation animationY = new DecelerateAnimation(380, 1);
    public float animations;

    public Notification(String title, String message, Type type, long time) {
        image = new ResourceLocation("solitude/notification/" + type.name.toLowerCase() + ".png");
        this.title = title;
        this.time = (long) (1500);
        this.message = message;
        this.type = type;
        timer = new Timer();
        timer.reset();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        width = Semibold.get(20).getStringWidth(message) + 34;
        animationX = width;
        stayTime = time;
        imageWidth = 9;
        height = 30;
        posY = sr.getScaledHeight() - height;
    }

    public void customshader(double getY, double lastY) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        this.lastY = lastY;
        animationY.setDirection(finished ? Direction.BACKWARDS : Direction.FORWARDS);
        animation.setDirection(isFinished() || finished ? Direction.FORWARDS : Direction.BACKWARDS);
        animationX = width * animation.getOutput();
        posY = RenderUtil.animate(posY, getY);
        RenderUtil.drawRect((float) ((resolution.getScaledWidth() / 2) - width / 2), (float) posY - 2, (float) width, 10, new Color(0, 0, 0, 255));

    }
    public void custom(double getY, double lastY) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        this.lastY = lastY;
        animationY.setDirection(finished ? Direction.BACKWARDS : Direction.FORWARDS);
        animation.setDirection(isFinished() || finished ? Direction.FORWARDS : Direction.BACKWARDS);
        animationX = width * animation.getOutput();
        posY = RenderUtil.animate(posY, getY);
        RenderUtil.drawRect((float) ((resolution.getScaledWidth() / 2) - width / 2), (float) posY - 2, (float) width, 10, new Color(0, 0, 0, 100));
        Mc.get(18).drawCenteredString(message, resolution.getScaledWidth() / 2, posY, -1);
    }
    public void render(double getY, double lastY) {
        Color scolor = new Color(0xFF171717);
        Color icolor = new Color(23, 3, 46, 100);
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

        animationY.setDirection(finished ? Direction.BACKWARDS : Direction.FORWARDS);
        animation.setDirection(isFinished() || finished ? Direction.FORWARDS : Direction.BACKWARDS);
        animationX = width * animation.getOutput();
        posY = RenderUtil.animate(posY, getY);

        int x1 = (int) ((resolution.getScaledWidth() - 6) - width + animationX), y1 = (int) posY;

        RoundedUtil.drawRound((float) x1, y1, (float) width, (float) height, 2, new Color(1,1,1,100));

        RenderUtil.startGlScissor(x1 - 4, y1 - 2, (int) width + 8,14);
        RoundedUtil.drawRound((float) x1, y1, (float) width , (float) 50, 2, ColorUtil.applyOpacity(new Color(Solitude.Instance.getModuleManager().getModule(Interface.class).color(1)).darker(), (float) 0.3f));
        RenderUtil.stopGlScissor();

        FontManager.newIcon35.get(35).drawString(type.getIcon(), (float) (x1 + 9), (float) (y1 + 6f + (height - 6f) / 2f),  Solitude.Instance.getModuleManager().getModule(Interface.class).color());

        Semibold.get(14).drawString(title, (x1 + 5), y1 + 4, Solitude.Instance.getModuleManager().getModule(Interface.class).color());
        Semibold.get(16).drawString(message, (float) (x1 + imageWidth + 16),
                (float) ((float) y1 + 9 + (height - Semibold.get(16).getHeight()) / 2), Solitude.Instance.getModuleManager().getModule(Interface.class).color());
        RenderUtil.stopScissor();
    }
    public void shader(double getY, double lastY) {
        Color scolor = new Color(0xFF171717);
        Color icolor = new Color(23, 3, 46, 100);
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

        animationY.setDirection(finished ? Direction.BACKWARDS : Direction.FORWARDS);
        animation.setDirection(isFinished() || finished ? Direction.FORWARDS : Direction.BACKWARDS);
        animationX = width * animation.getOutput();
        posY = RenderUtil.animate(posY, getY);

        int x1 = (int) ((resolution.getScaledWidth() - 6) - width + animationX), y1 = (int) posY;

        RoundedUtil.drawRound((float) x1, y1, (float) width, (float) height, 2, new Color(1,1,1,100));

        RenderUtil.startGlScissor(x1 - 4, y1 - 2, (int) width + 8,14);
        RoundedUtil.drawRound((float) x1, y1, (float) width , (float) 50, 2, ColorUtil.applyOpacity(new Color(Solitude.Instance.getModuleManager().getModule(Interface.class).color(1)).darker(), (float) 0.3f));
        RenderUtil.stopGlScissor();
        RenderUtil.stopScissor();
    }

    public boolean shouldDelete() {
        return (isFinished() || finished) && animationX >= width - 5;
    }

    private boolean isFinished() {
        return timer.hasReached(stayTime);
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public double getHeight() {
        return height;
    }

    public enum Type {
        SUCCESS("Success", "k"),
        INFO("Information", "j"),
        WARNING("Warning", "l"),
        ERROR("Error", "i");
        final String name;
        final String icon;

        Type(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

    }
}
