/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.gui;

import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.animations.impl.RippleAnimation;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.packet.impl.Packet26;

import java.util.HashMap;
import java.util.List;

public class GermGameSubElement {
    private final int A;
    private final String sid;
    private final String name;
    private final List<String> desc;
    private final Animation hoverAnim = new DecelerateAnimation(300, 1.0);
    private RippleAnimation animation;
    private Runnable runnable;

    public GermGameSubElement(int index, String sid, String name, List<String> desc) {
        this.A = index;
        this.sid = sid;
        this.name = name;
        this.desc = desc;
        this.animation = new RippleAnimation();
    }

    public void joinGame(String guiName) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("entry", this.A);
        data.put("sid", this.sid);
        String json = GsonUtil.toJson(data);
        String message = new StringBuilder().insert(0, "GUI$").append(guiName).append("@").append("entry/").append(this.A).toString();
        PacketProcessor.INSTANCE.setLastGameElement(this);
        PacketProcessor.INSTANCE.sendPacket(new Packet26(message, json));
    }

    public int getIndex() {
        return this.A;
    }

    public void setAnimation(RippleAnimation animation) {
        this.animation = animation;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public int getA() {
        return this.A;
    }

    public String getSid() {
        return this.sid;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDesc() {
        return this.desc;
    }

    public Animation getHoverAnim() {
        return this.hoverAnim;
    }

    public RippleAnimation getAnimation() {
        return this.animation;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }
}

