package qwq.arcane.gui.alt.gui;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import qwq.arcane.config.ConfigManager;
import qwq.arcane.gui.alt.GuiAccountManager;
import qwq.arcane.gui.alt.auth.Account;
import qwq.arcane.gui.alt.auth.MicrosoftAuth;
import qwq.arcane.gui.alt.auth.SessionManager;
import qwq.arcane.gui.alt.utils.Notification;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/gui/GuiMicrosoftAuth.class */
public class GuiMicrosoftAuth extends GuiScreen {
    private final GuiScreen previousScreen;
    private String cause = null;
    private String status = null;
    private GuiButton cancelButton = null;
    private ExecutorService executor = null;
    private CompletableFuture<Void> task = null;

    public GuiMicrosoftAuth(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void initGui() {
        buttonList.clear();

        buttonList.add(cancelButton = new GuiButton(0, width / 2 - 100, height / 2 + fontRendererObj.FONT_HEIGHT / 2 + fontRendererObj.FONT_HEIGHT, "Cancel"));
        if (task == null) {
            if (executor == null) {
                executor = Executors.newSingleThreadExecutor();
            }
            AtomicReference<String> refreshToken = new AtomicReference<>("");
            AtomicReference<String> accessToken = new AtomicReference<>("");
            status = "§fCheck your browser to continue...§r";
            task = MicrosoftAuth.acquireMSAuthCode(executor).thenComposeAsync(msAuthCode -> {
                status = "§fAcquiring Microsoft access tokens§r";
                return MicrosoftAuth.acquireMSAccessTokens(msAuthCode, executor);
            }).thenComposeAsync(msAccessTokens -> {
                status = "§fAcquiring Xbox access token§r";
                refreshToken.set(msAccessTokens.get("refresh_token"));
                return MicrosoftAuth.acquireXboxAccessToken(msAccessTokens.get("access_token"), executor);
            }).thenComposeAsync(xboxAccessToken -> {
                status = "§fAcquiring Xbox XSTS token§r";
                return MicrosoftAuth.acquireXboxXstsToken(xboxAccessToken, executor);
            }).thenComposeAsync(xboxXstsData -> {
                status = "§fAcquiring Minecraft access token§r";
                return MicrosoftAuth.acquireMCAccessToken(xboxXstsData.get("Token"), xboxXstsData.get("uhs"), executor);
            }).thenComposeAsync(mcToken -> {
                        status = "§fFetching your Minecraft profile§r";
                        accessToken.set(mcToken);
                        return MicrosoftAuth.login(mcToken, executor);
                    }
            ).thenAccept(session -> {
                status = null;
                ConfigManager.addAccount(new Account(refreshToken.get(), accessToken.get(), session.getUsername(), System.currentTimeMillis(), session.getProfile().getId().toString()));
                SessionManager.setSession(session);
                mc.displayGuiScreen(new GuiAccountManager(previousScreen, new Notification(String.format("§aSuccessful login! (%s)§r", session.getUsername()), 5000L)));
            }).exceptionally(error -> {
                status = String.format("§c%s§r", error.getMessage());
                cause = String.format("§c%s§r", error.getCause().getMessage());
                return null;
            });
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void onGuiClosed() {
        if (this.task != null && !this.task.isDone()) {
            this.task.cancel(true);
            this.executor.shutdownNow();
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Microsoft Authentication", this.width / 2, ((this.height / 2) - (this.fontRendererObj.FONT_HEIGHT / 2)) - (this.fontRendererObj.FONT_HEIGHT * 2), 11184810);
        if (this.status != null) {
            drawCenteredString(this.fontRendererObj, this.status, this.width / 2, (this.height / 2) - (this.fontRendererObj.FONT_HEIGHT / 2), -1);
        }
        if (this.cause != null) {
            String causeText = this.cause;
            Gui.drawRect(0, ((this.height - 2) - this.fontRendererObj.FONT_HEIGHT) - 3, 3 + this.fontRendererObj.getStringWidth(causeText) + 3, this.height, 1677721600);
            drawString(this.fontRendererObj, this.cause, 3, (this.height - 2) - this.fontRendererObj.FONT_HEIGHT, -1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            actionPerformed(this.cancelButton);
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void actionPerformed(GuiButton button) {
        if (button != null && button.id == 0) {
            mc.displayGuiScreen(new GuiAccountManager(this.previousScreen));
        }
    }
}
