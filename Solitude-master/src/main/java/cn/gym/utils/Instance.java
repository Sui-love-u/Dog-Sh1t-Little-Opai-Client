package cn.gym.utils;

import cn.gym.Solitude;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.fontrender.FontManager;
import net.minecraft.client.Minecraft;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 00:47
 */
public interface Instance {
    Solitude INSTANCE = Solitude.Instance;
    Minecraft mc = Minecraft.getMinecraft();

    FontManager Regular = FontManager.Regular;
    FontManager Mc = FontManager.Mc;
    FontManager Semibold = FontManager.Semibold;
    FontManager Bold = FontManager.Bold;
    FontManager Light = FontManager.Light;
    FontManager Icon = FontManager.ICON;

}
