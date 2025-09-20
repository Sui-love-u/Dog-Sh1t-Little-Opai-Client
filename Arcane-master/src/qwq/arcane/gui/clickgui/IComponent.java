package qwq.arcane.gui.clickgui;

import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/IComponent.class */
public interface IComponent extends Instance {
    default void drawScreen(int mouseX, int mouseY) {
    }

    default void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    default void mouseReleased(int mouseX, int mouseY, int state) {
    }

    default void keyTyped(char typedChar, int keyCode) {
    }
}
