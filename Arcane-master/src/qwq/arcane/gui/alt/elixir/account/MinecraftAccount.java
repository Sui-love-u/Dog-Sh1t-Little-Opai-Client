package qwq.arcane.gui.alt.elixir.account;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.imageio.ImageIO;
import net.minecraft.util.ResourceLocation;
import qwq.arcane.gui.alt.elixir.compat.Session;
import qwq.arcane.gui.alt.elixir.exception.LoginException;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/elixir/account/MinecraftAccount.class */
public abstract class MinecraftAccount {
    private final String type;
    private ResourceLocation headResource;
    private BufferedImage headImage;
    private static final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public abstract String getName();

    public abstract void setName(String str);

    public abstract Session getSession();

    public abstract void update() throws IOException, LoginException;

    public abstract void toRawYML(Map<String, String> map);

    public abstract void fromRawYML(Map<String, String> map);

    public MinecraftAccount(String type) {
        this.type = type;
    }

    public ResourceLocation getHeadResource() {
        return this.headResource;
    }

    public BufferedImage getHeadImage() {
        return this.headImage;
    }

    public void setHeadResource(ResourceLocation headResource) {
        this.headResource = headResource;
    }

    protected void loadHeadResource(String name) {
        if (getHeadResource() == null) {
            threadPool.execute(() -> {
                try {
                    this.headImage = ImageIO.read(new URL(String.format("https://minotar.net/avatar/%s", name)));
                } catch (IOException e) {
                }
            });
        }
    }

    public final String getType() {
        return this.type;
    }
}
