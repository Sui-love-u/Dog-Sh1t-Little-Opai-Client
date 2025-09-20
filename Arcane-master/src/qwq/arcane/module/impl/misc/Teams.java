package qwq.arcane.module.impl.misc;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/misc/Teams.class */
public class Teams extends Module {
    private static final BoolValue armorValue = new BoolValue("ArmorColor", true);
    private static final BoolValue colorValue = new BoolValue("Color", true);
    private static final BoolValue scoreboardValue = new BoolValue("ScoreboardTeam", true);

    public Teams() {
        super("Teams", Category.Misc);
    }

    @EventTarget
    public void onSuffix(UpdateEvent event) {
        setsuffix(armorValue.getName().toString());
    }

    public static boolean isSameTeam(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (((Teams) Objects.requireNonNull((Teams) Client.Instance.getModuleManager().getModule(Teams.class))).getState()) {
                return (armorValue.getValue().booleanValue() && PlayerUtil.armorTeam(entityPlayer)) || (colorValue.getValue().booleanValue() && PlayerUtil.colorTeam(entityPlayer)) || (scoreboardValue.getValue().booleanValue() && PlayerUtil.scoreTeam(entityPlayer));
            }
            return false;
        }
        return false;
    }
}
