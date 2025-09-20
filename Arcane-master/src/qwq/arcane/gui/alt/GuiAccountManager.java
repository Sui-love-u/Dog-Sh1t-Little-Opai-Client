package qwq.arcane.gui.alt;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.optifine.RandomEntities;
import net.optifine.http.HttpPipeline;
import net.optifine.shaders.config.ShaderOption;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import qwq.arcane.config.ConfigManager;
import qwq.arcane.gui.MainMenu;
import qwq.arcane.gui.alt.auth.Account;
import qwq.arcane.gui.alt.auth.MicrosoftAuth;
import qwq.arcane.gui.alt.auth.SessionManager;
import qwq.arcane.gui.alt.gui.GuiAltCracked;
import qwq.arcane.gui.alt.gui.GuiMicrosoftAuth;
import qwq.arcane.gui.alt.gui.GuiSessionLogin;
import qwq.arcane.gui.alt.utils.Notification;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.utils.render.StencilUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/GuiAccountManager.class */
public class GuiAccountManager extends GuiScreen {
    private int selectedAccount;
    private GuiButton loginButton;
    private GuiButton deleteButton;
    private GuiButton cancelButton;
    private final GuiScreen previousScreen;
    private ExecutorService executor;
    private Notification notification;
    private CompletableFuture<Void> task;
    private GuiAccountList guiAccountList;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final Map<String, ResourceLocation> SKIN_CACHE = new HashMap();

    public GuiAccountManager(GuiScreen previousScreen) {
        this.selectedAccount = -1;
        this.loginButton = null;
        this.deleteButton = null;
        this.cancelButton = null;
        this.executor = null;
        this.notification = null;
        this.task = null;
        this.guiAccountList = null;
        this.previousScreen = previousScreen;
    }

    public GuiAccountManager(GuiScreen previousScreen, Notification notification) {
        this.selectedAccount = -1;
        this.loginButton = null;
        this.deleteButton = null;
        this.cancelButton = null;
        this.executor = null;
        this.notification = null;
        this.task = null;
        this.guiAccountList = null;
        this.previousScreen = previousScreen;
        this.notification = notification;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        List<GuiButton> list = this.buttonList;
        GuiButton guiButton = new GuiButton(0, (this.width / 2) - 160, this.height - 48, 78, 20, "Login");
        this.loginButton = guiButton;
        list.add(guiButton);
        this.buttonList.add(new GuiButton(5, (this.width / 2) + 3, this.height - 48, 78, 20, "Token"));
        this.buttonList.add(new GuiButton(1, (this.width / 2) - 160, this.height - 24, 78, 20, "Microsoft"));
        this.buttonList.add(new GuiButton(4, (this.width / 2) + 3, this.height - 24, 78, 20, "Offline"));
        this.buttonList.add(new GuiButton(7, (this.width / 2) - 78, this.height - 24, 78, 20, "Change Skin"));
        List<GuiButton> list2 = this.buttonList;
        GuiButton guiButton2 = new GuiButton(2, (this.width / 2) + 84, this.height - 48, 78, 20, "Delete");
        this.deleteButton = guiButton2;
        list2.add(guiButton2);
        List<GuiButton> list3 = this.buttonList;
        GuiButton guiButton3 = new GuiButton(3, (this.width / 2) + 84, this.height - 24, 78, 20, "Back");
        this.cancelButton = guiButton3;
        list3.add(guiButton3);
        this.guiAccountList = new GuiAccountList(mc);
        this.guiAccountList.registerScrollButtons(11, 12);
        updateScreen();
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawImage(new ResourceLocation("nothing/background.png"), 0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());
        if (this.guiAccountList != null) {
            this.guiAccountList.drawScreen(mouseX, mouseY, renderPartialTicks);
        }
        drawCenteredString(this.fontRendererObj, String.format("§rAccount Manager §8(§7%s§8)§r", Integer.valueOf(ConfigManager.getAccountCount())), this.width / 2, 10, -1);
        if (this.notification != null && !this.notification.isExpired()) {
            drawCenteredString(this.fontRendererObj, this.notification.getMessage(), mc.currentScreen.width / 2, 22, -1);
        } else {
            drawCenteredString(this.fontRendererObj, "Username: §7" + mc.getSession().getUsername(), mc.currentScreen.width / 2, 22, -1);
        }
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (this.task != null && !this.task.isDone()) {
            this.task.cancel(true);
        }
        if (this.executor != null && !this.executor.isShutdown()) {
            this.executor.shutdownNow();
            this.executor = null;
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void updateScreen() {
        if (this.loginButton != null && this.deleteButton != null) {
            GuiButton guiButton = this.loginButton;
            GuiButton guiButton2 = this.deleteButton;
            boolean z = this.selectedAccount >= 0 && (this.task == null || this.task.isDone());
            guiButton2.enabled = z;
            guiButton.enabled = z;
        }
        if (this.notification != null && this.notification.isExpired()) {
            this.notification = null;
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void handleMouseInput() throws IOException {
        if (this.guiAccountList != null) {
            this.guiAccountList.handleMouseInput();
        }
        super.handleMouseInput();
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void keyTyped(char typedChar, int keyCode) {
        switch (keyCode) {
            case 1:
                actionPerformed(this.cancelButton);
                break;
            case 28:
                if (this.loginButton.enabled) {
                    actionPerformed(this.loginButton);
                    break;
                }
                break;
            case 200:
                if (this.selectedAccount > 0) {
                    this.selectedAccount--;
                    this.guiAccountList.scrollBy(-this.guiAccountList.getSlotHeight());
                    if (isCtrlKeyDown()) {
                        ConfigManager.swapAccounts(this.selectedAccount, this.selectedAccount + 1);
                        break;
                    }
                }
                break;
            case 208:
                if (this.selectedAccount < ConfigManager.getAccountCount() - 1) {
                    this.selectedAccount++;
                    this.guiAccountList.scrollBy(this.guiAccountList.getSlotHeight());
                    if (isCtrlKeyDown()) {
                        ConfigManager.swapAccounts(this.selectedAccount, this.selectedAccount - 1);
                        break;
                    }
                }
                break;
            case 211:
                if (this.deleteButton.enabled) {
                    actionPerformed(this.deleteButton);
                    break;
                }
                break;
        }
        if (isKeyComboCtrlC(keyCode) && this.selectedAccount >= 0 && this.selectedAccount < ConfigManager.getAccountCount()) {
            Account acc = ConfigManager.getAccount(this.selectedAccount);
            if (acc != null && !StringUtils.isBlank(acc.getUsername()) && !acc.getUsername().equals("???")) {
                setClipboardString(acc.getUsername());
                this.notification = new Notification("§aCopied username to clipboard!", 2000L);
            } else if (acc != null && !StringUtils.isBlank(acc.getAccessToken())) {
                setClipboardString(acc.getAccessToken());
                this.notification = new Notification("§aCopied access token to clipboard!", 2000L);
            }
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void actionPerformed(GuiButton button) {
        String skinType;
        if (button == null || !button.enabled) {
            return;
        }
        switch (button.id) {
            case 0:
                if (task != null && !task.isDone()) return;

                if (executor == null || executor.isShutdown()) {
                    executor = Executors.newSingleThreadExecutor();
                }

                final Account account = ConfigManager.getAccount(selectedAccount);
                final String originalUsername = StringUtils.isBlank(account.getUsername()) ? "???" : account.getUsername();

                notification = new Notification(String.format("§7Logging in... (%s)§r", originalUsername), -1L);
                updateScreen();

                // Session Token Account
                if (StringUtils.isBlank(account.getRefreshToken()) && !StringUtils.isBlank(account.getAccessToken())) {
                    task = CompletableFuture.runAsync(() -> {
                        try {
                            String token = account.getAccessToken();
                            String[] playerInfo = GuiSessionLogin.getProfileInfo(token); // Validates and gets info
                            SessionManager.setSession(new Session(playerInfo[0], playerInfo[1], token, "mojang"));

                            account.setUsername(playerInfo[0]);
                            account.setTimestamp(System.currentTimeMillis());

                            this.notification = new Notification(String.format("§aLogged in as %s!§r", playerInfo[0]), 5000L);
                        } catch (IOException ioe) {
                            this.notification = new Notification(String.format("§cLogin failed for %s: %s§r", originalUsername,
                                    ioe.getMessage() != null && ioe.getMessage().contains("401") ? "Invalid/Expired Token" : "API Error"), 5000L);
                        } catch (Exception e) { // Catch other potential errors like JSON parsing
                            this.notification = new Notification(String.format("§cLogin failed for %s: Error processing profile§r", originalUsername), 5000L);
                            // e.printStackTrace(); // Good for debugging
                        }
                    }, executor).whenComplete((res, ex) -> updateScreen()); // Re-enable button
                }
                // Microsoft Account
                else if (!StringUtils.isBlank(account.getRefreshToken())) {
                    final AtomicReference<String> currentRefreshToken = new AtomicReference<>(account.getRefreshToken());
                    final AtomicReference<String> currentAccessToken = new AtomicReference<>(account.getAccessToken());

                    CompletableFuture<Session> loginAttemptFuture = MicrosoftAuth.login(currentAccessToken.get(), executor)
                            .handle((session, error) -> { // session is Session result, error is Throwable from initial login attempt
                                if (session != null) { // Login with current access token succeeded
                                    this.notification = new Notification(String.format("§aSuccessful login! (%s)§r", session.getUsername()), 5000L);
                                    return CompletableFuture.completedFuture(session); // Short-circuit to thenAccept
                                }

                                if (StringUtils.isBlank(currentRefreshToken.get())) {
                                    throw new RuntimeException("Current access token invalid and no refresh token available.");
                                }

                                this.notification = new Notification(String.format("§7Refreshing Microsoft access tokens... (%s)§r", originalUsername), -1L);

                                return MicrosoftAuth.refreshMSAccessTokens(currentRefreshToken.get(), executor)
                                        .thenComposeAsync(msAccessTokens -> {
                                            this.notification = new Notification(String.format("§7Acquiring Xbox access token... (%s)§r", originalUsername), -1L);
                                            currentRefreshToken.set(msAccessTokens.get("refresh_token")); // Update for saving
                                            return MicrosoftAuth.acquireXboxAccessToken(msAccessTokens.get("access_token"), executor);
                                        }, executor)
                                        .thenComposeAsync(xboxAccessToken -> {
                                            this.notification = new Notification(String.format("§7Acquiring Xbox XSTS token... (%s)§r", originalUsername), -1L);
                                            return MicrosoftAuth.acquireXboxXstsToken(xboxAccessToken, executor);
                                        }, executor)
                                        .thenComposeAsync(xboxXstsData -> {
                                            this.notification = new Notification(String.format("§7Acquiring Minecraft access token... (%s)§r", originalUsername), -1L);
                                            return MicrosoftAuth.acquireMCAccessToken(xboxXstsData.get("Token"), xboxXstsData.get("uhs"), executor);
                                        }, executor)
                                        .thenComposeAsync(mcToken -> {
                                            this.notification = new Notification(String.format("§7Fetching your Minecraft profile... (%s)§r", originalUsername), -1L);
                                            currentAccessToken.set(mcToken); // This is the new Minecraft access token
                                            return MicrosoftAuth.login(mcToken, executor);
                                        }, executor);
                            })
                            .thenCompose(Function.identity());

                    task = loginAttemptFuture.thenAccept(finalSession -> {
                        account.setRefreshToken(currentRefreshToken.get());
                        account.setAccessToken(finalSession.getToken());
                        account.setUsername(finalSession.getUsername());
                        account.setTimestamp(System.currentTimeMillis());
                        SessionManager.setSession(finalSession);

                        if (this.notification == null || !this.notification.getMessage().startsWith("§aSuccessful login")) {
                            this.notification = new Notification(String.format("§aSuccessful login! (%s)§r", finalSession.getUsername()), 5000L);
                        }
                    }).exceptionally(ex -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex; // Unwrap CompletionException
                        this.notification = new Notification(String.format("§cLogin failed for %s: %s§r", originalUsername, cause.getMessage()), 5000L);
                        return null; // Required for exceptionally
                    }).whenComplete((res, ex) -> updateScreen()); // Re-enable button
                } else {
                    notification = new Notification(String.format("§cCannot login: Account %s has no token information.§r", originalUsername), 5000L);
                    updateScreen(); // Re-enable button as no async task was started
                }
                break;
            case 1:
                mc.displayGuiScreen(new GuiMicrosoftAuth(this.previousScreen));
                break;
            case 2:
                if (this.selectedAccount >= 0 && this.selectedAccount < ConfigManager.getAccountCount()) {
                    ConfigManager.removeAccount(this.selectedAccount);
                    if (this.selectedAccount >= ConfigManager.getAccountCount() && ConfigManager.getAccountCount() > 0) {
                        this.selectedAccount = ConfigManager.getAccountCount() - 1;
                    } else if (ConfigManager.getAccountCount() == 0) {
                        this.selectedAccount = -1;
                    }
                }
                updateScreen();
                break;
            case 3:
                mc.displayGuiScreen(this.previousScreen instanceof MainMenu ? this.previousScreen : new MainMenu());
                break;
            case 4:
                mc.displayGuiScreen(new GuiAltCracked(this));
                break;
            case 5:
                mc.displayGuiScreen(new GuiSessionLogin(this));
                break;
            case 6:
            default:
                if (this.guiAccountList != null) {
                    this.guiAccountList.actionPerformed(button);
                    break;
                }
                break;
            case 7:
                try {
                    JFileChooser jFileChooser = new JFileChooser() { // from class: qwq.arcane.gui.alt.GuiAccountManager.1
                        protected JDialog createDialog(Component parent) throws HeadlessException {
                            JDialog dialog = super.createDialog(parent);
                            dialog.setModal(true);
                            dialog.setAlwaysOnTop(true);
                            return dialog;
                        }
                    };
                    int returnVal = jFileChooser.showOpenDialog((Component) null);
                    if (returnVal == 0) {
                        File skinFile = jFileChooser.getSelectedFile();
                        Map<String, String> headers = new HashMap<>();
                        if (!skinFile.getName().endsWith(RandomEntities.SUFFIX_PNG)) {
                            SwingUtilities.invokeLater(() -> {
                                this.notification = new Notification("Its seems that the file isn't a skin..", 2000L);
                            });
                            break;
                        } else {
                            int result = JOptionPane.showConfirmDialog((Component) null, "Is this a slim skin?", "alert", 1);
                            if (result == 2) {
                                break;
                            } else {
                                if (result == 0) {
                                    skinType = "slim";
                                } else {
                                    skinType = "classic";
                                }
                                headers.put(HttpPipeline.HEADER_ACCEPT, "*/*");
                                headers.put("Authorization", "Bearer " + mc.getSession().getToken());
                                headers.put(HttpPipeline.HEADER_USER_AGENT, "MojangSharp/0.1");
                                HttpResponse response = ((HttpRequest) HttpRequest.post("https://api.minecraftservices.com/minecraft/profile/skins").headerMap(headers, true)).form("variant", skinType).form("file", skinFile).execute();
                                if (response.getStatus() == 200 || response.getStatus() == 204) {
                                    SwingUtilities.invokeLater(() -> {
                                        this.notification = new Notification("Skin changed!", 2000L);
                                    });
                                } else {
                                    SwingUtilities.invokeLater(() -> {
                                        this.notification = new Notification("Failed to change skin.", 2000L);
                                    });
                                }
                            }
                        }
                    }
                    break;
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        this.notification = new Notification("Failed to change skin.", 2000L);
                    });
                    e.printStackTrace();
                    return;
                }
        }
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/GuiAccountManager$GuiAccountList.class */
    class GuiAccountList extends GuiSlot {
        public GuiAccountList(Mine mc) {
            super(mc, GuiAccountManager.this.width, GuiAccountManager.this.height, 32, GuiAccountManager.this.height - 64, 27);
        }

        @Override // net.minecraft.client.gui.GuiSlot
        protected int getSize() {
            return ConfigManager.getAccountCount();
        }

        @Override // net.minecraft.client.gui.GuiSlot
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            if (slotIndex < 0 || slotIndex >= getSize()) {
                return;
            }
            GuiAccountManager.this.selectedAccount = slotIndex;
            GuiAccountManager.this.updateScreen();
            if (isDoubleClick && GuiAccountManager.this.loginButton.enabled) {
                GuiAccountManager.this.actionPerformed(GuiAccountManager.this.loginButton);
            }
        }

        @Override // net.minecraft.client.gui.GuiSlot
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiAccountManager.this.selectedAccount;
        }

        @Override // net.minecraft.client.gui.GuiSlot
        protected int getContentHeight() {
            return getSize() * this.slotHeight;
        }

        @Override // net.minecraft.client.gui.GuiSlot
        protected void drawBackground() {
            GuiAccountManager.this.drawDefaultBackground();
        }

        @Override // net.minecraft.client.gui.GuiSlot
        protected void drawSlot(int entryID, int x, int y, int k, int mouseXIn, int mouseYIn) {
            if (entryID < 0 || entryID >= getSize()) {
                return;
            }
            Account account = ConfigManager.getAccount(entryID);
            String username = StringUtils.isBlank(account.getUsername()) ? "???" : account.getUsername();
            String accountType = "";
            String accountTypeColor = "§7";
            if (!StringUtils.isBlank(account.getRefreshToken())) {
                accountType = " (Microsoft)";
                accountTypeColor = ShaderOption.COLOR_BLUE;
            } else if (!StringUtils.isBlank(account.getAccessToken())) {
                accountType = " (Token)";
                accountTypeColor = "§6";
            } else if (username.equals("???")) {
                accountTypeColor = "§7";
            }
            Object[] objArr = new Object[3];
            objArr[0] = SessionManager.getSession().getUsername().equals(username) ? "§a§l" : accountTypeColor;
            objArr[1] = username;
            objArr[2] = accountType;
            String displayName = String.format("%s%s%s§r", objArr);
            GuiAccountManager.this.drawString(GuiAccountManager.this.fontRendererObj, displayName, x + 30, y + 3, -1);
            String time = String.format("§8§o%s§r", GuiAccountManager.sdf.format(new Date(account.getTimestamp())));
            GuiAccountManager.this.renderHead(x + 3, y + 1.0f, 21, account.getUUID());
            GuiAccountManager.this.drawString(GuiAccountManager.this.fontRendererObj, time, x + 30, y + 14, -1);
        }
    }

    private void renderHead(double x, double y, int size, String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            uuid = "8667ba71-b85a-4004-af54-457a9734eed7";
        }
        StencilUtils.initStencilToWrite();
        RoundedUtil.drawRound((float) x, (float) y, size, size, 2.5f, Color.WHITE);
        StencilUtils.readStencilBuffer(1);
        mc.getTextureManager().bindTexture(getResourceLocation(uuid));
        Gui.drawModalRectWithCustomSizedTexture((float) x, (float) y, 0.0f, 0.0f, size, size, size, size);
        StencilUtils.uninitStencilBuffer();
    }

    public static ResourceLocation getResourceLocation(String uuid) {
        if (SKIN_CACHE.containsKey(uuid)) {
            return SKIN_CACHE.get(uuid);
        }
        String imageUrl = "http://crafatar.com/avatars/" + uuid;
        ResourceLocation resourceLocation = new ResourceLocation("skins/" + uuid + "?overlay=true");
        ThreadDownloadImageData headTexture = new ThreadDownloadImageData(null, imageUrl, null, null);
        mc.getTextureManager().loadTexture(resourceLocation, headTexture);
        SKIN_CACHE.put(uuid, resourceLocation);
        AbstractClientPlayer.getDownloadImageSkin(resourceLocation, uuid);
        return resourceLocation;
    }
}
