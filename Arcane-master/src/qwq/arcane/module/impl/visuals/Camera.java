package qwq.arcane.module.impl.visuals;

import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Camera.class */
public class Camera extends Module {
    public final BoolValue noFovValue;
    public final NumberValue fovValue;
    public final BoolValue motionCamera;
    public final NumberValue interpolation;

    public Camera() {
        super("Camera", Category.Visuals);
        this.noFovValue = new BoolValue("NoFov", false);
        this.fovValue = new NumberValue("Fov", 1.0d, 0.0d, 4.0d, 0.1d);
        this.motionCamera = new BoolValue("Motion Camera", true);
        this.interpolation = new NumberValue("Interpolation", 0.01d, 0.01d, 0.4d, 0.01d);
    }
}
