package com.amaya.command;

import com.amaya.config.ConfigManager;
import com.amaya.gui.widget.WidgetManager;
import com.amaya.module.Module;
import com.amaya.module.ModuleManager;
import com.amaya.utils.client.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class CommandManager {
    private final ModuleManager moduleManager;
    private final WidgetManager widgetManager;
    private final Minecraft mc = Minecraft.getMinecraft();
    
    public CommandManager(ModuleManager moduleManager,WidgetManager widgetManager) {
        this.moduleManager = moduleManager;
        this.widgetManager = widgetManager;
    }

    public boolean executeCommand(String message) {
        if (!message.startsWith(".")) return false;
        
        String[] args = message.substring(1).split(" ");
        if (args.length == 0) return false;
        
        String command = args[0].toLowerCase();
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        
        switch (command) {
            case "bind":
                return handleBindCommand(commandArgs);
            case "toggle":
                return handleToggleCommand(commandArgs);
            case "config":
                return handleConfigCommand(commandArgs);
            case "help":
                return handleHelpCommand();
            case "binds":
                return handleBindsCommand();
            default:
                ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Unknown command. Type .help for a list of commands.");
                return true;
        }
    }

    private boolean handleBindCommand(String[] args) {
        if (args.length < 2) {
            ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Usage: .bind <module> <key>");
            return true;
        }

        Module module = moduleManager.getModule(args[0]);
        if (module == null) {
            ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Module not found: " + args[0]);
            return true;
        }

        try {
            int key = Keyboard.getKeyIndex(args[1].toUpperCase());
            if (key == Keyboard.KEY_NONE && !args[1].equalsIgnoreCase("none")) {
                key = Integer.parseInt(args[1]);
            }

            module.setKey(key);
            ChatUtils.sendMessage(EnumChatFormatting.GREEN + "[AmayaClient] Bound " + module.getName() + " to " + (key == Keyboard.KEY_NONE ? "NONE" : Keyboard.getKeyName(key)));
            return true;
        } catch (NumberFormatException e) {
            ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Invalid key: " + args[1]);
            return true;
        }
    }

    private boolean handleToggleCommand(String[] args) {
        if (args.length < 1) {
            ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Usage: .toggle <module>");
            return true;
        }

        Module module = moduleManager.getModule(args[0]);
        if (module == null) {
            ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Module not found: " + args[0]);
            return true;
        }

        module.toggle();
        ChatUtils.sendMessage(EnumChatFormatting.GREEN + "[AmayaClient] " + module.getName() + " has been " + (module.getState() ? "enabled" : "disabled"));
        return true;
    }

    private boolean handleConfigCommand(String[] args) {
        if (args.length < 1) {
            ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Usage: .config <save/load/delete/list> <name>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "save":
                if (args.length < 2) {
                    ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Usage: .config save <name>");
                    return true;
                }
                ConfigManager.saveConfig(args[1], moduleManager,widgetManager);
                ChatUtils.sendMessage(EnumChatFormatting.GREEN + "[AmayaClient] Config saved as: " + args[1]);
                return true;

            case "load":
                if (args.length < 2) {
                    ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Usage: .config load <name>");
                    return true;
                }
                ConfigManager.loadConfig(args[1], moduleManager,widgetManager);
                ChatUtils.sendMessage(EnumChatFormatting.GREEN + "[AmayaClient] Config loaded: " + args[1]);
                return true;

            case "delete":
                if (args.length < 2) {
                    ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Usage: .config delete <name>");
                    return true;
                }
                ConfigManager.deleteConfig(args[1]);
                ChatUtils.sendMessage(EnumChatFormatting.GREEN + "[AmayaClient] Config deleted: " + args[1]);
                return true;

            case "list":
                List<String> configs = ConfigManager.getConfigs();
                if (configs.isEmpty()) {
                    ChatUtils.sendMessage(EnumChatFormatting.YELLOW + "[AmayaClient] No configs found.");
                } else {
                    ChatUtils.sendMessage(EnumChatFormatting.GOLD + "[AmayaClient] Available configs:");
                    for (String config : configs) {
                        ChatUtils.sendMessage(EnumChatFormatting.WHITE + "- " + config);
                    }
                }
                return true;

            default:
                ChatUtils.sendMessage(EnumChatFormatting.RED + "[AmayaClient] Unknown config command. Usage: .config <save/load/delete/list> <name>");
                return true;
        }
    }

    private boolean handleHelpCommand() {
        ChatUtils.sendMessage(EnumChatFormatting.GOLD + "[AmayaClient] Available commands:");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".bind <module> <key> - Bind a module to a key");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".toggle <module> - Toggle a module on/off");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".config save <name> - Save current config");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".config load <name> - Load a config");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".config delete <name> - Delete a config");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".config list - List all configs");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".binds - Show all keybinds");
        ChatUtils.sendMessage(EnumChatFormatting.WHITE + ".help - Show this help message");
        return true;
    }

    private boolean handleBindsCommand() {
        ChatUtils.sendMessage(EnumChatFormatting.GOLD + "[AmayaClient] Current keybinds:");

        moduleManager.getAllModules().stream()
                .filter(module -> module.getKey() != Keyboard.KEY_NONE)
                .forEach(module -> ChatUtils.sendMessage(EnumChatFormatting.WHITE +
                        module.getName() + ": " + Keyboard.getKeyName(module.getKey())));

        return true;
    }
}