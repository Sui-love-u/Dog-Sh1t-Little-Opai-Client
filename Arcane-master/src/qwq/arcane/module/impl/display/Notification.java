package qwq.arcane.module.impl.display;

import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/Notification.class */
public class Notification extends ModuleWidget {
    public ModeValue modeValue;

    public Notification() {
        super("Notification", Category.Display);
        this.modeValue = new ModeValue("Mode", "Normal", new String[]{"Normal", "Custom", "Type1"});
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void onShader(Shader2DEvent event) {
        switch (this.modeValue.getValue()) {
            case "Custom":
                Client.Instance.getNotification().customshader(this.sr.getScaledHeight() - 6);
                break;
            case "Normal":
                Client.Instance.getNotification().normalshader(this.sr.getScaledHeight() - 6);
                break;
            case "Type1":
                Client.Instance.getNotification().type1shader((this.sr.getScaledHeight() / 2) + 26);
                break;
        }
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        switch (this.modeValue.getValue()) {
            case "Custom":
                Client.Instance.getNotification().custom(this.sr.getScaledHeight() - 6);
                break;
            case "Normal":
                Client.Instance.getNotification().normalrender(this.sr.getScaledHeight() - 6);
                break;
            case "Type1":
                Client.Instance.getNotification().type1render((this.sr.getScaledHeight() / 2) + 26);
                break;
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void render() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.width = 0.0f;
        this.height = 0.0f;
    }

    @Override // qwq.arcane.module.ModuleWidget
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
