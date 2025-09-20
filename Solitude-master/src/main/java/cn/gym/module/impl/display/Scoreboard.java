package cn.gym.module.impl.display;

import cn.gym.Solitude;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.Category;
import cn.gym.module.ModuleWidget;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontRenderer;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.utils.render.ShaderElement;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ModeValue;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * @Author：Guyuemang
 * @Date：2025/6/7 12:26
 */
public class Scoreboard extends ModuleWidget {
    public Scoreboard() {
        super("Scoreboard", Category.Display);
    }
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal","Custom","Solitude"});
    private final BooleanValue leftLayout = new BooleanValue("Left Layout", true);
    private final BooleanValue redNumbers = new BooleanValue("Red Numbers", false);
    @Override
    public void onShader(Shader2DEvent event) {
        int x = (int) renderX;
        int y = (int) renderY + 24;
        switch (modeValue.getValue()){
            case "Custom": {
                ScoreObjective scoreObjective = getScoreObjective();

                if (scoreObjective == null) {
                    return;
                }

                net.minecraft.scoreboard.Scoreboard scoreboard = scoreObjective.getScoreboard();
                Collection<Score> sortedScores = scoreboard.getSortedScores(scoreObjective);
                List<Score> list = Lists.newArrayList(Iterables.filter(sortedScores, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

                if (list.size() > 15) {
                    sortedScores = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
                } else {
                    sortedScores = list;
                }

                int maxWidth = mc.fontRendererObj.getStringWidth(scoreObjective.getName());

                for (Score score : sortedScores) {
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName()) + ":" + (redNumbers.get() ? " " + EnumChatFormatting.RED + score.getScorePoints() : "");

                    maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(playerName));
                }
                if (!redNumbers.get()) {
                    maxWidth += 4;
                }

                int lineHeight = 10;
                int totalHeight = (sortedScores.size()) * lineHeight;

                if (!leftLayout.get()) {
                    x -= maxWidth;
                }

                int xEnd = x + maxWidth;
                int yEnd = y + totalHeight;

                int index = -1;

                int finalMaxWidth = maxWidth;
                int finalX1 = x;
                RoundedUtil.drawRound(x, y - 24, maxWidth, totalHeight + lineHeight + 29, Interface.radius.get().floatValue(), new Color(1, 1, 1, 100));
                RenderUtil.startGlScissor((int) (x - 2), (int) (y - 28), 159, 24);
                RoundedUtil.drawRound(x, y - 24, maxWidth, 29, Interface.radius.get().floatValue(), ColorUtil.applyOpacity(new Color(INTERFACE.color(1)), (float) 0.3f));
                RenderUtil.stopGlScissor();


                ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(finalX1, y, finalMaxWidth, totalHeight + lineHeight, -1));
                int finalX = x;
                ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(finalX, y, finalMaxWidth, totalHeight + lineHeight, ColorUtil.getColor(0, 0, 0, 200)));

                this.setWidth(leftLayout.get() ? maxWidth : -maxWidth);
                this.setHeight(totalHeight);

                for (Score score : sortedScores) {
                    ++index;
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());

                    int y1 = yEnd - index * lineHeight;

                }
                this.setWidth(maxWidth);
                this.renderY = y - 24;
                this.setHeight(totalHeight + lineHeight + 29);
            }
            break;
            case "Normal":
                ScoreObjective scoreObjective = getScoreObjective();

                if (scoreObjective == null) {
                    return;
                }

                net.minecraft.scoreboard.Scoreboard scoreboard = scoreObjective.getScoreboard();
                Collection<Score> sortedScores = scoreboard.getSortedScores(scoreObjective);
                List<Score> list = Lists.newArrayList(Iterables.filter(sortedScores, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

                if (list.size() > 15) {
                    sortedScores = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
                } else {
                    sortedScores = list;
                }

                int maxWidth = mc.fontRendererObj.getStringWidth(scoreObjective.getName());

                for (Score score : sortedScores) {
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName()) + ":" + (redNumbers.get() ? " " + EnumChatFormatting.RED + score.getScorePoints() : "");

                    maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(playerName));
                }
                if (!redNumbers.get()) {
                    maxWidth += 4;
                }

                int lineHeight = 10;
                int totalHeight = (sortedScores.size()) * lineHeight;

                if (!leftLayout.get()) {
                    x -= maxWidth;
                }

                int xEnd = x + maxWidth;
                int yEnd = y + totalHeight;

                int index = -1;

                int finalMaxWidth = maxWidth;
                int finalX1 = x;
                RoundedUtil.drawRound(x, y - 24, maxWidth, totalHeight + lineHeight + 29, Interface.radius.get().floatValue(), new Color(1,1,1,100));
                RenderUtil.startGlScissor((int) (x - 2), (int) (y - 28), 159, 24);
                RoundedUtil.drawRound(x,y - 24, maxWidth, 29, Interface.radius.get().floatValue(), ColorUtil.applyOpacity(new Color(INTERFACE.color(1)), (float) 0.3f));
                RenderUtil.stopGlScissor();


                ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(finalX1, y, finalMaxWidth, totalHeight + lineHeight, -1));
                int finalX = x;
                ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(finalX, y, finalMaxWidth, totalHeight + lineHeight, ColorUtil.getColor(0, 0, 0, 200)));

                this.setWidth(leftLayout.get() ? maxWidth : -maxWidth);
                this.setHeight(totalHeight);

                for (Score score : sortedScores) {
                    ++index;
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());

                    int y1 = yEnd - index * lineHeight;

                }
                this.setWidth(maxWidth);
                this.renderY = y - 24;
                this.setHeight(totalHeight + lineHeight + 29);
                break;
        }
    }

    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;

        switch (modeValue.getValue()){
            case "Custom": {

                FontRenderer font = Mc.get(18);

                ScoreObjective scoreObjective = getScoreObjective();

                if (scoreObjective == null) {
                    return;
                }

                net.minecraft.scoreboard.Scoreboard scoreboard = scoreObjective.getScoreboard();
                Collection<Score> sortedScores = scoreboard.getSortedScores(scoreObjective);
                List<Score> list = Lists.newArrayList(Iterables.filter(sortedScores, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

                if (list.size() > 15) {
                    sortedScores = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
                } else {
                    sortedScores = list;
                }

                int maxWidth = mc.fontRendererObj.getStringWidth(scoreObjective.getName());

                for (Score score : sortedScores) {
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName()) + ":" + (redNumbers.get() ? " " + EnumChatFormatting.RED + score.getScorePoints() : "");

                    maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(playerName));
                }
                if (!redNumbers.get()) {
                    maxWidth += 4;
                }

                int lineHeight = 10;
                int totalHeight = (sortedScores.size()) * lineHeight;

                if (!leftLayout.get()) {
                    x -= maxWidth;
                }

                int xEnd = x + maxWidth;
                int yEnd = y + totalHeight;

                int index = -1;

                int finalMaxWidth = maxWidth;
                int finalX1 = x;
                RenderUtil.drawRect(x, y - 24, maxWidth, totalHeight + lineHeight + 29, new Color(1, 1, 1, 100));
                Mc.get(18).drawString("ScoreBoard", x + maxWidth / 2 - font.getStringWidth("ScoreBoard") / 2, y - 18, -1);


                ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(finalX1, y, finalMaxWidth, totalHeight + lineHeight, -1));
                int finalX = x;
                ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(finalX, y, finalMaxWidth, totalHeight + lineHeight, ColorUtil.getColor(0, 0, 0, 200)));

                this.setWidth(leftLayout.get() ? maxWidth : -maxWidth);
                this.setHeight(totalHeight);

                for (Score score : sortedScores) {
                    ++index;
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());

                    int y1 = yEnd - index * lineHeight;

                    if (index == 0) {
                        font.drawString("guyuem.xyz", x + maxWidth / 2 - font.getStringWidth("guyuem.xyz") / 2, y1,-1);
                    } else {
                        font.drawString(playerName, x + 2, y1, ColorUtil.applyOpacity(553648127, 1F));
                    }

                    // Red numbers
                    if (redNumbers.get()) {
                        String scorePoint = EnumChatFormatting.RED + "" + score.getScorePoints();
                        font.drawString(scorePoint, xEnd - font.getStringWidth(scorePoint), y1, ColorUtil.applyOpacity(553648127, 1F));
                    }

                    if (index == sortedScores.size() - 1) {
                        String replaced = StringUtils.replace(StringUtils.replace(scoreObjective.getDisplayName(), "花雨庭", "Quick Macro"), "§c✿", "§c⌨");
                        font.drawString(replaced, x + maxWidth / 2 - font.getStringWidth(replaced) / 2, y1 - lineHeight, ColorUtil.applyOpacity(553648127, 1F));
                    }
                }
                this.setWidth(maxWidth);
                this.renderY = y - 24;
                this.setHeight(totalHeight + lineHeight + 29);
            }
                break;
            case "Normal":

                FontRenderer font = Semibold.get(18);

                ScoreObjective scoreObjective = getScoreObjective();

                if (scoreObjective == null) {
                    return;
                }

                net.minecraft.scoreboard.Scoreboard scoreboard = scoreObjective.getScoreboard();
                Collection<Score> sortedScores = scoreboard.getSortedScores(scoreObjective);
                List<Score> list = Lists.newArrayList(Iterables.filter(sortedScores, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

                if (list.size() > 15) {
                    sortedScores = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
                } else {
                    sortedScores = list;
                }

                int maxWidth = mc.fontRendererObj.getStringWidth(scoreObjective.getName());

                for (Score score : sortedScores) {
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName()) + ":" + (redNumbers.get() ? " " + EnumChatFormatting.RED + score.getScorePoints() : "");

                    maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(playerName));
                }
                if (!redNumbers.get()) {
                    maxWidth += 4;
                }

                int lineHeight = 10;
                int totalHeight = (sortedScores.size()) * lineHeight;

                if (!leftLayout.get()) {
                    x -= maxWidth;
                }

                int xEnd = x + maxWidth;
                int yEnd = y + totalHeight;

                int index = -1;

                int finalMaxWidth = maxWidth;
                int finalX1 = x;
                RoundedUtil.drawRound(x, y - 24, maxWidth, totalHeight + lineHeight + 29, Interface.radius.get().floatValue(), new Color(1,1,1,100));
                RenderUtil.startGlScissor((int) (x - 2), (int) (y - 28), 159, 24);
                RoundedUtil.drawRound(x,y - 24, maxWidth, 29, Interface.radius.get().floatValue(), ColorUtil.applyOpacity(new Color(INTERFACE.color(1)), (float) 0.3f));
                RenderUtil.stopGlScissor();
                Semibold.get(18).drawString("ScoreBoard",x + maxWidth / 2 - font.getStringWidth("ScoreBoard") / 2,y - 18,-1);


                ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(finalX1, y, finalMaxWidth, totalHeight + lineHeight, -1));
                int finalX = x;
                ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(finalX, y, finalMaxWidth, totalHeight + lineHeight, ColorUtil.getColor(0, 0, 0, 200)));

                this.setWidth(leftLayout.get() ? maxWidth : -maxWidth);
                this.setHeight(totalHeight);

                for (Score score : sortedScores) {
                    ++index;
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());

                    int y1 = yEnd - index * lineHeight;

                    if (index == 0) {
                        font.drawString("guyuem.xyz", x + maxWidth / 2 - font.getStringWidth("guyuem.xyz") / 2, y1, Solitude.Instance.getModuleManager().getModule(Interface.class).color());
                    } else {
                        font.drawString(playerName, x + 2, y1, ColorUtil.applyOpacity(553648127, 1F));
                    }

                    // Red numbers
                    if (redNumbers.get()) {
                        String scorePoint = EnumChatFormatting.RED + "" + score.getScorePoints();
                        font.drawString(scorePoint, xEnd - font.getStringWidth(scorePoint), y1, ColorUtil.applyOpacity(553648127, 1F));
                    }

                    if (index == sortedScores.size() - 1) {
                        String replaced = StringUtils.replace(StringUtils.replace(scoreObjective.getDisplayName(), "花雨庭", "Quick Macro"), "§c✿", "§c⌨");
                        font.drawString(replaced, x + maxWidth / 2 - font.getStringWidth(replaced) / 2, y1 - lineHeight, ColorUtil.applyOpacity(553648127, 1F));
                    }
                }
                this.setWidth(maxWidth);
                this.renderY = y - 24;
                this.setHeight(totalHeight + lineHeight + 29);
                break;
        }
    }

    private static ScoreObjective getScoreObjective() {
        net.minecraft.scoreboard.Scoreboard worldScoreboard = mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = worldScoreboard.getPlayersTeam(mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int colorIndex = scoreplayerteam.getChatFormat().getColorIndex();

            if (colorIndex >= 0) {
                scoreobjective = worldScoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            }
        }

        return scoreobjective != null ? scoreobjective : worldScoreboard.getObjectiveInDisplaySlot(1);
    }

    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
