package cn.gym.module;

import cn.gym.Solitude;
import cn.gym.events.EventManager;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.KeyPressEvent;
import cn.gym.events.impl.render.ChatGUIEvent;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.impl.combat.*;
import cn.gym.module.impl.display.*;
import cn.gym.module.impl.misc.Disabler;
import cn.gym.module.impl.movement.*;
import cn.gym.module.impl.player.*;
import cn.gym.module.impl.render.*;
import cn.gym.value.Value;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 13:06
 */
public class ModuleManager {
    private final Map<Class<? extends Module>, Module> modules = new ConcurrentHashMap<>();

    public void Init() {
        EventManager.register(this);
        registerModule(new Interface());
        registerModule(new AutoWeapon());
        registerModule(new Chams());
        registerModule(new FullBright());
        registerModule(new BlockOverlay());
        registerModule(new Health());
        registerModule(new MotionBlur());
        registerModule(new KillEffect());
        registerModule(new Animations());
        registerModule(new Projectile());
        registerModule(new AntiVoid());
        registerModule(new NoSlow());
        registerModule(new Sprint());
        registerModule(new Scaffold());
        registerModule(new ContainerESP());
        registerModule(new Camera());
        registerModule(new NoHurtCam());
        registerModule(new EffectHUD());
        registerModule(new InvManager());
        registerModule(new ESP());
        registerModule(new Disabler());
        registerModule(new AutoTool());
        registerModule(new InventoryMove());
        registerModule(new Stealer());
        registerModule(new ItemESP());
        registerModule(new ArrayList());
        registerModule(new Notification());
        registerModule(new IsLand());
        registerModule(new Velocity());
        registerModule(new AntiBot());
        registerModule(new NoRotate());
        registerModule(new NoFall());
        registerModule(new Speed());
        registerModule(new TargetRender());
        registerModule(new Scoreboard());
        registerModule(new KillAura());
        registerModule(new AutoClicker());
        registerModule(new Inventory());
        registerModule(new Session());
        registerModule(new ClickGui());
    }
    public void registerModule(Module module) {
        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Value<?>) module.getSettings().add((Value<?>) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        modules.put(module.getClass(), module);
    }

    public Collection<Module> getAllModules() {
        return Collections.unmodifiableCollection(modules.values());
    }

    public Collection<ModuleWidget> getAllWidgets() {
        return modules.values().stream()
                .filter(ModuleWidget.class::isInstance)
                .map(ModuleWidget.class::cast)
                .collect(Collectors.toList());
    }

    public Module getModule(String name) {
        for (Module module : modules.values()) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    public <T extends Module> T getModule(Class<T> cls) {
        return cls.cast(modules.get(cls));
    }

    public List<Module> getModsByCategory(Category m) {
        return modules.values().stream()
                .filter(module -> module.getCategory() == m)
                .collect(Collectors.toList());
    }

    @EventTarget
    public void onKey(KeyPressEvent e) {
        modules.values().stream().filter(module -> module.getKey() == e.getKey() && e.getKey() != -1)
                .forEach(Module::toggle);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        for (Module module : modules.values()) {
            if (module instanceof ModuleWidget && module.getState()) {
                ModuleWidget widget = (ModuleWidget) module;
                if (widget.shouldRender()) {
                    widget.updatePos();
                    widget.render();
                }
            }
        }
    }

    @EventTarget
    public void onShader2D(Shader2DEvent event) {
        for (Module module : modules.values()) {
            if (module instanceof ModuleWidget && module.getState()) {
                ModuleWidget widget = (ModuleWidget) module;
                if (widget.shouldRender()) {
                    widget.onShader(event);
                }
            }
        }
    }

    @EventTarget
    public void onChatGUI(ChatGUIEvent event) {
        ModuleWidget draggingWidget = null;
        for (Module module : modules.values()) {
            if (module instanceof ModuleWidget && module.getState()) {
                ModuleWidget widget = (ModuleWidget) module;
                if (widget.shouldRender() && widget.dragging) {
                    draggingWidget = widget;
                    break;
                }
            }
        }

        for (Module module : modules.values()) {
            if (module instanceof ModuleWidget && module.getState()) {
                ModuleWidget widget = (ModuleWidget) module;
                if (widget.shouldRender()) {
                    widget.onChatGUI(event.getMouseX(), event.getMouseY(),
                            (draggingWidget == null || draggingWidget == widget));
                    if (widget.dragging) draggingWidget = widget;
                }
            }
        }
    }
}
