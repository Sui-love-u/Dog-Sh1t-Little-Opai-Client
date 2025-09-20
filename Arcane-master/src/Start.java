import java.io.File;
import java.util.Arrays;
import net.minecraft.world.biome.BiomeGenSend;

/* loaded from: Arcane 8.10.jar:Start.class */
public class Start {
    public static void main(String[] args) {
        // 如果崩端 需要把test_natives文件夹放进run里面
        String path = new File("test_natives/" + (System.getProperty("os.name").startsWith("Windows") ? "windows" : "linux")).getAbsolutePath();
        System.out.println(path);
        System.setProperty("org.lwjgl.librarypath", path);

        BiomeGenSend.main((String[]) concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] tArr, T[] tArr2) {
        T[] tArr3 = (T[]) Arrays.copyOf(tArr, tArr.length + tArr2.length);
        System.arraycopy(tArr2, 0, tArr3, tArr.length, tArr2.length);
        return tArr3;
    }
}
