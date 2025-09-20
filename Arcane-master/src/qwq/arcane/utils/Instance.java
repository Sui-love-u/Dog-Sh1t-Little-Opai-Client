package qwq.arcane.utils;

import qwq.arcane.Client;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.fontrender.FontManager;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/Instance.class */
public interface Instance {
    Mine mc = Mine.getMinecraft();
    Client INSTANCE = Client.Instance;
    FontManager Semibold = FontManager.Semibold;
    FontManager Bold = FontManager.Bold;
    FontManager Icon = FontManager.Icon;
    FontManager Light = FontManager.Light;
    FontManager Regular = FontManager.Regular;
}
