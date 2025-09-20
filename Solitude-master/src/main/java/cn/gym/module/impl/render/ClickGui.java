package cn.gym.module.impl.render;

import cn.gym.Solitude;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.value.impl.ModeValue;
import org.lwjgl.input.Keyboard;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 10:56
 */
public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui",Category.Render);
        setKey(Keyboard.KEY_RSHIFT);
    }

    public static ModeValue modeValue = new ModeValue("Mode","DropDown",new String[]{"DropDown","Neverlose"});

    @Override
    public void onEnable() {
        switch (modeValue.getValue()){
            case "DropDown":
                mc.displayGuiScreen(Solitude.Instance.dropDownClickGui);
                break;
            case "Neverlose":
                mc.displayGuiScreen(Solitude.Instance.neverLoseClickGui);
                break;
        }
        setState(false);
    }
}
