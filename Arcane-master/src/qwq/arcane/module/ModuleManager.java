package qwq.arcane.module;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.KeyPressEvent;
import qwq.arcane.event.impl.events.render.ChatGUIEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.impl.combat.AntiBot;
import qwq.arcane.module.impl.combat.AntiKB;
import qwq.arcane.module.impl.combat.AutoWeapon;
import qwq.arcane.module.impl.combat.BackTrack;
import qwq.arcane.module.impl.combat.Gapple;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.combat.ThrowableAura;
import qwq.arcane.module.impl.combat.TickBase;
import qwq.arcane.module.impl.combat.Wtap;
import qwq.arcane.module.impl.display.ArrayList;
import qwq.arcane.module.impl.display.EffectHUD;
import qwq.arcane.module.impl.display.Inventory;
import qwq.arcane.module.impl.display.Notification;
import qwq.arcane.module.impl.display.Session;
import qwq.arcane.module.impl.display.TargetRender;
import qwq.arcane.module.impl.misc.AutoPlay;
import qwq.arcane.module.impl.misc.ClientSpoofer;
import qwq.arcane.module.impl.misc.FakeLag;
import qwq.arcane.module.impl.misc.NoRotate;
import qwq.arcane.module.impl.misc.Teams;
import qwq.arcane.module.impl.misc.Timer;
import qwq.arcane.module.impl.movement.GuiMove;
import qwq.arcane.module.impl.movement.LongJump;
import qwq.arcane.module.impl.movement.NoJumpDelay;
import qwq.arcane.module.impl.movement.Noslow;
import qwq.arcane.module.impl.movement.Speed;
import qwq.arcane.module.impl.movement.Sprint;
import qwq.arcane.module.impl.movement.TargetStrafe;
import qwq.arcane.module.impl.player.AntiVoid;
import qwq.arcane.module.impl.player.AutoTool;
import qwq.arcane.module.impl.player.BedNuker;
import qwq.arcane.module.impl.player.Blink;
import qwq.arcane.module.impl.player.InvManager;
import qwq.arcane.module.impl.player.NoFall;
import qwq.arcane.module.impl.player.Stealer;
import qwq.arcane.module.impl.visuals.Animations;
import qwq.arcane.module.impl.visuals.Atmosphere;
import qwq.arcane.module.impl.visuals.BlockOverlay;
import qwq.arcane.module.impl.visuals.Breadcrumbs;
import qwq.arcane.module.impl.visuals.Camera;
import qwq.arcane.module.impl.visuals.Chams;
import qwq.arcane.module.impl.visuals.ClickGui;
import qwq.arcane.module.impl.visuals.ContainerESP;
import qwq.arcane.module.impl.visuals.ESP;
import qwq.arcane.module.impl.visuals.FreeLook;
import qwq.arcane.module.impl.visuals.FullBright;
import qwq.arcane.module.impl.visuals.Hat;
import qwq.arcane.module.impl.visuals.Health;
import qwq.arcane.module.impl.visuals.Hitmarkers;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.module.impl.visuals.ItemESP;
import qwq.arcane.module.impl.visuals.KillEffect;
import qwq.arcane.module.impl.visuals.MotionBlur;
import qwq.arcane.module.impl.visuals.NoHurtCam;
import qwq.arcane.module.impl.visuals.Projectile;
import qwq.arcane.module.impl.world.BlockFly;
import qwq.arcane.module.impl.world.ChestAura;
import qwq.arcane.module.impl.world.Disabler;
import qwq.arcane.module.impl.world.FastPlace;
import qwq.arcane.module.impl.world.PlayerTracker;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/ModuleManager.class */
public class ModuleManager {
    private final Map<Class<? extends Module>, Module> modules = new ConcurrentHashMap<>();

    public void Init() {
        Client.Instance.getEventManager().register(this);
        registerModule(new AntiBot());
        registerModule(new AutoWeapon());
        registerModule(new BackTrack());
        registerModule(new Wtap());
        registerModule(new Gapple());
        registerModule(new KillAura());
        registerModule(new ThrowableAura());
        registerModule(new TickBase());
        registerModule(new AntiKB());
        registerModule(new AutoPlay());
        registerModule(new ClientSpoofer());
        registerModule(new FakeLag());
        registerModule(new NoRotate());
        registerModule(new Timer());
        registerModule(new Teams());
        registerModule(new GuiMove());
        registerModule(new LongJump());
        registerModule(new NoJumpDelay());
        registerModule(new Noslow());
        registerModule(new TargetStrafe());
        registerModule(new Speed());
        registerModule(new Sprint());
        registerModule(new AntiVoid());
        registerModule(new Blink());
        registerModule(new AutoTool());
        registerModule(new BedNuker());
        registerModule(new InvManager());
        registerModule(new Stealer());
        registerModule(new NoFall());
        registerModule(new Animations());
        registerModule(new Atmosphere());
        registerModule(new BlockOverlay());
        registerModule(new Breadcrumbs());
        registerModule(new Camera());
        registerModule(new Chams());
        registerModule(new ClickGui());
        registerModule(new ContainerESP());
        registerModule(new ESP());
        registerModule(new FreeLook());
        registerModule(new FullBright());
        registerModule(new Hat());
        registerModule(new Health());
        registerModule(new Hitmarkers());
        registerModule(new InterFace());
        registerModule(new ItemESP());
        registerModule(new KillEffect());
        registerModule(new MotionBlur());
        registerModule(new NoHurtCam());
        registerModule(new Projectile());
        registerModule(new Disabler());
        registerModule(new FastPlace());
        registerModule(new ChestAura());
        registerModule(new PlayerTracker());
        registerModule(new Scaffold());
        registerModule(new BlockFly());
        registerModule(new ArrayList());
        registerModule(new Notification());
        registerModule(new EffectHUD());
        registerModule(new Inventory());
        registerModule(new TargetRender());
        registerModule(new Session());
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
        return Collections.unmodifiableCollection(this.modules.values());
    }

    public Module getModule(String name) {
        for (Module module : this.modules.values()) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public <T extends Module> T getModule(Class<T> cls) {
        return cls.cast(this.modules.get(cls));
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

    public Collection<ModuleWidget> getAllWidgets() {
        return modules.values().stream()
                .filter(ModuleWidget.class::isInstance)
                .map(ModuleWidget.class::cast)
                .collect(Collectors.toList());
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        for (Module module : this.modules.values()) {
            if ((module instanceof ModuleWidget) && module.getState()) {
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
        for (Module module : this.modules.values()) {
            if ((module instanceof ModuleWidget) && module.getState()) {
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
        Iterator<Module> it = this.modules.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Module module = it.next();
            if ((module instanceof ModuleWidget) && module.getState()) {
                ModuleWidget widget = (ModuleWidget) module;
                if (widget.shouldRender() && widget.dragging) {
                    draggingWidget = widget;
                    break;
                }
            }
        }
        for (Module module2 : this.modules.values()) {
            if ((module2 instanceof ModuleWidget) && module2.getState()) {
                ModuleWidget widget2 = (ModuleWidget) module2;
                if (widget2.shouldRender()) {
                    widget2.onChatGUI(event.getMouseX(), event.getMouseY(), draggingWidget == null || draggingWidget == widget2);
                    if (widget2.dragging) {
                        draggingWidget = widget2;
                    }
                }
            }
        }
    }
}
