package qwq.arcane.gui.alt.utils;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/utils/AltLoginThread.class */
public final class AltLoginThread extends Thread {
    private final String password;
    private String status;
    private final String username;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AltLoginThread(String username, String password) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    private Session createSession(String username, String password) {
        try {
            MicrosoftAuthResult result = new MicrosoftAuthenticator().loginWithCredentials(username, password);
            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "microsoft");
        } catch (MicrosoftAuthenticationException e) {
            return null;
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        if (this.password.isEmpty()) {
            Instance.mc.session = new Session(this.username, "", "", "mojang");
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = EnumChatFormatting.YELLOW + "Logging in...";
        Session auth = createSession(this.username, this.password);
        if (auth == null) {
            this.status = EnumChatFormatting.RED + "Login failed!";
        } else {
            this.status = EnumChatFormatting.GREEN + "Logged in as " + auth.getUsername();
            Instance.mc.session = auth;
        }
    }
}
