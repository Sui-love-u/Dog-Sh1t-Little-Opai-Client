package net.minecraft.client;

import com.amaya.Amaya;
import com.amaya.module.impl.misc.Protocol;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

public class ClientBrandRetriever
{
    public static String getClientModName() {
        Protocol fakeForge = Amaya.Instance.moduleManager.getModule(Protocol.class);
        return fakeForge.getState() ? ClientBrandRetriever.getModName() : "vanilla";
    }

    private static String getModName() {
        List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        modNames.add("forge");
        return Joiner.on(',').join(modNames);
    }
}
