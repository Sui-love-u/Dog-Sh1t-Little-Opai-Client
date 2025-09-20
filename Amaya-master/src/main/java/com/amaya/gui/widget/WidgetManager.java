/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package com.amaya.gui.widget;

import com.amaya.events.EventManager;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.render.ChatGUIEvent;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.gui.widget.impl.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WidgetManager {
    public List<Widget> widgetList = new ArrayList<>();

    public WidgetManager() {
        EventManager.register(this);
        widgetList.add(new GameInfo());
        widgetList.add(new Target());
        widgetList.add(new PotionHUD());
        widgetList.add(new Inventory());
        widgetList.add(new com.amaya.gui.widget.impl.ArrayList());
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        for (Widget widget : widgetList) {
            if (widget.shouldRender()) {
                widget.updatePos();
                widget.render();
            }
        }
    }
    public <module extends Widget> module get(Class<? extends module> moduleClass) {
        Iterator<Widget> var2 = this.widgetList.iterator();
        Widget module;
        do {
            if (!var2.hasNext()) {
                return null;
            }
            module = var2.next();
        } while (module.getClass() != moduleClass);

        return (module) module;
    }
    @EventTarget
    public void onShader2D(Shader2DEvent event) {
        for (Widget widget : widgetList) {
            if (widget.shouldRender()) {
                widget.onShader(event);
            }
        }
    }
    @EventTarget
    public void onChatGUI(ChatGUIEvent event) {
        Widget draggingWidget = null;
        for (Widget widget : widgetList) {
            if (widget.shouldRender() && widget.dragging) {
                draggingWidget = widget;
                break;
            }
        }

        for (Widget widget : widgetList) {
            if (widget.shouldRender()) {
                widget.onChatGUI(event.getMouseX(), event.getMouseY(), (draggingWidget == null || draggingWidget == widget));
                if (widget.dragging) draggingWidget = widget;
            }
        }
    }
}