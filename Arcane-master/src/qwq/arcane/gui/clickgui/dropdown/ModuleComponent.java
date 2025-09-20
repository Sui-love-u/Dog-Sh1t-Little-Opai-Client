package qwq.arcane.gui.clickgui.dropdown;

import java.awt.Color;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.gui.clickgui.IComponent;
import qwq.arcane.gui.clickgui.dropdown.setting.impl.BooleanComponent;
import qwq.arcane.gui.clickgui.dropdown.setting.impl.ColorPickerComponent;
import qwq.arcane.gui.clickgui.dropdown.setting.impl.ModeComponent;
import qwq.arcane.gui.clickgui.dropdown.setting.impl.MultiBooleanComponent;
import qwq.arcane.gui.clickgui.dropdown.setting.impl.SliderComponent;
import qwq.arcane.gui.clickgui.dropdown.setting.impl.StringComponent;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.EaseInOutQuad;
import qwq.arcane.utils.animations.impl.EaseOutSine;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.value.Value;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.MultiBooleanValue;
import qwq.arcane.value.impl.NumberValue;
import qwq.arcane.value.impl.TextValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/ModuleComponent.class */
public class ModuleComponent implements IComponent {
    private float x;
    private float y;
    private float width;
    private final Module module;
    private boolean opened;
    private float height = 19.0f;
    private final EaseInOutQuad openAnimation = new EaseInOutQuad(250, 1.0d);
    private final EaseOutSine toggleAnimation = new EaseOutSine(300, 1.0d);
    private final EaseOutSine hoverAnimation = new EaseOutSine(200, 1.0d);
    private final CopyOnWriteArrayList<Component> settings = new CopyOnWriteArrayList<>();

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Module getModule() {
        return this.module;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public EaseInOutQuad getOpenAnimation() {
        return this.openAnimation;
    }

    public EaseOutSine getToggleAnimation() {
        return this.toggleAnimation;
    }

    public EaseOutSine getHoverAnimation() {
        return this.hoverAnimation;
    }

    public CopyOnWriteArrayList<Component> getSettings() {
        return this.settings;
    }

    public ModuleComponent(Module module) {
        this.module = module;
        this.openAnimation.setDirection(Direction.BACKWARDS);
        this.toggleAnimation.setDirection(Direction.BACKWARDS);
        this.hoverAnimation.setDirection(Direction.BACKWARDS);
        for (Value value : module.getSettings()) {
            if (value instanceof BoolValue) {
                BoolValue boolValue = (BoolValue) value;
                this.settings.add(new BooleanComponent(boolValue));
            } else if (value instanceof ModeValue) {
                ModeValue modeSetting = (ModeValue) value;
                this.settings.add(new ModeComponent(modeSetting));
            } else if (value instanceof NumberValue) {
                NumberValue numberSetting = (NumberValue) value;
                this.settings.add(new SliderComponent(numberSetting));
            } else if (value instanceof ColorValue) {
                ColorValue colorSetting = (ColorValue) value;
                this.settings.add(new ColorPickerComponent(colorSetting));
            } else if (value instanceof MultiBooleanValue) {
                MultiBooleanValue enumSetting = (MultiBooleanValue) value;
                this.settings.add(new MultiBooleanComponent(enumSetting));
            } else if (value instanceof TextValue) {
                TextValue textValue = (TextValue) value;
                this.settings.add(new StringComponent(textValue));
            }
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        float yOffset = 19.0f;
        this.openAnimation.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
        this.toggleAnimation.setDirection(this.module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        this.hoverAnimation.setDirection(isHovered(mouseX, mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        RenderUtil.drawRect(this.x, this.y, this.width, 19.0f, ColorUtil.applyOpacity(InterFace.mainColor.get().getRGB(), this.toggleAnimation.getOutput().floatValue()));
        FontManager.Bold.get((float) (14.0d - (1.0d * this.hoverAnimation.getOutput().doubleValue()))).drawCenteredString(this.module.getName(), this.x + (getWidth() / 2.0f), ((this.y + (19.0f / 2.0f)) - 3.0f) + (0.5d * this.hoverAnimation.getOutput().doubleValue()), new Color(234, 234, 234).getRGB());
        Iterator<Component> it = this.settings.iterator();
        while (it.hasNext()) {
            Component component = it.next();
            if (component.isVisible()) {
                component.setX(this.x);
                component.setY((float) (this.y + 2.0f + (yOffset * this.openAnimation.getOutput().doubleValue())));
                component.setWidth(this.width);
                if (this.openAnimation.getOutput().doubleValue() > 0.699999988079071d) {
                    component.drawScreen(mouseX, mouseY);
                }
                yOffset += (float) (component.getHeight() * this.openAnimation.getOutput().doubleValue());
                this.height = yOffset;
            }
        }
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws SecurityException {
        if (isHovered(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    this.module.toggle();
                    break;
                case 1:
                    this.opened = !this.opened;
                    break;
            }
        }
        if (this.opened && !isHovered(mouseX, mouseY)) {
            this.settings.forEach(setting -> {
                setting.mouseClicked(mouseX, mouseY, mouseButton);
            });
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.opened && !isHovered(mouseX, mouseY)) {
            this.settings.forEach(setting -> {
                setting.mouseReleased(mouseX, mouseY, state);
            });
        }
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        if (this.opened) {
            this.settings.forEach(setting -> {
                setting.keyTyped(typedChar, keyCode);
            });
        }
        IComponent.super.keyTyped(typedChar, keyCode);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return RenderUtil.isHovering(this.x + 2.0f, this.y, this.width - 2.0f, 17.0f, mouseX, mouseY);
    }
}
