package qwq.arcane.gui.alt.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lwjgl.input.Keyboard;
import qwq.arcane.config.ConfigManager;
import qwq.arcane.gui.alt.GuiAccountManager;
import qwq.arcane.gui.alt.auth.Account;
import qwq.arcane.gui.alt.elixir.account.MicrosoftAccount;
import qwq.arcane.gui.alt.elixir.compat.Session;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/gui/GuiSessionLogin.class */
public class GuiSessionLogin extends GuiScreen {
    private final GuiScreen previousScreen;
    private String status = null;
    private GuiButton cancelButton = null;
    private GuiTextField sessionField = null;

    public GuiSessionLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sessionField = new GuiTextField(1, this.fontRendererObj, (this.width / 2) - 100, this.height / 2, 200, 20);
        this.sessionField.setMaxStringLength(32767);
        this.sessionField.setFocused(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, (this.width / 2) - 100, (this.height / 2) + 35, "Login"));
        List<GuiButton> list = this.buttonList;
        GuiButton guiButton = new GuiButton(0, (this.width / 2) - 100, (this.height / 2) + 65, "Cancel");
        this.cancelButton = guiButton;
        list.add(guiButton);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.sessionField.drawTextBox();
        if (this.status != null) {
            this.fontRendererObj.drawStringWithShadow(this.status, (this.width / 2.0f) - 100.0f, (this.height / 2.0f) - 20.0f, -1);
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void keyTyped(char typedChar, int keyCode) {
        this.sessionField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28 && this.sessionField.isFocused()) {
            actionPerformed(this.buttonList.iterator().next());
        }
        if (keyCode == 1) {
            actionPerformed(this.cancelButton);
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void actionPerformed(GuiButton button) {
        if (button != null && button.id == 0) {
            mc.displayGuiScreen(new GuiAccountManager(this.previousScreen));
        }
        if (button != null && button.id == 1) {
            try {
                String token = this.sessionField.getText();
                if (token.startsWith("M.C")) {
                    MicrosoftAccount microsoftAccount = MicrosoftAccount.buildFromRefreshToken(token, MicrosoftAccount.AuthMethod.TOKENLOGIN);
                    Session customSession = microsoftAccount.getSession();
                    net.minecraft.util.Session mcSession = new net.minecraft.util.Session(customSession.getUsername(), customSession.getUuid(), customSession.getToken(), "mojang");
                    mc.setSession(mcSession);
                    if (customSession.getUsername() != null) {
                        this.status = "§2Logged in as " + customSession.getUsername();
                    }
                    Account sessionAccount = new Account(token, customSession.getToken(), customSession.getUsername(), System.currentTimeMillis(), customSession.getUuid());
                    ConfigManager.addAccount(sessionAccount);
                } else {
                    String[] playerInfo = getProfileInfo(token);
                    mc.setSession(new net.minecraft.util.Session(playerInfo[0], playerInfo[1], token, "mojang"));
                    this.status = "§2Logged in as " + playerInfo[0];
                    Account sessionAccount2 = new Account("", token, playerInfo[0], System.currentTimeMillis(), playerInfo[1]);
                    ConfigManager.addAccount(sessionAccount2);
                }
            } catch (Exception e) {
                this.status = "§4Invalid token (" + "" + ")";
                e.printStackTrace();
            }
        }
    }

    public static String[] getProfileInfo(String token) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(MicrosoftAccount.MC_PROFILE_URL);
        request.setHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse response = client.execute(request);
        String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        String IGN = jsonObject.get("name").getAsString();
        String UUID = jsonObject.get("id").getAsString();
        return new String[]{IGN, UUID};
    }
}
