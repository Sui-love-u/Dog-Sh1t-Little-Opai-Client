/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.gui;

import com.amaya.utils.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.packet.impl.Packet26;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.amaya.utils.client.InstanceAccess.mc;

public class GermGameElement {
    private final List<GermGameSubElement> subElements = new ArrayList<GermGameSubElement>();
    private final String name;
    private final String defaultPath;
    private final String hoverPath;
    private final List<String> hoverDos;
    private ResourceLocation defaultImage;
    private ResourceLocation hoverImage;
    private GifDecoder.GifImage gifImage;
    private final List<Integer> delayList = new ArrayList<Integer>();
    private final TimerUtil updateTimer = new TimerUtil();
    private int imageCount;
    private Runnable runnable;
    private final String clickName;

    public GermGameElement(String name, String defaultPath, String hoverPath, List<String> hoverDos, String clickName) {
        this.name = name;
        this.defaultPath = defaultPath;
        this.hoverPath = hoverPath;
        this.hoverDos = hoverDos;
        this.clickName = clickName;
    }

    public synchronized void loadTexture() throws IOException {
        this.defaultImage = TextureUtil.loadTextureFormURL(this.defaultPath);
    }

    public synchronized void loadHoverTexture() {
        try {
            URI url = new URI(this.hoverPath);
            URLConnection urlConnection = url.toURL().openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            this.gifImage = GifDecoder.read(IOUtils.toByteArray(inputStream));
            for (int i = 0; i < this.gifImage.getFrameCount(); ++i) {
                BufferedImage bufferedImage = this.gifImage.getFrame(i);
                ResourceLocation resourceLocation = new ResourceLocation(String.valueOf(this.hoverPath.hashCode() + i));
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new DynamicTexture(bufferedImage)));
                this.delayList.add(this.gifImage.getDelay(i));
            }
            inputStream.close();
        } catch (Exception e2) {
            String message = Arrays.toString(e2.getMessage().toCharArray());
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        }
    }

    public ResourceLocation getCurrentGifImage() {
        if (this.imageCount >= this.gifImage.getFrameCount()) {
            this.imageCount = 0;
        }
        if (!this.delayList.isEmpty() && this.updateTimer.hasTimeElapsed(this.delayList.get(this.imageCount) * 10)) {
            this.hoverImage = new ResourceLocation(String.valueOf(this.hoverPath.hashCode() + this.imageCount));
            this.updateTimer.reset();
            ++this.imageCount;
        }
        return this.hoverImage;
    }

    public void click(String guiName) {
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("click", 1);
        String json = GsonUtil.toJson(data);
        String message = new StringBuilder().insert(0, "GUI$").append(guiName).append("@").append(this.clickName).toString();
        PacketProcessor.INSTANCE.sendPacket(new Packet26(message, json));
    }

    public List<GermGameSubElement> getSubElements() {
        return this.subElements;
    }

    public String getName() {
        return this.name;
    }

    public String getDefaultPath() {
        return this.defaultPath;
    }

    public String getHoverPath() {
        return this.hoverPath;
    }

    public List<String> getHoverDos() {
        return this.hoverDos;
    }

    public ResourceLocation getDefaultImage() {
        return this.defaultImage;
    }

    public ResourceLocation getHoverImage() {
        return this.hoverImage;
    }

    public GifDecoder.GifImage getGifImage() {
        return this.gifImage;
    }

    public List<Integer> getDelayList() {
        return this.delayList;
    }

    public TimerUtil getUpdateTimer() {
        return this.updateTimer;
    }

    public int getImageCount() {
        return this.imageCount;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public String getClickName() {
        return this.clickName;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}

