package qwq.arcane.module.impl.visuals;

import qwq.arcane.Client;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/ClickGui.class */
public class ClickGui extends Module {
    public static ModeValue modeValue = new ModeValue("Mode", "DropDown", new String[]{"DropDown", "Arcane"});

    public ClickGui() {
        super("ClickGui", Category.Visuals);
        setKey(54);
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        /*
        if (!Mine.isPaused) {
            switch (modeValue.getValue()) {
                case "DropDown":
                    mc.displayGuiScreen(Client.Instance.getDropDownClickGui());
                    break;
                case "Arcane":
                    mc.displayGuiScreen(Client.Instance.getArcaneClickGui());
                    break;
            }
        }

         */

        switch (modeValue.getValue()) {
            case "DropDown":
                mc.displayGuiScreen(Client.Instance.getDropDownClickGui());
                break;
            case "Arcane":
                mc.displayGuiScreen(Client.Instance.getArcaneClickGui());
                break;
        }

        if (Mine.isPaused) {
            // System.exit(0);
        }

        setState(false);
    }
}
