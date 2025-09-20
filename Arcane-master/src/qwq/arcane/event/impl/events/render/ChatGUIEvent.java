package qwq.arcane.event.impl.events.render;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/render/ChatGUIEvent.class */
public class ChatGUIEvent extends CancellableEvent {
    private final int mouseX;
    private final int mouseY;

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }

    public ChatGUIEvent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
