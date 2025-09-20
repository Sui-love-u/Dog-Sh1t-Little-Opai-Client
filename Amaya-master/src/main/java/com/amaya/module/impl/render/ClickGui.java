package com.amaya.module.impl.render;

import com.amaya.Amaya;
import com.amaya.gui.clickgui.dropdown.DropDownClickGui;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ColorSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/4/22
 */
@ModuleInfo(name = "ClickGui",category = Category.Render)
public class ClickGui extends Module {
    public ClickGui(){
        setKey(Keyboard.KEY_RSHIFT);
    }
    public static ModeSetting mod = new ModeSetting("mod","DropDown",new String[]{"DropDown","NeverLose"}) ;
    public static final BooleanSetting Darkmode = new BooleanSetting("Dark mode",false);
    public static NumberSetting alpha = new NumberSetting("Alpha", 100.0, 0.0, 255.0, 1.0);
    public static NumberSetting radius = new NumberSetting("Radius", 6.0, 0.0, 10.0, 1.0);
    public static ModeSetting color2 = new ModeSetting("ColorMode","Custom",new String[]{"Custom", "Rainbow", "Dynamic", "Fade","Astolfo","Tenacity"}) ;
    public final ColorSetting FirstColor = new ColorSetting("Color", new Color(255, 136, 185));
    public final ColorSetting color = new ColorSetting("FirstColor", new Color(128, 128, 255));
    public static final ColorSetting SecondColor = new ColorSetting("SecondColor", new Color(255, 255, 255));
    public final NumberSetting fadeSpeed = new NumberSetting("ColorSpeed",()-> color2.is("Dynamic") || color2.is("Fade") || color2.is("Tenacity"),15.0, 1.0, 30.0, 1.0);
    public final NumberSetting colorIndex = new NumberSetting("Color Seperation", 20.0,5.0, 100.0, 1.0);
    public final NumberSetting colorcounter = new NumberSetting("counter", ()-> color2.is("Dynamic") || color2.is("Fade") || color2.is("Astolfo"),1.0, 1.0, 20.0, 0.1);


    @Override
    public void onEnable() {
        switch (mod.get()){
            case "DropDown":
                mc.displayGuiScreen(Amaya.Instance.dropDownClickGui);
                break;
            case "NeverLose":
                mc.displayGuiScreen(Amaya.Instance.negative);
                break;
        }
        this.setState(false);
    }
}
