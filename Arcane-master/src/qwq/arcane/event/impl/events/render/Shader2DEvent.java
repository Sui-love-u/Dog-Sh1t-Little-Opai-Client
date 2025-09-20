package qwq.arcane.event.impl.events.render;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/render/Shader2DEvent.class */
public class Shader2DEvent extends CancellableEvent {
    private ShaderType shaderType;

    /* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/render/Shader2DEvent$ShaderType.class */
    public enum ShaderType {
        BLUR,
        SHADOW,
        GLOW
    }

    public void setShaderType(ShaderType shaderType) {
        this.shaderType = shaderType;
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }

    public Shader2DEvent(ShaderType shaderType) {
        this.shaderType = shaderType;
    }
}
