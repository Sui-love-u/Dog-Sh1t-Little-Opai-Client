package net.minecraft.world.biome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;
import qwq.arcane.module.Mine;

/* loaded from: Arcane 8.10.jar:net/minecraft/world/biome/BiomeGenSend.class */
public class BiomeGenSend {
    public static void main(String[] p_main_0_) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecWithRequiredArg = optionparser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecOfType = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecOfType2 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecWithRequiredArg2 = optionparser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecOfType3 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecWithRequiredArg3 = optionparser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecWithRequiredArg4 = optionparser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo3 = optionparser.accepts("username").withRequiredArg().defaultsTo("Arcane1337", new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecWithRequiredArg5 = optionparser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecRequired = optionparser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecRequired2 = optionparser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo4 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo5 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo6 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo7 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecWithRequiredArg6 = optionparser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec argumentAcceptingOptionSpecDefaultsTo8 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
        NonOptionArgumentSpec nonOptionArgumentSpecNonOptions = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List<String> list = optionset.valuesOf(nonOptionArgumentSpecNonOptions);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        String s = (String) optionset.valueOf(argumentAcceptingOptionSpecWithRequiredArg2);
        Proxy proxy = Proxy.NO_PROXY;
        if (s != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, ((Integer) optionset.valueOf(argumentAcceptingOptionSpecOfType3)).intValue()));
            } catch (Exception e) {
            }
        }
        final String s1 = (String) optionset.valueOf(argumentAcceptingOptionSpecWithRequiredArg3);
        final String s2 = (String) optionset.valueOf(argumentAcceptingOptionSpecWithRequiredArg4);
        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator() { // from class: net.minecraft.world.biome.BiomeGenSend.1
                @Override // java.net.Authenticator
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }
        int i = ((Integer) optionset.valueOf(argumentAcceptingOptionSpecDefaultsTo4)).intValue();
        int j = ((Integer) optionset.valueOf(argumentAcceptingOptionSpecDefaultsTo5)).intValue();
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = (String) optionset.valueOf(argumentAcceptingOptionSpecRequired2);
        Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        PropertyMap propertymap = (PropertyMap) gson.fromJson((String) optionset.valueOf(argumentAcceptingOptionSpecDefaultsTo6), PropertyMap.class);
        PropertyMap propertymap1 = (PropertyMap) gson.fromJson((String) optionset.valueOf(argumentAcceptingOptionSpecDefaultsTo7), PropertyMap.class);
        File file1 = (File) optionset.valueOf(argumentAcceptingOptionSpecDefaultsTo2);
        File file2 = optionset.has(argumentAcceptingOptionSpecOfType) ? (File) optionset.valueOf(argumentAcceptingOptionSpecOfType) : new File(file1, "assets/");
        File file3 = optionset.has(argumentAcceptingOptionSpecOfType2) ? (File) optionset.valueOf(argumentAcceptingOptionSpecOfType2) : new File(file1, "resourcepacks/");
        String s4 = optionset.has(argumentAcceptingOptionSpecWithRequiredArg5) ? (String) argumentAcceptingOptionSpecWithRequiredArg5.value(optionset) : (String) argumentAcceptingOptionSpecDefaultsTo3.value(optionset);
        String s5 = optionset.has(argumentAcceptingOptionSpecWithRequiredArg6) ? (String) argumentAcceptingOptionSpecWithRequiredArg6.value(optionset) : null;
        String s6 = (String) optionset.valueOf(argumentAcceptingOptionSpecWithRequiredArg);
        Integer integer = (Integer) optionset.valueOf(argumentAcceptingOptionSpecDefaultsTo);
        Session session = new Session((String) argumentAcceptingOptionSpecDefaultsTo3.value(optionset), s4, (String) argumentAcceptingOptionSpecRequired.value(optionset), (String) argumentAcceptingOptionSpecDefaultsTo8.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer.intValue()));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") { // from class: net.minecraft.world.biome.BiomeGenSend.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Mine.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        new Mine(gameconfiguration).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty()) ? false : true;
    }
}
