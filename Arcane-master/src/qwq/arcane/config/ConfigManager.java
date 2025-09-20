package qwq.arcane.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import qwq.arcane.gui.alt.auth.Account;
import qwq.arcane.module.Module;
import qwq.arcane.module.ModuleManager;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.value.Value;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.MultiBooleanValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/config/ConfigManager.class */
public class ConfigManager {
    private static final File CONFIG_DIR = new File("Arcane/configs");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ArrayList<Account> accounts = new ArrayList<>();

    static {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
    }

    public static Account getAccount(int index) {
        return accounts.get(index);
    }

    public static void addAccount(Account account) {
        accounts.add(account);
    }

    public static void removeAccount(int index) {
        accounts.remove(index);
    }

    public static int getAccountCount() {
        return accounts.size();
    }

    public static void swapAccounts(int i, int j) {
        Collections.swap(accounts, i, j);
    }

    public static void saveConfig(String configName, ModuleManager moduleManager) {
        try {
            JsonObject config = new JsonObject();
            JsonObject modulesObj = new JsonObject();
            for (Module module : moduleManager.getAllModules()) {
                JsonObject moduleObj = new JsonObject();
                moduleObj.addProperty("enabled", Boolean.valueOf(module.getState()));
                moduleObj.addProperty("key", Integer.valueOf(module.getKey()));
                JsonObject settingsObj = new JsonObject();
                for (Value<?> setting : module.getSettings()) {
                    if (setting instanceof BoolValue) {
                        settingsObj.addProperty(setting.getName(), ((BoolValue) setting).getValue());
                    } else if (setting instanceof NumberValue) {
                        settingsObj.addProperty(setting.getName(), ((NumberValue) setting).getValue());
                    } else if (setting instanceof MultiBooleanValue) {
                        settingsObj.addProperty(setting.getName(), ((MultiBooleanValue) setting).isEnabled());
                    } else if (setting instanceof ModeValue) {
                        settingsObj.addProperty(setting.getName(), ((ModeValue) setting).getValue());
                    } else if (setting instanceof ColorValue) {
                        ColorValue colorValue = (ColorValue) setting;
                        JsonObject colorValues = new JsonObject();
                        colorValues.addProperty("hue", Float.valueOf(colorValue.getHue()));
                        colorValues.addProperty("saturation", Float.valueOf(colorValue.getSaturation()));
                        colorValues.addProperty("brightness", Float.valueOf(colorValue.getBrightness()));
                        colorValues.addProperty("alpha", Float.valueOf(colorValue.getAlpha()));
                        colorValues.addProperty("rainbow", Boolean.valueOf(colorValue.isRainbow()));
                        settingsObj.add(colorValue.getName(), colorValues);
                    }
                }
                moduleObj.add("settings", settingsObj);
                modulesObj.add(module.getName(), moduleObj);
            }
            config.add("modules", modulesObj);
            JsonObject widgetsObj = new JsonObject();
            for (ModuleWidget widget : moduleManager.getAllWidgets()) {
                JsonObject widgetObj = new JsonObject();
                widgetObj.addProperty("x", Float.valueOf(widget.getX()));
                widgetObj.addProperty("y", Float.valueOf(widget.getY()));
                widgetObj.addProperty("width", Float.valueOf(widget.getWidth()));
                widgetObj.addProperty("height", Float.valueOf(widget.getHeight()));
                widgetsObj.add(widget.getName(), widgetObj);
            }
            config.add("widgets", widgetsObj);
            JsonArray accountsArray = new JsonArray();
            Iterator<Account> it = accounts.iterator();
            while (it.hasNext()) {
                Account account = it.next();
                JsonObject accountObj = new JsonObject();
                accountObj.addProperty("refreshToken", account.getRefreshToken());
                accountObj.addProperty("accessToken", account.getAccessToken());
                accountObj.addProperty("username", account.getUsername());
                accountObj.addProperty("timestamp", Long.valueOf(account.getTimestamp()));
                accountObj.addProperty("uuid", account.getUUID());
                accountsArray.add(accountObj);
            }
            config.add("accounts", accountsArray);
            Files.write(Paths.get(CONFIG_DIR.getPath(), configName + ".json"), GSON.toJson(config).getBytes(), new OpenOption[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(String configName, ModuleManager moduleManager) {
        JsonArray accountsArray;
        File configFile = new File(CONFIG_DIR, configName + ".json");
        if (configFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(configFile.toPath()));
                JsonObject config = new JsonParser().parse(content).getAsJsonObject();
                if (config.has("modules")) {
                    JsonObject modulesObj = config.getAsJsonObject("modules");
                    for (Map.Entry<String, JsonElement> entry : modulesObj.entrySet()) {
                        Module module = moduleManager.getModule(entry.getKey());
                        if (module != null) {
                            JsonObject moduleObj = entry.getValue().getAsJsonObject();
                            module.setState(moduleObj.get("enabled").getAsBoolean());
                            module.setKey(moduleObj.get("key").getAsInt());
                            if (moduleObj.has("settings")) {
                                JsonObject settingsObj = moduleObj.getAsJsonObject("settings");
                                for (Value<?> setting : module.getSettings()) {
                                    if (settingsObj.has(setting.getName())) {
                                        JsonElement settingValue = settingsObj.get(setting.getName());
                                        if (setting instanceof BoolValue) {
                                            ((BoolValue) setting).setValue(Boolean.valueOf(settingValue.getAsBoolean()));
                                        } else if (setting instanceof NumberValue) {
                                            NumberValue numSetting = (NumberValue) setting;
                                            numSetting.setValue(Double.valueOf(settingValue.getAsDouble()));
                                        } else if (setting instanceof MultiBooleanValue) {
                                            MultiBooleanValue enumSetting = (MultiBooleanValue) setting;
                                            if (!settingValue.getAsString().isEmpty()) {
                                                String[] strings = settingValue.getAsString().split(", ");
                                                enumSetting.getToggled().forEach(option -> {
                                                    option.set(false);
                                                });
                                                for (String string : strings) {
                                                    enumSetting.getValues().stream().filter(settings -> {
                                                        return settings.getName().equalsIgnoreCase(string);
                                                    }).forEach(boolValue -> {
                                                        boolValue.set(true);
                                                    });
                                                }
                                            }
                                        } else if (setting instanceof ModeValue) {
                                            ((ModeValue) setting).setValue(settingValue.getAsString());
                                        } else if (setting instanceof ColorValue) {
                                            ColorValue colorSetting = (ColorValue) setting;
                                            JsonObject colorValues = settingValue.getAsJsonObject();
                                            float hue = colorValues.get("hue").getAsFloat();
                                            float saturation = colorValues.get("saturation").getAsFloat();
                                            float brightness = colorValues.get("brightness").getAsFloat();
                                            float alpha = colorValues.get("alpha").getAsFloat();
                                            boolean rainbow = colorValues.get("rainbow").getAsBoolean();
                                            Color color = Color.getHSBColor(hue, saturation, brightness);
                                            colorSetting.set(ColorUtil.applyOpacity(color, alpha));
                                            colorSetting.setRainbow(rainbow);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
                accounts.clear();
                if (config.has("accounts") && (accountsArray = config.getAsJsonArray("accounts")) != null) {
                    Iterator it = accountsArray.iterator();
                    while (it.hasNext()) {
                        JsonElement jsonElement = (JsonElement) it.next();
                        JsonObject accountObj = jsonElement.getAsJsonObject();
                        accounts.add(new Account((String) Optional.ofNullable(accountObj.get("refreshToken")).map((v0) -> {
                            return v0.getAsString();
                        }).orElse(""), (String) Optional.ofNullable(accountObj.get("accessToken")).map((v0) -> {
                            return v0.getAsString();
                        }).orElse(""), (String) Optional.ofNullable(accountObj.get("username")).map((v0) -> {
                            return v0.getAsString();
                        }).orElse(""), ((Long) Optional.ofNullable(accountObj.get("timestamp")).map((v0) -> {
                            return v0.getAsLong();
                        }).orElse(Long.valueOf(System.currentTimeMillis()))).longValue(), (String) Optional.ofNullable(accountObj.get("uuid")).map((v0) -> {
                            return v0.getAsString();
                        }).orElse("")));
                    }
                }
            } catch (IOException | JsonParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getConfigs() {
        File[] files = CONFIG_DIR.listFiles((dir, name) -> {
            return name.endsWith(".json");
        });
        if (files == null) {
            return new ArrayList();
        }
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
