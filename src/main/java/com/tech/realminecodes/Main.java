package com.tech.realminecodes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Database database;
    private CodeManager codeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        database = new Database(this);
        database.initialize();

        codeManager = new CodeManager(this, database);

        getCommand("code").setExecutor(new CodeCommand(this, codeManager));

        getCommand("rmcodes").setExecutor(this);
    }

    @Override
    public void onDisable() {
        database.close();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rmcodes")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("realminecodes.admin")) {
                    sender.sendMessage(ColorUtils.translateHexColors(getConfig().getString("messages.no_permission_reload")));
                    return true;
                }

                reloadConfig();
                codeManager.reloadConfig();

                sender.sendMessage(ColorUtils.translateHexColors(getConfig().getString("messages.reload_success")));
                return true;
            }
        }
        return false;
    }
}