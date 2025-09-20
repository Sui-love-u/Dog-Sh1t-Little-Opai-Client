package cn.gym.module.impl.render;

import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.NumberValue;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
public class Camera extends Module {
    public final BooleanValue noFovValue = new BooleanValue("NoFov", false);
    public final NumberValue fovValue = new NumberValue("Fov", 1.0, 0.0, 4.0, 0.1);
    public final BooleanValue motionCamera = new BooleanValue("Motion Camera", true);
    public final NumberValue interpolation = new NumberValue("Interpolation", 0.01, 0.01, 0.4, 0.01);

    public Camera() {
        super("Camera",Category.Render);
    }
}
