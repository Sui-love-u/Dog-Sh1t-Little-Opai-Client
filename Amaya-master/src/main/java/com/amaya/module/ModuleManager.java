package com.amaya.module;

import com.amaya.events.EventManager;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.KeyEvent;
import com.amaya.module.impl.combat.*;
import com.amaya.module.impl.display.*;
import com.amaya.module.impl.display.ArrayList;
import com.amaya.module.impl.misc.*;
import com.amaya.module.impl.movement.*;
import com.amaya.module.impl.player.*;
import com.amaya.module.impl.render.*;
import com.amaya.module.impl.world.*;
import com.amaya.module.impl.world.Timer;
import com.amaya.module.setting.Setting;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class ModuleManager {
    private final Map<Class<? extends Module>, Module> modules = new ConcurrentHashMap<>();

    public void Init(String name){
        EventManager.register(this);
        //movement
        registerModule(new InventoryMove());
        registerModule(new NoSlow());
        registerModule(new NoWeb());
        registerModule(new LongJump());
        registerModule(new TargetStrafe());
        registerModule(new Speed());
        registerModule(new Sprint());
        registerModule(new Eagle());
        registerModule(new NoJumpDelay());
        //world
        registerModule(new Scaffold());
        registerModule(new Timer());
        registerModule(new Ambience());
        registerModule(new AutoPearl());
        registerModule(new Disabler());
        registerModule(new BlockFly());
        registerModule(new BedBeaker());
        registerModule(new FastPlace());
        //combat
        registerModule(new KillAura());
        registerModule(new AutoWeapon());
        registerModule(new NoClickDelay());
        registerModule(new BackTrack());
        registerModule(new ThrowerAura());
        registerModule(new SuperKnockBack());
        registerModule(new Gapple());
        registerModule(new Velocity());
        //misc
        registerModule(new IRC());
        registerModule(new Teams());
        registerModule(new MCF());
        registerModule(new Spammer());
        registerModule(new Protocol());
        registerModule(new AntiBot());
        //player
        registerModule(new AutoTool());
        registerModule(new AutoPlay());
        registerModule(new ChestAura());
        registerModule(new AntiVoid());
        registerModule(new Stuck());
        registerModule(new Blink());
        registerModule(new InvManager());
        registerModule(new NoFall());
        registerModule(new ChestStealer());
        //display
        registerModule(new GameInfo());
        registerModule(new ArrayList());
        registerModule(new Inventory());
        registerModule(new Notification());
        registerModule(new PotionHUD());
        registerModule(new TargetHUD());
        //render
        registerModule(new Animations());
        registerModule(new HUD());
        registerModule(new Camera());
        registerModule(new Health());
        registerModule(new Projectile());
        registerModule(new ItemESP());
        registerModule(new PostProcessing());
        registerModule(new MotionBlur());
        registerModule(new ContainerESP());
        registerModule(new BlockOverlay());
        registerModule(new Chams());
        registerModule(new Breadcrumbs());
        registerModule(new MotionDisabler());
        registerModule(new Hat());
        registerModule(new Cape());
        registerModule(new NoHurtCam());
        registerModule(new BrightPlayers());
        registerModule(new FullBright());
        registerModule(new ESP());
        registerModule(new ClickGui());
    }

    public void registerModule(Module module) {
        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Setting) module.getSettings().add((Setting<?>) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        modules.put(module.getClass(), module);
    }

    public Collection<Module> getAllModules() {
        return Collections.unmodifiableCollection(modules.values());
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
    public void onKey(KeyEvent e) {
        modules.values().stream().filter(module -> module.getKey() == e.getKeyCode() && e.getKeyCode() != -1)
                .forEach(Module::toggle);
    }
}