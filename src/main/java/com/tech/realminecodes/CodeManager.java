package com.tech.realminecodes;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class CodeManager {

    private final Main plugin;
    private final Database database;
    private FileConfiguration config;

    public CodeManager(Main plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean activateCode(Player player, String code) {
        if (!config.contains("codes." + code)) {
            player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.code_not_found")));
            return false;
        }

        String type = config.getString("codes." + code + ".type");
        boolean ipLimit = config.getBoolean("codes." + code + ".iplimit", false);
        int mediaTimeout = config.getInt("codes." + code + ".mediatimeout", 86400);
        List<String> rewards = config.getStringList("codes." + code + ".reward");

        if (type.equals("oot")) {
            if (database.hasActivatedByUsername(player.getName(), code)) {
                player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.code_already_used")));
                return false;
            }

            if (ipLimit) {
                String ipAddress = player.getAddress().getAddress().getHostAddress();
                if (database.hasActivatedByIP(ipAddress, code)) {
                    player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.ip_already_used")));
                    return false;
                }
            }
        }

        if (type.equals("media")) {
            if (database.hasActivatedWithinTimeout(player.getName(), code, mediaTimeout)) {
                player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.code_already_used")));
                return false;
            }

            if (ipLimit) {
                String ipAddress = player.getAddress().getAddress().getHostAddress();
                if (database.hasActivatedByIPWithinTimeout(ipAddress, code, mediaTimeout)) {
                    player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.ip_already_used")));
                    return false;
                }
            }
        }

        if (type.equals("bonus")) {
            int maxActivations = config.getInt("codes." + code + ".max_activations", 1);
            int activations = database.getCodeActivations(code);

            if (activations >= maxActivations) {
                player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.code_limit_reached")));
                return false;
            }
        }

        for (String reward : rewards) {
            String formattedReward = reward.replace("%player%", player.getName());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), formattedReward);
        }

        String ipAddress = player.getAddress().getAddress().getHostAddress();
        database.logActivation(player.getName(), code, type, ipAddress);
        player.sendMessage(ColorUtils.translateHexColors(config.getString("messages.code_success")));
        return true;
    }
}