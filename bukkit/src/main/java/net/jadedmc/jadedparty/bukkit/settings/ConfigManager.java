/*
 * This file is part of JadedParty, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.jadedparty.bukkit.settings;

import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.cache.Cache;
import net.jadedmc.jadedparty.bukkit.cache.CacheType;
import net.jadedmc.jadedparty.bukkit.cache.types.MemoryCache;
import net.jadedmc.jadedparty.bukkit.cache.types.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Manages everything configurable in the plugin.
 */
public final class ConfigManager {
    private final JadedPartyBukkit plugin;
    private final Cache cache;
    private FileConfiguration config;
    private final File configFile;
    private FileConfiguration messages;
    private final File messagesFile;

    /**
     * Sets up and loads the plugin configuration.
     * @param plugin Instance of the plugin.
     */
    public ConfigManager(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;

        // Loads config.yml
        this.config = plugin.getConfig();
        this.config.options().copyDefaults(true);
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        plugin.saveConfig();

        // Loads messages.yml
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        if(!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        // Get and load the proper cache system.
        final CacheType cacheType = CacheType.valueOf(this.config.getString("Cache.type").toUpperCase());
        switch (cacheType) {
            case REDIS -> this.cache = new RedisCache(plugin);
            default -> this.cache = new MemoryCache(plugin);
        }
    }

    /**
     * Gets the configured party cache.
     * @return Configured Cache.
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Get the main configuration file.
     * @return Main configuration file.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Gets a configurable message from the config.
     * @param configMessage Targeted Configurable Message.
     * @return Configured String of the message.
     */
    public String getMessage(final ConfigMessage configMessage) {
        // Loads the default config message.
        String message = configMessage.getDefaultMessage();

        // If the message is configured, use that one instead.
        if(messages.isSet(configMessage.getKey())) {
            message = messages.getString(configMessage.getKey());
        }
        else if(isDebugMode()) {
            plugin.getLogger().info(configMessage.getKey() + " called while missing from messages.yml.");
        }

        // Replace newline characters from YAML with MiniMessage newline.
        message = message.replace("\\n", "<newline>");

        return message;
    }

    /**
     * Gets a configurable message from the config with Placeholder support.
     * @param player Player to process placeholders for.
     * @param configMessage Targeted Configurable message.
     * @return Configured String of the message, with placeholders.
     */
    public String getMessage(@NotNull final Player player, final ConfigMessage configMessage) {
        String message = getMessage(configMessage);

        // Process placeholders if PlaceholderAPI is installed.
        if(plugin.getHookManager().usePlaceholderAPI()) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        else {
            // Non-PlaceholderAPI placeholders.
            message = message.replace("%player_name%", player.getName());
        }

        return message;
    }

    /**
     * Check if the plugin is in Debug Mode.
     * Debug mode logs various information to the console to help diagnose issues.
     * @return true if in debug mode, false otherwise.
     */
    public boolean isDebugMode() {
        return this.config.getBoolean("debugMode");
    }

    /**
     * Check if the plugin is in standalone mode.
     * Standalone mode does not try to sync data between multiple servers.
     * @return true if in standalone mode, false otherwise.
     */
    public boolean isStandalone() {
        return this.config.getBoolean("standalone");
    }
}