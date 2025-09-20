/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package com.amaya.gui.clickgui.neverlose.panel.search;

import com.amaya.gui.clickgui.neverlose.Component;
import com.amaya.gui.clickgui.neverlose.IComponent;
import com.amaya.gui.clickgui.neverlose.component.ModuleComponent;
import com.amaya.gui.clickgui.neverlose.panel.CategoryPanel;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.animations.impl.SmoothStepAnimation;
import com.amaya.utils.client.InstanceAccess;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static com.amaya.gui.clickgui.neverlose.NegativeClickGui.*;

/**
 * @Author: Guyuemang
 */
@Getter
public class SearchPanel extends CategoryPanel implements IComponent, InstanceAccess {
    @Setter
    private boolean selected;
    private int posX, posY;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private final Animation animation = new DecelerateAnimation(250,1);
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();
    private ObjectArrayList<ModuleComponent> filtered = new ObjectArrayList<>();
    private final Animation input = new DecelerateAnimation(250, 1);
    private boolean inputting;
    private String text = "";
    public SearchPanel(Category category) {
        super(category);
        for (Module module : INSTANCE.moduleManager.getAllModules()) {
            moduleComponents.add(new ModuleComponent(module));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //animations
        animation.setDirection(isSelected() ? Direction.FORWARDS : Direction.BACKWARDS);
        input.setDirection(inputting ? Direction.FORWARDS : Direction.BACKWARDS);
        //update coordinate
        posX = INSTANCE.getNegative().getPosX();
        posY = INSTANCE.getNegative().getPosY();
        //render
        if (isSelected()){
            RoundedUtil.drawRoundOutline(posX + 140, posY + 12, 340, (float) 22, 2, 0.1f, ColorUtil.applyOpacity(bgcolor3, (float) animation.getOutput().floatValue()), ColorUtil.applyOpacity(bgcolor4, (float) animation.getOutput().floatValue()));
            //drawTextWithLineBreaks(text + (inputting && text.length() < 67 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""), posX + 144, posY + 21, 180);

            //render module components
            ObjectArrayList<ModuleComponent> filtered = moduleComponents.stream()
                    .filter(moduleComponent -> moduleComponent.getModule().getName().toLowerCase().contains(text.toLowerCase()))
                    .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
            this.filtered = filtered;
            FontManager.SEMIBOLD.get(18).drawString(text + (inputting && text.length() < 67 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""), posX + 146, posY + 21, textcolor.darker().darker().getRGB());
            if (!inputting && text.isEmpty()) {
                FontManager.neverlose.get(24).drawString("u", posX + 146, posY + 20, textcolor.getRGB());
                FontManager.SEMIBOLD.get(18).drawString("Search", posX + 166, posY + 21, textcolor.getRGB());
            }
            if (!filtered.isEmpty()) {
                GL11.glPushMatrix();

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(getPosX() + 140, getPosY() + 49, 380, 368);

                float left = 0, right = 0;

                for (int i = 0; i < filtered.size(); i++) {
                    ModuleComponent module = filtered.get(i);

                    module.setLeft((i + 1) % 2 != 0);
                    module.setX(module.isLeft() ? posX + 140 : posX + 330);
                    module.setHeight(20);
                    module.setY(posY + 32 + module.getHeight() + ((i + 1) % 2 == 0 ? left : right));
                    float componentOffset = 0;
                    for (Component component2 : module.getComponents()) {
                        if (component2.isVisible())
                            componentOffset += component2.getHeight();
                    }
                    module.setHeight(module.getHeight() + componentOffset);

                    module.drawScreen(mouseX, mouseY);

                    double scroll = getScroll();
                    module.setScroll((int) MathUtils.roundToHalf(scroll));
                    onScroll(30, mouseX, mouseY);

                    maxScroll = Math.max(0, filtered.isEmpty() ? 0 : filtered.get(filtered.size() - 1).getMaxScroll());

                    if ((i + 1) % 2 == 0) {
                        left += 40 + componentOffset;
                    } else {
                        right += 40 + componentOffset;
                    }
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glPopMatrix();
            }
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(posX + 140, posY + 12, 340,22,mouseX,mouseY) && mouseButton == 0){
            inputting = !inputting;
        } else {
            inputting = false;
        }
        filtered.forEach(moduleComponent -> moduleComponent.mouseClicked(mouseX,mouseY,mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        filtered.forEach(moduleComponent -> moduleComponent.mouseReleased(mouseX,mouseY,state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (inputting){
            if (keyCode == Keyboard.KEY_BACK) {
                deleteLastCharacter();
            }
            if (text.length() < 66 && (Character.isLetterOrDigit(typedChar) || keyCode == Keyboard.KEY_SPACE)) {
                text += typedChar;
            }
        }
        filtered.forEach(moduleComponent -> moduleComponent.keyTyped(typedChar,keyCode));
        super.keyTyped(typedChar, keyCode);
    }
    private void deleteLastCharacter() {
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
        }
    }
    private StringBuilder breakAndAddWord(String word, StringBuilder currentLine, float maxWidth, List<String> lines) {
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            String nextPart = currentLine.toString() + c;
            if (FontManager.SEMIBOLD.get(18).getStringWidth(nextPart) <= maxWidth) {
                currentLine.append(c);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(String.valueOf(c));
            }
        }
        return currentLine;
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (RenderUtil.isHovering(getPosX() + 140, getPosY() + 49, 380, 368, mx, my) && moduleComponents.stream().noneMatch(moduleComponent -> moduleComponent.getComponents().stream().anyMatch(component -> component.isHovered(mx,my)))) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }
}
