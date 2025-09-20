/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.gui.clickgui.neverloseclickgui.component;

import cn.gym.gui.clickgui.neverloseclickgui.component.values.*;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.Value;
import cn.gym.value.impl.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import cn.gym.gui.clickgui.Component;
import lombok.Setter;
import java.awt.*;
import cn.gym.module.Module;

@Getter
public class ModuleComponent extends Component {
    private final Module module;
    @Setter
    private int scroll = 0;
    @Setter
    private boolean left = true;
    private final ObjectArrayList<Component> components = new ObjectArrayList<>();
    private final Animation enabled = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);
    public ModuleComponent(Module module) {
        this.module = module;
        for (Value setting : module.getSettings()) {
            if (setting instanceof BooleanValue bool) {
                components.add(new BooleanComponent(bool));
            }else if (setting instanceof NumberValue number) {
                components.add(new NumberComponent(number));
            }else if (setting instanceof ModeValue modeValue) {
                components.add(new ModeComponent(modeValue));
            }else if (setting instanceof MultiBooleanValue booleanValue) {
                components.add(new MultiBoxComponent(booleanValue));
            }else if (setting instanceof ColorValue colorValue) {
                components.add(new ColorPickerComponent(colorValue));
            }else if (setting instanceof TextValue textValue) {
                components.add(new StringComponent(textValue));
            }
        }
        enabled.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float y = getY() + 6 + scroll;
        enabled.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(RenderUtil.isHovering(getX() + 172, y + 15, 22, 12,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        Animation moduleAnimation = module.getAnimations();
        moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS));

        FontManager.Semibold.get(18).drawString(module.name, getX() + 5, y,new Color(255,255,255,160).getRGB());
        int visibleComponentsCount = (int)components.stream().filter(Component::isVisible).count();
        RoundedUtil.drawRoundOutline(getX(), y + 10, 200, getHeight(), 6, 0.2f, new Color(0x171717), new Color(0x2D2D2D));
        FontManager.Semibold.get(18).drawString("Enable", getX() + 5, y + 15 + 1.5f,ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) enabled.getOutput().floatValue()));
        RoundedUtil.drawRound(getX() + 172, y + 15, 22, 12, 5.5f, module.getState() ? new Color(ColorUtil.interpolateColor2(new Color(0x2B445D),new Color(0x3D6083),(float) hover.getOutput().floatValue())) : new Color(ColorUtil.interpolateColor2(new Color(0x2F3130),new Color(0x3F4241),(float) hover.getOutput().floatValue())));
        RenderUtil.drawCircleCGUI(getX() + 179 + moduleAnimation.getOutput().floatValue() * 9f, y + 21, 12, ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) enabled.getOutput().floatValue()));

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
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 172, getY() + scroll + 21, 22, 12,mouseX,mouseY) && mouseButton == 0){
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
        return (int) (((getY() - INSTANCE.getNeverLoseClickGui().getY()) + getHeight()) * 4);
    }
}
