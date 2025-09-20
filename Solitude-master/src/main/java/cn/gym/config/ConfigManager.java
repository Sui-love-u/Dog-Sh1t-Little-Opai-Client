package cn.gym.config;

import cn.gym.module.Module;
import cn.gym.module.ModuleManager;
import cn.gym.module.ModuleWidget;
import cn.gym.utils.color.ColorUtil;
import cn.gym.value.Value;
import cn.gym.value.impl.*;
import com.google.gson.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class ConfigManager {
    private static final File CONFIG_DIR = new File("Solitude/configs");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
    }

    public static void saveConfig(String configName, ModuleManager moduleManager) {
        try {
            JsonObject config = new JsonObject();
            JsonObject modulesObj = new JsonObject();
            for (Module module : moduleManager.getAllModules()) {
                JsonObject moduleObj = new JsonObject();
                moduleObj.addProperty("enabled", module.getState());
                moduleObj.addProperty("key", module.getKey());

                JsonObject settingsObj = new JsonObject();
                for (Value<?> setting : module.getSettings()) {
                    if (setting instanceof BooleanValue) {
                        settingsObj.addProperty(setting.getName(), ((BooleanValue) setting).getValue());
                    } else if (setting instanceof NumberValue) {
                        settingsObj.addProperty(setting.getName(), ((NumberValue) setting).getValue());
                    } else if (setting instanceof MultiBooleanValue) {
                        settingsObj.addProperty(setting.getName(), ((MultiBooleanValue) setting).isEnabled());
                    } else if (setting instanceof ModeValue) {
                        settingsObj.addProperty(setting.getName(), ((ModeValue) setting).getValue());
                    } else if (setting instanceof ColorValue colorValue) {
                        JsonObject colorValues = new JsonObject();
                        colorValues.addProperty("RGB", Color.HSBtoRGB(colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()));
                        colorValues.addProperty("Alpha", colorValue.getAlpha());
                        settingsObj.add(colorValue.getName(), colorValues);
                    }
                }

                moduleObj.add("settings", settingsObj);
                modulesObj.add(module.getName(), moduleObj);
            }
            config.add("modules", modulesObj);

            // 保存Widget配置
            JsonObject widgetsObj = new JsonObject();
            for (ModuleWidget widget : moduleManager.getAllWidgets()) {
                JsonObject widgetObj = new JsonObject();
                widgetObj.addProperty("x", widget.getX());
                widgetObj.addProperty("y", widget.getY());
                widgetObj.addProperty("width", widget.getWidth());
                widgetObj.addProperty("height", widget.getHeight());
                widgetsObj.add(widget.getName(), widgetObj);
            }
            config.add("widgets", widgetsObj);

            Files.write(Paths.get(CONFIG_DIR.getPath(), configName + ".json"), GSON.toJson(config).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(String configName, ModuleManager moduleManager) {
        File configFile = new File(CONFIG_DIR, configName + ".json");
        if (!configFile.exists()) return;

        try {
            String content = new String(Files.readAllBytes(configFile.toPath()));
            JsonObject config = new JsonParser().parse(content).getAsJsonObject();

            // 加载模块配置
            if (config.has("modules")) {
                JsonObject modulesObj = config.getAsJsonObject("modules");
                for (Map.Entry<String, JsonElement> entry : modulesObj.entrySet()) {
                    Module module = moduleManager.getModule(entry.getKey());
                    if (module == null) continue;

                    JsonObject moduleObj = entry.getValue().getAsJsonObject();
                    module.setState(moduleObj.get("enabled").getAsBoolean());
                    module.setKey(moduleObj.get("key").getAsInt());

                    if (moduleObj.has("settings")) {
                        JsonObject settingsObj = moduleObj.getAsJsonObject("settings");
                        for (Value<?> setting : module.getSettings()) {
                            if (!settingsObj.has(setting.getName())) continue;

                            JsonElement settingValue = settingsObj.get(setting.getName());
                            if (setting instanceof BooleanValue) {
                                ((BooleanValue) setting).setValue(settingValue.getAsBoolean());
                            }
                            else if (setting instanceof NumberValue) {
                                NumberValue numSetting = (NumberValue) setting;
                                numSetting.setValue(settingValue.getAsDouble());
                            }
                            else if (setting instanceof MultiBooleanValue enumSetting) {
                                if (!settingValue.getAsString().isEmpty()) {
                                    String[] strings = settingValue.getAsString().split(", ");
                                    enumSetting.getToggled().forEach(option -> option.set(false));
                                    for (String string : strings) {
                                        enumSetting.getValues().stream().filter(settings -> settings.getName().equalsIgnoreCase(string)).forEach(boolValue -> boolValue.set(true));
                                    }
                                }
                            }
                            else if (setting instanceof ModeValue) {
                                ((ModeValue) setting).setValue(settingValue.getAsString());
                            }
                            else if (setting instanceof ColorValue colorSetting) {
                                JsonObject colorValues = settingValue.getAsJsonObject();
                                colorSetting.setValue(ColorUtil.applyOpacity(new Color(colorValues.get("RGB").getAsInt()), colorValues.get("Alpha").getAsFloat()));
                            }
                        }
                    }
                }
            }

            // 加载Widget配置
            if (config.has("widgets")) {
                JsonObject widgetsObj = config.getAsJsonObject("widgets");
                for (ModuleWidget widget : moduleManager.getAllWidgets()) {
                    if (widgetsObj.has(widget.getName())) {
                        JsonObject widgetObj = widgetsObj.getAsJsonObject(widget.getName());
                        widget.setX(widgetObj.get("x").getAsFloat());
                        widget.setY(widgetObj.get("y").getAsFloat());
                        widget.setWidth(widgetObj.get("width").getAsFloat());
                        widget.setHeight(widgetObj.get("height").getAsFloat());
                    }
                }
            }
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getConfigs() {
        File[] files = CONFIG_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return new ArrayList<>();

        List<String> configs = new ArrayList<>();
        for (File file : files) {
            configs.add(file.getName().replace(".json", ""));
        }
        return configs;
    }

    public static void deleteConfig(String configName) {
        File configFile = new File(CONFIG_DIR, configName + ".json");
        if (configFile.exists()) {
            configFile.delete();
        }
    }
}