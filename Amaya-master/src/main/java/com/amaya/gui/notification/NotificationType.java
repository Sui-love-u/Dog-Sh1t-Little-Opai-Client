package com.amaya.gui.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(0, 255, 81)),
    DISABLE(new Color(255, 0, 0)),
    INFO(new Color(255, 240, 0)),
    WARNING(Color.YELLOW);
    private final Color color;
}