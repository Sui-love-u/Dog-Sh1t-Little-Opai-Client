package com.amaya.gui.clickgui.dropdown;

import com.amaya.Amaya;
import com.amaya.gui.clickgui.neverlose.IComponent;
import com.amaya.gui.clickgui.neverlose.component.ModuleComponent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.utils.client.InstanceAccess;

/**
 * @Author: Guyuemang
 * 2025/5/17
 */
public class CategoryPanel implements IComponent, InstanceAccess{
    private final Category category;
    public CategoryPanel(Category category) {
        this.category = category;
    }
}
