package net.optifine.util;

import java.util.HashMap;
import java.util.Map;

import qwq.arcane.module.Mine;

public class FrameEvent
{
    private static Map<String, Integer> mapEventFrames = new HashMap();

    public static boolean isActive(String name, int frameInterval)
    {
        synchronized (mapEventFrames)
        {
            int i = Mine.getMinecraft().entityRenderer.frameCount;
            Integer integer = (Integer)mapEventFrames.get(name);

            if (integer == null)
            {
                integer = new Integer(i);
                mapEventFrames.put(name, integer);
            }

            int j = integer.intValue();

            if (i > j && i < j + frameInterval)
            {
                return false;
            }
            else
            {
                mapEventFrames.put(name, new Integer(i));
                return true;
            }
        }
    }
}
