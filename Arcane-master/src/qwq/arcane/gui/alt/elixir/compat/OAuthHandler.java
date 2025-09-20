package qwq.arcane.gui.alt.elixir.compat;

import qwq.arcane.gui.alt.elixir.account.MicrosoftAccount;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/elixir/compat/OAuthHandler.class */
public interface OAuthHandler {
    void openUrl(String str);

    void authResult(MicrosoftAccount microsoftAccount);

    void authError(String str);
}
