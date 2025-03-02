package com.tech.realminecodes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CodeCommand implements CommandExecutor {

    private final Main plugin;
    private final CodeManager codeManager;

    public CodeCommand(Main plugin, CodeManager codeManager) {
        this.plugin = plugin;
        this.codeManager = codeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (!player.hasPermission("realminecodes.use")) {
            player.sendMessage(ColorUtils.translateHexColors(plugin.getConfig().getString("messages.no_permission_use")));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ColorUtils.translateHexColors(plugin.getConfig().getString("messages.usage")));
            return true;
        }

        String code = args[0];
        codeManager.activateCode(player, code);
        return true;
    }
}