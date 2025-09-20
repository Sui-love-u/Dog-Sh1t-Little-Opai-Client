package qwq.arcane.gui.clickgui.arcane;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.ESP;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.GLUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/ESPComponent.class */
public class ESPComponent extends Component {
    private int dragX;
    private int dragY;
    private int posX = 90;
    private int posY = 50;
    private boolean dragging = false;

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getDragX() {
        return this.dragX;
    }

    public int getDragY() {
        return this.dragY;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        String str;
        if (this.dragging) {
            this.posX = mouseX + this.dragX;
            this.posY = mouseY + this.dragY;
        }
        RoundedUtil.drawRound(this.posX - 10, this.posY, 120.0f, 200.0f, 7.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().backgroundColor, 0.6f));
        GlStateManager.pushMatrix();
        GuiInventory.drawEntityOnScreen(this.posX + 50, this.posY + 180, 80, 0.0f, 0.0f, mc.thePlayer);
        GlStateManager.popMatrix();
        if (ESP.box.get().booleanValue()) {
            float x = this.posX + 5;
            float y = this.posY + 20;
            float x2 = x + 90.0f;
            float y2 = y + 170.0f;
            GL11.glDisable(3553);
            GLUtil.startBlend();
            GL11.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) -106);
            GL11.glBegin(7);
            GL11.glVertex2f(x, y);
            GL11.glVertex2f(x, y2);
            GL11.glVertex2f(x + 1.5f, y2);
            GL11.glVertex2f(x + 1.5f, y);
            GL11.glVertex2f(x2 - 1.5f, y);
            GL11.glVertex2f(x2 - 1.5f, y2);
            GL11.glVertex2f(x2, y2);
            GL11.glVertex2f(x2, y);
            GL11.glVertex2f(x + 1.5f, y);
            GL11.glVertex2f(x + 1.5f, y + 1.5f);
            GL11.glVertex2f(x2 - 1.5f, y + 1.5f);
            GL11.glVertex2f(x2 - 1.5f, y);
            GL11.glVertex2f(x + 1.5f, y2 - 1.5f);
            GL11.glVertex2f(x + 1.5f, y2);
            GL11.glVertex2f(x2 - 1.5f, y2);
            GL11.glVertex2f(x2 - 1.5f, y2 - 1.5f);
            if (ESP.boxSyncColor.get().booleanValue()) {
                ESP.color(InterFace.color(7).getRGB());
            } else {
                ESP.color(ESP.boxColor.get().getRGB());
            }
            GL11.glVertex2f(x + 0.5f, y + 0.5f);
            GL11.glVertex2f(x + 0.5f, y2 - 0.5f);
            GL11.glVertex2f(x + 1.0f, y2 - 0.5f);
            GL11.glVertex2f(x + 1.0f, y + 0.5f);
            GL11.glVertex2f(x2 - 1.0f, y + 0.5f);
            GL11.glVertex2f(x2 - 1.0f, y2 - 0.5f);
            GL11.glVertex2f(x2 - 0.5f, y2 - 0.5f);
            GL11.glVertex2f(x2 - 0.5f, y + 0.5f);
            GL11.glVertex2f(x + 0.5f, y + 0.5f);
            GL11.glVertex2f(x + 0.5f, y + 1.0f);
            GL11.glVertex2f(x2 - 0.5f, y + 1.0f);
            GL11.glVertex2f(x2 - 0.5f, y + 0.5f);
            GL11.glVertex2f(x + 0.5f, y2 - 1.0f);
            GL11.glVertex2f(x + 0.5f, y2 - 0.5f);
            GL11.glVertex2f(x2 - 0.5f, y2 - 0.5f);
            GL11.glVertex2f(x2 - 0.5f, y2 - 1.0f);
            ESP.resetColor();
            GL11.glEnd();
            GL11.glEnable(3553);
            GLUtil.endBlend();
        }
        int x3 = this.posX + 4;
        int y3 = this.posY + 20;
        float y22 = y3 + 170;
        if (ESP.healthBar.get().booleanValue()) {
            GL11.glDisable(3553);
            GLUtil.startBlend();
            float healthBarLeft = x3 - 2.5f;
            float healthBarRight = x3 - 0.5f;
            float health = mc.thePlayer.getHealth();
            float maxHealth = mc.thePlayer.getMaxHealth();
            float healthPercentage = health / maxHealth;
            GL11.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) -106);
            GL11.glBegin(7);
            GL11.glVertex2f(healthBarLeft, y3);
            GL11.glVertex2f(healthBarLeft, y22);
            GL11.glVertex2f(healthBarRight, y22);
            GL11.glVertex2f(healthBarRight, y3);
            float healthBarLeft2 = healthBarLeft + 0.5f;
            float healthBarRight2 = healthBarRight - 0.5f;
            float heightDif = y3 - y22;
            float healthBarHeight = heightDif * healthPercentage;
            float topOfHealthBar = y22 + 0.5f + healthBarHeight;
            if (ESP.healthBarSyncColor.get().booleanValue()) {
                int syncedcolor = InterFace.color(1).getRGB();
                ESP.color(syncedcolor);
            } else {
                int color = ColorUtil.getColorFromPercentage(healthPercentage);
                ESP.color(color);
            }
            GL11.glVertex2f(healthBarLeft2, topOfHealthBar);
            GL11.glVertex2f(healthBarLeft2, y22 - 0.5f);
            GL11.glVertex2f(healthBarRight2, y22 - 0.5f);
            GL11.glVertex2f(healthBarRight2, topOfHealthBar);
            float absorption = mc.thePlayer.getAbsorptionAmount();
            float absorptionPercentage = Math.min(1.0f, absorption / 20.0f);
            int absorptionColor = ESP.absorptionColor.get().getRGB();
            float absorptionHeight = heightDif * absorptionPercentage;
            float topOfAbsorptionBar = y22 + 0.5f + absorptionHeight;
            if (ESP.healthBarSyncColor.get().booleanValue()) {
                ESP.color(InterFace.color(7).getRGB());
            } else {
                ESP.color(absorptionColor);
            }
            GL11.glVertex2f(healthBarLeft2, topOfAbsorptionBar);
            GL11.glVertex2f(healthBarLeft2, y22 - 0.5f);
            GL11.glVertex2f(healthBarRight2, y22 - 0.5f);
            GL11.glVertex2f(healthBarRight2, topOfAbsorptionBar);
            ESP.resetColor();
            GL11.glEnd();
            GL11.glEnable(3553);
            GLUtil.endBlend();
        }
        int x4 = this.posX;
        int y4 = this.posY;
        if (ESP.fontTags.get().booleanValue()) {
            if (ESP.fonttagsHealth.get().booleanValue()) {
                EnumChatFormatting enumChatFormatting = EnumChatFormatting.RED;
                EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.BOLD;
                double dRoundToHalf = MathUtils.roundToHalf(mc.thePlayer.getHealth());
                EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.RESET;
                str = " |" + enumChatFormatting + enumChatFormatting2 + " " + dRoundToHalf + "❤" + enumChatFormatting;
            } else {
                str = "";
            }
            String healthString = str;
            String name = mc.thePlayer.getDisplayName().getFormattedText() + healthString;
            float halfWidth = mc.fontRendererObj.getStringWidth(name) * 0.5f;
            float middle = x4 + halfWidth;
            float textHeight = mc.fontRendererObj.FONT_HEIGHT * 0.5f;
            float renderY = y4 + 12;
            float left = (middle - halfWidth) - 1.0f;
            float right = middle + halfWidth + 1.0f;
            if (ESP.fonttagsBackground.get().booleanValue()) {
                Gui.drawRect(left, renderY - 6.0f, right, renderY + textHeight + 1.0f, new Color(0, 0, 0, 50).getRGB());
            }
            mc.fontRendererObj.drawStringWithShadow(name, middle - halfWidth, renderY - 4.0f, -1);
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(this.posX, this.posY, 100.0f, 200.0f, mouseX, mouseY) && mouseButton == 0) {
            this.dragging = true;
            this.dragX = this.posX - mouseX;
            this.dragY = this.posY - mouseY;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }
}
