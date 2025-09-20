package qwq.arcane.gui.notification;

import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.utils.time.Timer;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/notification/Notification.class */
public class Notification implements Instance {
    private ResourceLocation image;
    private final String message;
    private final String title;
    private final Type type;
    private double lastY;
    private double posY;
    private double width;
    private double height;
    private double animationX;
    private int color;
    private final int imageWidth;
    private final long stayTime;
    private boolean finished;
    public float animations;
    public final Animation animation = new DecelerateAnimation(380, 1.0d, Direction.BACKWARDS);
    private final Animation animationY = new DecelerateAnimation(380, 1.0d);
    private final float time = 1500.0f;
    private final Timer timer = new Timer();

    public ResourceLocation getImage() {
        return this.image;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTitle() {
        return this.title;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public float getTime() {
        return this.time;
    }

    public Type getType() {
        return this.type;
    }

    public double getLastY() {
        return this.lastY;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getWidth() {
        return this.width;
    }

    public double getAnimationX() {
        return this.animationX;
    }

    public int getColor() {
        return this.color;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public long getStayTime() {
        return this.stayTime;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public Animation getAnimationY() {
        return this.animationY;
    }

    public float getAnimations() {
        return this.animations;
    }

    public Notification(String title, String message, Type type, long time) {
        this.image = new ResourceLocation("solitude/notification/" + type.name.toLowerCase() + ".png");
        this.title = title;
        this.message = message;
        this.type = type;
        this.timer.reset();
        ScaledResolution sr = new ScaledResolution(Mine.getMinecraft());
        this.width = Bold.get(20.0f).getStringWidth(message) + 10;
        this.animationX = this.width;
        this.stayTime = time;
        this.imageWidth = 9;
        this.height = 30.0d;
        this.posY = sr.getScaledHeight() - this.height;
    }

    public void customshader(double getY, double lastY) {
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Mine.getMinecraft());
        this.animationY.setDirection(this.finished ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation.setDirection((isFinished() || this.finished) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animationX = this.width * this.animation.getOutput().doubleValue();
        this.posY = RenderUtil.animate(this.posY, getY);
        float timePassed = 1.0f - (this.timer.getTimePassed() / this.stayTime);
        int x1 = (int) (((resolution.getScaledWidth() - 6) - this.width) + this.animationX);
        int y1 = (int) this.posY;
        RenderUtil.drawRect((x1 + (Bold.get(14.0f).getStringWidth(this.message) / 2)) - 15.0f, y1, Bold.get(14.0f).getStringWidth(this.message) + 20.0f, 20.0f, new Color(1, 1, 1, 255).getRGB());
    }

    public void render1(double getY, double lastY) {
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Mine.getMinecraft());
        this.animationY.setDirection(this.finished ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation.setDirection((isFinished() || this.finished) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animationX = this.width * this.animation.getOutput().doubleValue();
        this.posY = RenderUtil.animate(this.posY, getY);
        float timePassed = 1.0f - (this.timer.getTimePassed() / this.stayTime);
        int x1 = ((resolution.getScaledWidth() / 2) - Bold.get(14.0f).getStringWidth(this.message)) + 30;
        int y1 = (int) this.posY;
        new ScaledResolution(Mine.getMinecraft());
        RoundedUtil.drawRound(x1, y1, Bold.get(14.0f).getStringWidth(this.message) + 30, 15.0f, 3.0f, new Color(255, 255, 255, 40));
        Icon.get(24.0f).drawString(this.type.icon, x1 + 4, y1 + 5, this.type.getColor().getRGB());
        Bold.get(14.0f).drawString(this.message, x1 + 18, y1 + 6, -1);
    }

    public void shader1(double getY, double lastY) {
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Mine.getMinecraft());
        this.animationY.setDirection(this.finished ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation.setDirection((isFinished() || this.finished) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animationX = this.width * this.animation.getOutput().doubleValue();
        this.posY = RenderUtil.animate(this.posY, getY);
        float timePassed = 1.0f - (this.timer.getTimePassed() / this.stayTime);
        int x1 = ((resolution.getScaledWidth() / 2) - Bold.get(14.0f).getStringWidth(this.message)) + 30;
        int y1 = (int) this.posY;
        new ScaledResolution(Mine.getMinecraft());
        RoundedUtil.drawRound(x1, y1, Bold.get(14.0f).getStringWidth(this.message) + 30, 15.0f, 3.0f, new Color(255, 255, 255, 190));
        Icon.get(24.0f).drawString(this.type.icon, x1 + 4, y1 + 5, this.type.getColor().getRGB());
    }

    public void custom(double getY, double lastY) {
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Mine.getMinecraft());
        this.animationY.setDirection(this.finished ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation.setDirection((isFinished() || this.finished) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animationX = this.width * this.animation.getOutput().doubleValue();
        this.posY = RenderUtil.animate(this.posY, getY);
        float timePassed = 1.0f - (this.timer.getTimePassed() / this.stayTime);
        int x1 = (int) (((resolution.getScaledWidth() - 6) - this.width) + this.animationX);
        int y1 = (int) this.posY;
        RenderUtil.drawRect((x1 + (Bold.get(14.0f).getStringWidth(this.message) / 2)) - 15.0f, y1, Bold.get(14.0f).getStringWidth(this.message) + 20.0f, 20.0f, new Color(1, 1, 1, 100).getRGB());
        RoundedUtil.drawGradientVertical(((x1 + (Bold.get(14.0f).getStringWidth(this.message) / 2)) - 15) + 4, y1 + 3, 2.5f, 14.0f, 2.0f, this.type.color, this.type.color);
        Bold.get(14.0f).drawString(this.message, (x1 + (Bold.get(14.0f).getStringWidth(this.message) / 2)) - 4, y1 + 8, -1);
    }

    public void render(double getY, double lastY) {
        this.lastY = lastY;
        ScaledResolution resolution = new ScaledResolution(Mine.getMinecraft());
        this.animationY.setDirection(this.finished ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation.setDirection((isFinished() || this.finished) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animationX = this.width * this.animation.getOutput().doubleValue();
        this.posY = RenderUtil.animate(this.posY, getY);
        float timePassed = 1.0f - (this.timer.getTimePassed() / this.stayTime);
        int x1 = (int) (((resolution.getScaledWidth() - 6) - this.width) + this.animationX);
        int y1 = (int) this.posY;
        RoundedUtil.drawRound(x1, y1, (float) this.width, (float) this.height, 6.0f, new Color(1, 1, 1, 100));
        RoundedUtil.drawRound(x1, y1, (float) this.width, 12.0f, 6.0f, new Color(1, 1, 1, 100));
        RenderUtil.drawCircleCGUI(x1 + 6, y1 + 6, 8.0f, this.type.color.getRGB());
        Bold.get(14.0f).drawString(this.title, x1 + 12, y1 + 4, -1);
        Icon.get(24.0f).drawString(this.type.icon, x1 + 4, y1 + 19, this.type.getColor().getRGB());
        Bold.get(14.0f).drawString(this.message, x1 + 18, y1 + 20, -1);
        RenderUtil.stopScissor();
    }

    public void shader(double getY, double lastY) {
        ScaledResolution resolution = new ScaledResolution(Mine.getMinecraft());
        this.animationY.setDirection(this.finished ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation.setDirection((isFinished() || this.finished) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.animationX = this.width * this.animation.getOutput().doubleValue();
        this.posY = RenderUtil.animate(this.posY, getY);
        float timePassed = 1.0f - (this.timer.getTimePassed() / this.stayTime);
        int x1 = (int) (((resolution.getScaledWidth() - 6) - this.width) + this.animationX);
        int y1 = (int) this.posY;
        RoundedUtil.drawRound(x1, y1, (float) this.width, (float) this.height, 6.0f, new Color(1, 1, 1, 255));
        RoundedUtil.drawRound(x1, y1, (float) this.width, 12.0f, 6.0f, new Color(1, 1, 1, 255));
        RenderUtil.drawCircleCGUI(x1 + 6, y1 + 6, 8.0f, this.type.color.getRGB());
        RenderUtil.stopScissor();
    }

    public boolean shouldDelete() {
        return (isFinished() || this.finished) && this.animationX >= this.width - 5.0d;
    }

    private boolean isFinished() {
        return this.timer.hasReached(this.stayTime);
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public double getHeight() {
        return this.height;
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/gui/notification/Notification$Type.class */
    public enum Type {
        SUCCESS("Success", "U", new Color(10673573)),
        INFO("Information", "N", new Color(12028786)),
        ERROR("Error", "T", new Color(12023674));

        final String name;
        final String icon;
        final Color color;

        Type(String name, String icon, Color color) {
            this.name = name;
            this.icon = icon;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public Color getColor() {
            return this.color;
        }

        public String getIcon() {
            return this.icon;
        }
    }
}
