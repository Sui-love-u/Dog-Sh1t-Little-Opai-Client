package com.amaya.module.impl.world;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.events.impl.player.StrafeEvent;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.player.PlayerUtil;
import com.amaya.utils.player.RayCastUtil;
import com.amaya.utils.player.Rise.MoveUtil;
import com.amaya.utils.player.Rise.MovementFix;
import com.amaya.component.RotationComponent;
import com.amaya.utils.player.Rise.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Scaffold",category = Category.World)
public class Scaffold extends Module {
    public final ModeSetting swingMode = new ModeSetting("Swing Mode", "Client", new String[]{"Client", "Packet", "None"});
    public final BooleanSetting autojump = new BooleanSetting("AutoJump", true);
    public final BooleanSetting tower = new BooleanSetting("Tower", true);
    public final BooleanSetting swordSpoof = new BooleanSetting("Sword Spoof", true);
    public final ModeSetting tellymode = new ModeSetting("Telly Mode","SkyWars",new String[]{"SkyWars","BedWars"});
    private int swordSlot = -1;
    private EnumFacing enumFacing;
    public BlockPos blockPos;
    public static int slot = -1;
    private int startslot = 0;
    private boolean cantelly;
    public ItemStack heldItem = Scaffold.slot == -1 ? null : mc.thePlayer.inventory.mainInventory[Scaffold.slot];
    public int getBlockCount() {
        int slot = PlayerUtil.getBlockSlot();
        return slot == -1 ? 0 : PlayerUtil.getBlockCount();
    }
    @Override
    public void onEnable(){
        blockPos = null;
        enumFacing = null;
        startslot = mc.thePlayer.inventory.currentItem;
        swordSlot = findSwordSlot();
    }

    @Override
    public void onDisable(){
        mc.thePlayer.inventory.currentItem = startslot;
        slot = -1;  // 重置slot
        swordSlot = -1;  // 重置swordSlot
    }
    @EventTarget
    public void onUpdate(PreUpdateEvent e) {
        slot = PlayerUtil.getBlockSlot();
        if (slot != -1) {
            mc.thePlayer.inventory.currentItem = slot;
        }

        // Sword Spoof 逻辑
        if (swordSpoof.get() && swordSlot != -1) {
            mc.thePlayer.inventory.currentItem = swordSlot;
        } else {
            mc.thePlayer.inventory.currentItem = slot;
        }
        search();
        float tellyTicks;
        switch (tellymode.get()) {
            case "SkyWars": {
                tellyTicks = 0.6F;
                break;
            }
            case "BedWars": {
                tellyTicks = 3.3F;
                break;
            }
            default: {
                tellyTicks = 0.5F;
            }
        }
        this.cantelly = mc.thePlayer.offGroundTicks >= tellyTicks;
        if (!cantelly) {
            return;
        }
        if (blockPos != null) {
            float yaw = RotationUtils.getRotationBlock(this.blockPos)[0];
            float pitch = RotationUtils.getRotationBlock(this.blockPos)[1];
            RotationComponent.setRotations(new Vector2f(yaw, pitch), 360, MovementFix.NORMAL);
        }

    }

    @EventTarget
    public void onRender(Render2DEvent event){

    }

    @EventTarget
    public void onPlace(MotionEvent e){
        if (!cantelly || slot == -1) {
            return;
        }

        place();
        e.setCancelled(true);
        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
    }

    public void place(){
        if (isGapple()) return;
        if (RayCastUtil.overBlock(RotationComponent.rotations, enumFacing, blockPos, false)) {
            if (blockPos != null) {
                if (enumFacing == null) {
                    return;
                }
                Vec3 hitvec = getVec3(blockPos, enumFacing);
                if (validateBlockRange(hitvec)) {
                    // 实际放置方块时使用正确的槽位
                    mc.thePlayer.inventory.currentItem = slot;

                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.blockPos, enumFacing, hitvec)) {
                        // 改进的 Swing 处理
                        switch (swingMode.get()) {
                            case "Client":
                                mc.thePlayer.swingItem();
                                break;
                            case "Packet":
                                // 绕过 Grimac 的 Packet Swing
                                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                                break;
                            case "None":
                                // 不执行任何 Swing
                                break;
                        }
                    }

                    // 恢复剑槽位显示
                    if (swordSpoof.get() && swordSlot != -1) {
                        mc.thePlayer.inventory.currentItem = swordSlot;
                    }
                }
            }
        }
    }
    private int findSwordSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemSword) {
                return i;
            }
        }
        return -1;
    }
    private static boolean validateBlockRange(final Vec3 pos) {
        if (pos == null)
            return false;
        final EntityPlayerSP player = mc.thePlayer;
        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + player.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);
        return StrictMath.sqrt(x * x + y * y + z * z) <= 5.0D;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
            z += MathUtils.getRandomInRange(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    public static boolean canBeClick(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock().canCollideCheck(mc.theWorld.getBlockState(pos), false) && mc.theWorld.getWorldBorder().contains(pos);
    }

    @EventTarget
    public void onStrafe(StrafeEvent e){
        if ((autojump.get()) && mc.thePlayer.onGround && MoveUtil.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()){
            mc.thePlayer.jump();
        }
    }

    private void search() {
        EntityPlayerSP player = Scaffold.mc.thePlayer;
        WorldClient world = Scaffold.mc.theWorld;
        double posX = player.posX;
        double posZ = player.posZ;
        double minY = player.getEntityBoundingBox().minY;
        Vec3 vec3 =getPlacePossibility(0.0, 0.0, 0.0,true);
        if (vec3 == null) {
            return;
        }
        BlockPos pos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        if (!Scaffold.mc.theWorld.getBlockState(pos).getBlock().getMaterial().isReplaceable()) {
            return;
        }
        for (EnumFacing facingType : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facingType);
            if (!Scaffold.canBeClick(neighbor)) continue;
            Vec3 dirVec = new Vec3(facingType.getDirectionVec());
            for (double xSearch = 0.5; xSearch <= 0.5; xSearch += 0.01) {
                for (double ySearch = 0.5; ySearch <= 0.5; ySearch += 0.01) {
                    double zSearch = 0.5;
                    while (zSearch <= 0.5) {
                        Vec3 eyesPos = new Vec3(posX, minY + (double)Scaffold.mc.thePlayer.getEyeHeight(), posZ);
                        Vec3 posVec = new Vec3(pos).addVector(xSearch, ySearch, zSearch);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        if (eyesPos.distanceTo(hitVec) > 5.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || world.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null) {
                            zSearch += 0.01;
                            continue;
                        }
                        double diffX = hitVec.xCoord - eyesPos.xCoord;
                        double diffY = hitVec.yCoord - eyesPos.yCoord;
                        double diffZ = hitVec.zCoord - eyesPos.zCoord;
                        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
                        if (facingType != EnumFacing.UP && facingType != EnumFacing.DOWN) {
                            if ((facingType == EnumFacing.NORTH || facingType == EnumFacing.SOUTH ? Math.abs(diffZ) : Math.abs(diffX)) < 0.0) continue;
                        }
                        Vector2f rotation = new Vector2f(MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(MathHelper.atan2(diffZ, diffX)) - 90.0)), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(MathHelper.atan2(diffY, diffXZ)))));
                        Vec3 rotVec = getVectorForRotation(rotation);
                        Vec3 vector = eyesPos.addVector(rotVec.xCoord * 5.0, rotVec.yCoord * 5.0, rotVec.zCoord * 5.0);
                        MovingObjectPosition obj = Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                        if (obj == null) continue;
                        if (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || obj.getBlockPos().getX() != neighbor.getX() || obj.getBlockPos().getZ() != neighbor.getZ() || obj.getBlockPos().getY() != neighbor.getY() || obj.sideHit != facingType.getOpposite()) {
                            zSearch += 0.01;
                            continue;
                        }
                        this.blockPos = neighbor;
                        this.enumFacing = facingType.getOpposite();
                        return;
                    }
                }
            }
        }
    }

    public static Vec3 getVectorForRotation(Vector2f rotation) {
        float yawCos = (float)Math.cos(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float yawSin = (float)Math.sin(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float pitchCos = (float)(-Math.cos(-rotation.y * ((float)Math.PI / 180)));
        float pitchSin = (float)Math.sin(-rotation.y * ((float)Math.PI / 180));
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ, boolean searchUP) {
        ArrayList<Vec3> possibilities = new ArrayList<>();
        int range = (int)(6.0 + (Math.abs(offsetX) + Math.abs(offsetZ)));
        Vec3 playerPos = new Vec3(mc.thePlayer.posX + offsetX, mc.thePlayer.posY - 1.0 + offsetY, mc.thePlayer.posZ + offsetZ);
        if (!(mc.theWorld.getBlockState(new BlockPos(playerPos)).getBlock() instanceof BlockAir)) {
            return playerPos;
        }
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= 0; ++y) {
                for (int z = -range; z <= range; ++z) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(x, y, z)).getBlock();
                    if (block instanceof BlockAir) continue;
                    for (int x2 = -1; x2 <= 1; x2 += 2) {
                        possibilities.add(new Vec3(mc.thePlayer.posX + (double)x + (double)x2, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z));
                    }
                    for (int y2 = -1; y2 <= 1; y2 += 2) {
                        possibilities.add(new Vec3(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y + (double)y2, mc.thePlayer.posZ + (double)z));
                    }
                    for (int z2 = -1; z2 <= 1; z2 += 2) {
                        possibilities.add(new Vec3(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z + (double)z2));
                    }
                }
            }
        }
        possibilities.removeIf(vec3 -> {
            BlockPos blockPos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            if (mc.thePlayer.getPosition().getX() == blockPos.getX() && mc.thePlayer.getPosition().getY() == blockPos.getY() && mc.thePlayer.getPosition().getZ() == blockPos.getZ()) {
                return true;
            }
            BlockPos position = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return mc.thePlayer.getDistance((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5) > 6.0 || !(mc.theWorld.getBlockState(new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord)).getBlock() instanceof BlockAir);
        });
        possibilities.removeIf(e -> {
            boolean hasBlock = false;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos position;
                if (facing == EnumFacing.UP || facing == EnumFacing.DOWN && !searchUP || mc.theWorld.getBlockState((position = new BlockPos(e.xCoord, e.yCoord, e.zCoord)).offset(facing)) == null || mc.theWorld.getBlockState(position.offset(facing)).getBlock() instanceof BlockAir) continue;
                BlockPos facePos = position.offset(facing);
                if (mc.thePlayer.getDistance((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5) > mc.thePlayer.getDistance((double)facePos.getX() + 0.5, (double)facePos.getY() + 0.5, (double)facePos.getZ() + 0.5)) {
                    return true;
                }
                hasBlock = true;
            }
            if (e.yCoord > mc.thePlayer.getEntityBoundingBox().minY && !searchUP) {
                return true;
            }
            return !hasBlock;
        });
        if (possibilities.isEmpty()) {
            return null;
        }
        possibilities.sort(Comparator.comparingDouble(vec3 -> {
            double d0 = mc.thePlayer.posX + offsetX - vec3.xCoord;
            double d1 = mc.thePlayer.posY - 1.0 + offsetY - vec3.yCoord;
            double d2 = mc.thePlayer.posZ + offsetZ - vec3.zCoord;
            return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
        }));
        return possibilities.get(0);
    }
}