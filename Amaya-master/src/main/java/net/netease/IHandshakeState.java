/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

public interface IHandshakeState<S> {
    public void accept(int var1, ByteBuf var2, Consumer<? super S> var3);
}

