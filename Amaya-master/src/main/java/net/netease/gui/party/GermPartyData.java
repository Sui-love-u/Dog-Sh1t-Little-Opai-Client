/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.gui.party;

import net.netease.util.CustomMenuButton;

import java.util.ArrayList;
import java.util.List;

public class GermPartyData {
    private String text;
    private final List<CustomMenuButton> buttons = new ArrayList<CustomMenuButton>();

    public String getText() {
        return this.text;
    }

    public List<CustomMenuButton> getButtons() {
        return this.buttons;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof GermPartyData)) {
            return false;
        }
        GermPartyData other = (GermPartyData)o2;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) {
            return false;
        }
        List<CustomMenuButton> this$buttons = this.getButtons();
        List<CustomMenuButton> other$buttons = other.getButtons();
        return !(this$buttons != null ? !((Object)this$buttons).equals(other$buttons) : other$buttons != null);
    }

    protected boolean canEqual(Object other) {
        return other instanceof GermPartyData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        List<CustomMenuButton> $buttons = this.getButtons();
        result = result * 59 + ($buttons == null ? 43 : ((Object)$buttons).hashCode());
        return result;
    }

    public String toString() {
        return "GermPartyData(text=" + this.getText() + ", buttons=" + this.getButtons() + ")";
    }
}

