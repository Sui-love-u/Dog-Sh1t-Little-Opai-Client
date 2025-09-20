package com.amaya.module.impl.display;

import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.NumberSetting;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
@ModuleInfo(name = "Inventory",category = Category.Display)
public class Inventory extends Module {
    public NumberSetting radius = new NumberSetting("radius",3,0,8,1);
}
