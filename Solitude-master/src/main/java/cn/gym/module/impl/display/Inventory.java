package cn.gym.module.impl.display;

import cn.gym.Solitude;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.Category;
import cn.gym.module.ModuleWidget;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:35
 */
public class Inventory extends ModuleWidget {
    public Inventory() {
        super("Inventory",Category.Display);
    }
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal","Custom","Solitude"});

    @Override
    public void onShader(Shader2DEvent event) {
        float x = renderX;
        float y = renderY;
        float itemWidth = 14;
        float itemHeight = 14;
        float y1 = 17.0F;
        float x1 = 0.7F;
        switch (modeValue.getValue()) {
            case "Solitude":
                RoundedUtil.drawRound(x,y, itemWidth + 120, 65, Interface.radius.get().intValue(), new Color(255, 255, 255, 255));
                break;
            case "Custom":
                RenderUtil.drawRect(x,y, itemWidth + 120, 65, new Color(1,1,1,255));
                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
                    x += itemWidth;
                    x += x1;
                    if (i == 17) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 8.5F;
                    }

                    if (i == 26) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 9.0F;
                    }
                }
                width = (itemWidth * 9.1F + x1 * 9.0F);
                height = (itemHeight * 3.0F + 19.0F);
                break;
            case "Normal":
                RoundedUtil.drawRound(x,y, itemWidth + 120, 65, Interface.radius.get().intValue(), new Color(0, 0, 0, 89));
                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
                    x += itemWidth;
                    x += x1;
                    if (i == 17) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 8.5F;
                    }

                    if (i == 26) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 9.0F;
                    }
                }
                width = (itemWidth * 9.1F + x1 * 9.0F);
                height = (itemHeight * 3.0F + 19.0F);
                break;
        }
    }

    Interface setting = Solitude.Instance.getModuleManager().getModule(Interface.class);
    @Override
    public void render() {
        float x = renderX;
        float y = renderY;
        float itemWidth = 14;
        float itemHeight = 14;
        float y1 = 17.0F;
        float x1 = 0.7F;
        switch (modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x,y, itemWidth + 120, 65, new Color(255, 255, 255, 89));
                RenderUtil.drawRect(x,y, itemWidth + 120, 15, new Color(255, 255, 255, 89));
                Semibold.get(18).drawCenteredString("Inventory",x + itemWidth / 2 + 60,y + 5, new Color(255, 255, 255).getRGB());
                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.drawRect(x + 1.7F, y + 17.5F,13,13, new Color(255, 255, 255, 30));
                    RenderUtil.renderItemStack(slot, x + 1.7F, y + 17.5F, 0.80F);
                    x += itemWidth;
                    x += x1;
                    if (i == 17) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 8.5F;
                    }

                    if (i == 26) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 9.0F;
                    }
                }
                break;
            case "Custom":
                RenderUtil.drawRect(x,y, itemWidth + 120, 65, new Color(1,1,1,100));
                Mc.get(18).drawString("Inventory",x + 5,y + 5,-1);
                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
                    x += itemWidth;
                    x += x1;
                    if (i == 17) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 8.5F;
                    }

                    if (i == 26) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 9.0F;
                    }
                }
                width = (itemWidth * 9.1F + x1 * 9.0F);
                height = (itemHeight * 3.0F + 19.0F);
                break;
            case "Normal":
                RoundedUtil.drawRound(x,y, itemWidth + 120, 65, Interface.radius.get().intValue(), new Color(0, 0, 0, 89));
                RenderUtil.startGlScissor((int) (x - 2), (int) (y - 1), 159, 18);
                RoundedUtil.drawRound(x,y, itemWidth + 120, 29, Interface.radius.get().intValue(), ColorUtil.applyOpacity(new Color(setting.color(1)), (float) 0.3f));
                RenderUtil.stopGlScissor();
                FontManager.Bold.get(18).drawString("Inventory",x + 5,y + 5,-1);
                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
                    x += itemWidth;
                    x += x1;
                    if (i == 17) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 8.5F;
                    }

                    if (i == 26) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 9.0F;
                    }
                }
                width = (itemWidth * 9.1F + x1 * 9.0F);
                height = (itemHeight * 3.0F + 19.0F);
            break;
        }
    }

    @Override
    public boolean shouldRender() {
        return getState() && setting.getState();
    }
}
