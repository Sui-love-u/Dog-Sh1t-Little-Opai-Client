package com.amaya.config;

import com.amaya.gui.widget.Widget;
import com.amaya.gui.widget.WidgetManager;
import com.amaya.module.Module;
import com.amaya.module.ModuleManager;
import com.amaya.module.setting.Setting;
import com.amaya.module.setting.impl.*;
import com.amaya.utils.render.ColorUtil;
import com.google.gson.*;
import java.awt.Color;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class ConfigManager {
    private static final File CONFIG_DIR = new File("AmayaClient/configs");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
    }

    public static void saveConfig(String configName, ModuleManager moduleManager, WidgetManager widgetManager) {
        try {
            JsonObject config = new JsonObject();

            // 保存模块配置
            JsonObject modulesObj = new JsonObject();
            for (Module module : moduleManager.getAllModules()) {
                JsonObject moduleObj = new JsonObject();
                moduleObj.addProperty("enabled", module.getState());
                moduleObj.addProperty("key", module.getKey());

                JsonObject settingsObj = new JsonObject();
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof BooleanSetting) {
                        settingsObj.addProperty(setting.getName(), ((BooleanSetting) setting).getValue());
                    } else if (setting instanceof NumberSetting) {
                        settingsObj.addProperty(setting.getName(), ((NumberSetting) setting).getValue());
                    } else if (setting instanceof EnumSetting) {
                        settingsObj.addProperty(setting.getName(), ((EnumSetting) setting).isEnabled());
                    } else if (setting instanceof ModeSetting) {
                        settingsObj.addProperty(setting.getName(), ((ModeSetting) setting).getValue());
                    } else if (setting instanceof StringSetting) {
                        settingsObj.addProperty(setting.getName(), ((StringSetting) setting).getValue());
                    } else if (setting instanceof ColorSetting colorValue) {
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
            for (Widget widget : widgetManager.widgetList) {
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

    public static void loadConfig(String configName, ModuleManager moduleManager, WidgetManager widgetManager) {
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
                        for (Setting<?> setting : module.getSettings()) {
                            if (!settingsObj.has(setting.getName())) continue;

                            JsonElement settingValue = settingsObj.get(setting.getName());
                            if (setting instanceof BooleanSetting) {
                                ((BooleanSetting) setting).setValue(settingValue.getAsBoolean());
                            }
                            else if (setting instanceof NumberSetting) {
                                NumberSetting numSetting = (NumberSetting) setting;
                                numSetting.setValue(settingValue.getAsDouble());
                            }
                            else if (setting instanceof EnumSetting enumSetting) {
                                if (!settingValue.getAsString().isEmpty()) {
                                    String[] strings = settingValue.getAsString().split(", ");
                                    enumSetting.getToggled().forEach(option -> option.set(false));
                                    for (String string : strings) {
                                        enumSetting.getValues().stream().filter(settings -> settings.getName().equalsIgnoreCase(string)).forEach(boolValue -> boolValue.set(true));
                                    }
                                }
                            }
                            else if (setting instanceof ModeSetting) {
                                ((ModeSetting) setting).setValue(settingValue.getAsString());
                            }
                            else if (setting instanceof StringSetting) {
                                ((StringSetting) setting).setValue(settingValue.getAsString());
                            }
                            else if (setting instanceof ColorSetting colorSetting) {
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
                for (Widget widget : widgetManager.widgetList) {
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