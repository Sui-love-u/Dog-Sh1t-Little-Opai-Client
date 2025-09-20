package qwq.arcane.gui.clickgui.arcane.component;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.gui.clickgui.arcane.component.settings.BooleanComponent;
import qwq.arcane.gui.clickgui.arcane.component.settings.ColorPickerComponent;
import qwq.arcane.gui.clickgui.arcane.component.settings.ModeComponent;
import qwq.arcane.gui.clickgui.arcane.component.settings.MultiBoxComponent;
import qwq.arcane.gui.clickgui.arcane.component.settings.NumberComponent;
import qwq.arcane.gui.clickgui.arcane.component.settings.StringComponent;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.Value;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.MultiBooleanValue;
import qwq.arcane.value.impl.NumberValue;
import qwq.arcane.value.impl.TextValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/component/ModuleComponent.class */
public class ModuleComponent extends Component {
    private final Module module;
    private int scroll = 0;
    private boolean left = true;
    private final ObjectArrayList<Component> components = new ObjectArrayList<>();
    private final Animation enabled = new DecelerateAnimation(250, 1.0d);
    private final Animation hover = new DecelerateAnimation(250, 1.0d);

    public Module getModule() {
        return this.module;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }

    public int getScroll() {
        return this.scroll;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isLeft() {
        return this.left;
    }

    public ObjectArrayList<Component> getComponents() {
        return this.components;
    }

    public Animation getEnabled() {
        return this.enabled;
    }

    public Animation getHover() {
        return this.hover;
    }

    public ModuleComponent(Module module) {
        this.module = module;
        for (Value setting : module.getSettings()) {
            if (setting instanceof BoolValue) {
                BoolValue bool = (BoolValue) setting;
                this.components.add(new BooleanComponent(bool));
            } else if (setting instanceof NumberValue) {
                NumberValue number = (NumberValue) setting;
                this.components.add(new NumberComponent(number));
            } else if (setting instanceof ModeValue) {
                ModeValue modeValue = (ModeValue) setting;
                this.components.add(new ModeComponent(modeValue));
            } else if (setting instanceof MultiBooleanValue) {
                MultiBooleanValue booleanValue = (MultiBooleanValue) setting;
                this.components.add(new MultiBoxComponent(booleanValue));
            } else if (setting instanceof ColorValue) {
                ColorValue colorValue = (ColorValue) setting;
                this.components.add(new ColorPickerComponent(colorValue));
            } else if (setting instanceof TextValue) {
                TextValue textValue = (TextValue) setting;
                this.components.add(new StringComponent(textValue));
            }
        }
        this.enabled.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        float y = getY() + 6.0f + this.scroll;
        this.enabled.setDirection(this.module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        this.hover.setDirection(RenderUtil.isHovering(getX() + 135.0f, y + 4.0f, 22.0f, 12.0f, mouseX, mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        Animation moduleAnimation = this.module.getAnimations();
        moduleAnimation.setDirection(this.module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (this.module.getState() || moduleAnimation.finished(Direction.BACKWARDS)) {
        }
        RoundedUtil.drawRound(getX(), y, 165.0f, getHeight(), 2.0f, INSTANCE.getArcaneClickGui().backgroundColor);
        FontManager.Bold.get(18.0f).drawString(this.module.name + " Module", getX() + 10.0f, y + 5.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 0.6f));
        RoundedUtil.drawRound(getX() + 135.0f, y + 4.0f, 20.0f, 10.0f, 4.0f, ColorUtil.applyOpacity(InterFace.color(1), 0.4f));
        RenderUtil.drawCircleCGUI(getX() + 141.0f + (moduleAnimation.getOutput().floatValue() * 9.0f), y + 9.0f, 8.0f, InterFace.color(1).darker().getRGB());
        RoundedUtil.drawRound(getX() + 10.0f, y + 20.0f, 145.0f, 1.0f, 0.0f, INSTANCE.getArcaneClickGui().linecolor);
        float componentY = y + 22.0f;
        ObjectArrayList<Component> filtered = (ObjectArrayList) this.components.stream().filter((v0) -> {
            return v0.isVisible();
        }).collect(ObjectArrayList::new, (v0, v1) -> {
            v0.add(v1);
        }, (v0, v1) -> {
            v0.addAll(v1);
        });
        ObjectListIterator it = filtered.iterator();
        while (it.hasNext()) {
            Component component = (Component) it.next();
            component.setX(getX());
            component.setY(componentY);
            component.drawScreen(mouseX, mouseY);
            componentY += component.getHeight();
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float y = getY() + 6.0f + this.scroll;
        if (RenderUtil.isHovering(getX() + 135.0f, y + 4.0f, 20.0f, 10.0f, mouseX, mouseY) && mouseButton == 0) {
            this.module.toggle();
        }
        ObjectListIterator it = this.components.iterator();
        while (it.hasNext()) {
            Component component = (Component) it.next();
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        ObjectListIterator it = this.components.iterator();
        while (it.hasNext()) {
            Component component = (Component) it.next();
            component.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        ObjectListIterator it = this.components.iterator();
        while (it.hasNext()) {
            Component component = (Component) it.next();
            component.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public int getMaxScroll() {
        return (int) (((getY() - INSTANCE.getArcaneClickGui().getY()) + getHeight()) * 4.0f);
    }
}
