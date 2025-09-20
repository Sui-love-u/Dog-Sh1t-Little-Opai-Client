package qwq.arcane.gui.clickgui.dropdown.setting.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.TextValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/setting/impl/StringComponent.class */
public class StringComponent extends Component {
    private final TextValue setting;
    private boolean inputting;
    private final Animation input = new DecelerateAnimation(250, 1.0d);
    private String text = "";

    public StringComponent(TextValue setting) {
        this.setting = setting;
        setHeight((Bold.get(14.0f).getHeight() * 2) + 6);
        this.input.setDirection(Direction.BACKWARDS);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.input.setDirection(this.inputting ? Direction.FORWARDS : Direction.BACKWARDS);
        this.text = this.setting.get();
        if (this.setting.isOnlyNumber() && !NumberUtils.isNumber(this.text)) {
            this.text = this.text.replaceAll("[a-zA-Z]", "");
        }
        String textToDraw = (!this.setting.get().isEmpty() || this.inputting) ? this.setting.getText() : "Empty...";
        RoundedUtil.drawRound(getX(), (getY() + Bold.get(14.0f).getHeight()) - 2.0f, getWidth(), Bold.get(14.0f).getHeight() + 4, 2.0f, InterFace.mainColor.get());
        Bold.get(14.0f).drawString(this.setting.getName(), getX() + 4.0f, getY(), -1);
        drawTextWithLineBreaks(textToDraw + ((!this.inputting || this.text.length() >= 59 || System.currentTimeMillis() % 1000 <= 500) ? "" : "|"), getX() + 6.0f, getY() + Bold.get(14.0f).getHeight() + 2.0f, getWidth() - 12.0f);
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX(), getY() + Bold.get(14.0f).getHeight() + 4.0f, getWidth(), 4.0f, mouseX, mouseY) && mouseButton == 0) {
            this.inputting = !this.inputting;
        } else {
            this.inputting = false;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        if (this.setting.isOnlyNumber() && !NumberUtils.isNumber(String.valueOf(typedChar))) {
            return;
        }
        if (this.inputting) {
            if (keyCode == 14) {
                deleteLastCharacter();
            }
            if (this.text.length() < 18 && (Character.isLetterOrDigit(typedChar) || keyCode == 57)) {
                this.text += typedChar;
                this.setting.setText(this.text);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    private void drawTextWithLineBreaks(String text, float x, float y, float maxWidth) {
        String[] lines = text.split("\n");
        float currentY = y;
        for (String line : lines) {
            List<String> wrappedLines = wrapText(line, 0.0f, maxWidth);
            for (String wrappedLine : wrappedLines) {
                Bold.get(14.0f).drawString(wrappedLine, x, currentY, ColorUtil.interpolateColor2(Color.GRAY, Color.WHITE, this.input.getOutput().floatValue()));
                currentY += Bold.get(14.0f).getHeight();
            }
        }
    }

    private List<String> wrapText(String text, float size, float maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (Bold.get(14.0f).getStringWidth(word) > maxWidth) {
                if (!currentLine.toString().isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                currentLine = breakAndAddWord(word, currentLine, size, lines);
            } else if (Bold.get(14.0f).getStringWidth(currentLine.toString() + word) <= maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word).append(" ");
            }
        }
        if (!currentLine.toString().isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private void deleteLastCharacter() {
        if (!this.text.isEmpty()) {
            this.text = this.text.substring(0, this.text.length() - 1);
            this.setting.setText(this.text);
        }
    }

    private StringBuilder breakAndAddWord(String word, StringBuilder currentLine, float maxWidth, List<String> lines) {
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            String nextPart = currentLine.toString() + c;
            if (Bold.get(14.0f).getStringWidth(nextPart) <= maxWidth) {
                currentLine.append(c);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(String.valueOf(c));
            }
        }
        return currentLine;
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
