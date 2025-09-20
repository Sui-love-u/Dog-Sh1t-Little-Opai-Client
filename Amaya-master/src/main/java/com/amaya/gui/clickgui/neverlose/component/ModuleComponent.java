package com.amaya.gui.clickgui.neverlose.component;

import com.amaya.gui.clickgui.neverlose.component.settings.*;
import com.amaya.module.setting.Setting;
import com.amaya.module.setting.impl.*;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.module.Module;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import com.amaya.gui.clickgui.neverlose.Component;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static com.amaya.gui.clickgui.neverlose.NegativeClickGui.*;

/**
 * @Author: Guyuemang
 */
@Getter
public class ModuleComponent extends Component {
    private Module module;
    @Setter
    private int scroll = 0;
    @Setter
    private boolean left = true;
    private final ObjectArrayList<Component> components = new ObjectArrayList<>();
    private final Animation enabled = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);

    public ModuleComponent(Module module) {
        this.module = module;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting bool) {
                components.add(new BoolValueComponent(bool));
            }
            if (setting instanceof ModeSetting modeValue) {
                components.add(new ModeComponent(modeValue));
            }
            if (setting instanceof ColorSetting colorValue) {
                components.add(new ColorPickerComponent(colorValue));
            }
            if (setting instanceof NumberSetting number) {
                components.add(new NumberComponent(number));
            }
            if (setting instanceof EnumSetting modes) {
                components.add(new MultiBoxComponent(modes));
            }
            if (setting instanceof StringSetting string) {
                components.add(new StringComponent(string));
            }
        }
        enabled.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        float y = getY() + 6 + scroll;
        RoundedUtil.drawRoundOutline(getX(),y + 10,180,getHeight(),4,0.1f,bgcolor5,bgcolor6);
        enabled.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(RenderUtil.isHovering(getX() + 154,y + 16,20,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        FontManager.REGULAR.get(18).drawString(module.getName().toUpperCase(),getX() + 4,y,textcolor.getRGB());

        RoundedUtil.drawRound(getX() + 153,y + 15,20,12,6,new Color(ColorUtil.interpolateColor2(new Color(ColorUtil.interpolateColor2(bgcolor4,lineColor,(float) enabled.getOutput().floatValue())),
                new Color(ColorUtil.interpolateColor2(bgcolor6,bgcolor6,(float) enabled.getOutput().floatValue())).brighter().brighter(), (float) hover.getOutput().floatValue())));

        RenderUtil.drawCircleCGUI(getX() + 159 + 8 * (float) enabled.getOutput().floatValue(),y + 21,12, ColorUtil.interpolateColor2(new Color(ColorUtil.interpolateColor2(lineColor,circlecolor,(float) enabled.getOutput().floatValue())),
                new Color(ColorUtil.interpolateColor2(bgcolor7.darker().darker(),bgcolor7,(float) enabled.getOutput().floatValue())).brighter().brighter(), (float) hover.getOutput().floatValue()));

        FontManager.SEMIBOLD.get(20).drawString("Enable",getX() + 6,y + 17,textcolor.getRGB());
        float componentY = y + 22;
        ObjectArrayList<Component> filtered = components.stream()
                .filter(Component::isVisible)
                .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
        for (Component component : filtered) {
            component.setX(getX());
            component.setY(componentY);
            component.drawScreen(mouseX, mouseY);
            componentY += component.getHeight();
        }
        super.drawScreen(mouseX, mouseY);
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 153,getY() + scroll + 20,20,12,mouseX,mouseY) && mouseButton == 0){
            module.toggle();
        }
        for (Component component : components) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (Component component : components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (Component component : components) {
            component.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }
    public int getMaxScroll() {
        return (int) (((getY() - INSTANCE.getNegative().getPosY()) + getHeight()) * 4);
    }
}
