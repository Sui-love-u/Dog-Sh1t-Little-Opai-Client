package qwq.arcane.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import qwq.arcane.config.ConfigManager;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.module.ModuleManager;
import qwq.arcane.utils.chats.ChatUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/command/CommandManager.class */
public class CommandManager {
    private final ModuleManager moduleManager;
    private final Mine mc = Mine.getMinecraft();

    public CommandManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public boolean executeCommand(String message) {
        String[] commandArgs;
        if (!message.startsWith(".")) {
            return false;
        }
        String[] args = message.substring(1).split(" ");
        if (args.length == 0) {
            return false;
        }
        String command = args[0].toLowerCase();
        commandArgs = (String[]) Arrays.copyOfRange(args, 1, args.length);
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
                ChatUtils.sendMessage(" Unknown command. Type .help for a list of commands.");
                return true;
        }
    }

    private boolean handleBindCommand(String[] args) throws NumberFormatException {
        if (args.length < 2) {
            ChatUtils.sendMessage(" Usage: .bind <module> <key>");
            return true;
        }
        Module module = this.moduleManager.getModule(args[0]);
        if (module == null) {
            ChatUtils.sendMessage(" Module not found: " + args[0]);
            return true;
        }
        try {
            int key = Keyboard.getKeyIndex(args[1].toUpperCase());
            if (key == 0 && !args[1].equalsIgnoreCase("none")) {
                key = Integer.parseInt(args[1]);
            }
            module.setKey(key);
            ChatUtils.sendMessage(" Bound " + module.getName() + " to " + (key == 0 ? "NONE" : Keyboard.getKeyName(key)));
            return true;
        } catch (NumberFormatException e) {
            ChatUtils.sendMessage(" Invalid key: " + args[1]);
            return true;
        }
    }

    private boolean handleToggleCommand(String[] args) throws SecurityException {
        if (args.length < 1) {
            ChatUtils.sendMessage(" Usage: .toggle <module>");
            return true;
        }
        Module module = this.moduleManager.getModule(args[0]);
        if (module == null) {
            ChatUtils.sendMessage(" Module not found: " + args[0]);
            return true;
        }
        module.toggle();
        ChatUtils.sendMessage(" " + module.getName() + " has been " + (module.getState() ? "enabled" : "disabled"));
        return true;
    }

    private boolean handleConfigCommand(String[] args) {
        if (args.length < 1) {
            ChatUtils.sendMessage(" Usage: .config <save/load/delete/list> <name>");
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "save":
                if (args.length < 2) {
                    ChatUtils.sendMessage(" Usage: .config save <name>");
                    break;
                } else {
                    ConfigManager.saveConfig(args[1], this.moduleManager);
                    ChatUtils.sendMessage(" Config saved as: " + args[1]);
                    break;
                }
            case "load":
                if (args.length < 2) {
                    ChatUtils.sendMessage(" Usage: .config load <name>");
                    break;
                } else {
                    ConfigManager.loadConfig(args[1], this.moduleManager);
                    ChatUtils.sendMessage(" Config loaded: " + args[1]);
                    break;
                }
            case "delete":
                if (args.length < 2) {
                    ChatUtils.sendMessage(" Usage: .config delete <name>");
                    break;
                } else {
                    ConfigManager.deleteConfig(args[1]);
                    ChatUtils.sendMessage(" Config deleted: " + args[1]);
                    break;
                }
            case "list":
                List<String> configs = ConfigManager.getConfigs();
                if (configs.isEmpty()) {
                    ChatUtils.sendMessage(EnumChatFormatting.YELLOW + "[Arcane] No configs found.");
                    break;
                } else {
                    ChatUtils.sendMessage(EnumChatFormatting.GOLD + "[Arcane] Available configs:");
                    for (String config : configs) {
                        ChatUtils.sendMessage(EnumChatFormatting.WHITE + "- " + config);
                    }
                    break;
                }
            default:
                ChatUtils.sendMessage(" Unknown config command. Usage: .config <save/load/delete/list> <name>");
                break;
        }
        return true;
    }

    private boolean handleHelpCommand() {
        ChatUtils.sendMessage(EnumChatFormatting.GOLD + "[Arcane] Available commands:");
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
        ChatUtils.sendMessage(EnumChatFormatting.GOLD + "[Arcane] Current keybinds:");
        this.moduleManager.getAllModules().stream().filter(module -> {
            return module.getKey() != 0;
        }).forEach(module2 -> {
            ChatUtils.sendMessage(EnumChatFormatting.WHITE + module2.getName() + ": " + Keyboard.getKeyName(module2.getKey()));
        });
        return true;
    }
}
