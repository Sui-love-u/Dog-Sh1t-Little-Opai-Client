package cn.gym.gui.mainmenu;

import cn.gym.utils.misc.FileUtils;
import net.minecraft.client.Minecraft;

import java.io.File;

public final class VideoComponent {
    public VideoComponent() {
        // 获取视频文件的路径
        File videoFile = new File(Minecraft.getMinecraft().mcDataDir, "background.mp4");

        // 检查视频文件是否存在，如果不存在则解压视频文件
        if (!videoFile.exists()) {
            FileUtils.unpackFile(videoFile, "assets/minecraft/solitude/background.mp4");
        }
    }
}
