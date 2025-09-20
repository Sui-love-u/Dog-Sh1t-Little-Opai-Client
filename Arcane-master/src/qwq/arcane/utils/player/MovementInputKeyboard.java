package qwq.arcane.utils.player;

import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/MovementInputKeyboard.class */
public class MovementInputKeyboard extends MovementInput implements Instance {
    @Override // net.minecraft.util.MovementInput
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0f;
        this.moveForward = 0.0f;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            this.moveForward += 1.0f;
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            this.moveForward -= 1.0f;
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
            this.moveStrafe += 1.0f;
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
            this.moveStrafe -= 1.0f;
        }
        this.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        this.sneak = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        if (this.sneak) {
            this.moveStrafe = (float) (this.moveStrafe * 0.3d);
            this.moveForward = (float) (this.moveForward * 0.3d);
        }
    }
}
