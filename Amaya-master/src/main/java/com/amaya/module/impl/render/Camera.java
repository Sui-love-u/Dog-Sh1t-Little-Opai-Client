package com.amaya.module.impl.render;

import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Camera",category = Category.Render)
public class Camera extends Module {
    public final BooleanSetting noFovValue = new BooleanSetting("NoFov", false);
    public final NumberSetting fovValue = new NumberSetting("Fov", 1.0, 0.0, 4.0, 0.1);
    public final BooleanSetting motionCamera = new BooleanSetting("Motion Camera", true);
    public final NumberSetting interpolation = new NumberSetting("Motion Interpolation", 0.01, 0.01, 0.4, 0.01);
}
