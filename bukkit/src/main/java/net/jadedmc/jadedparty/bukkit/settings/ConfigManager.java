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

import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.cache.Cache;
import net.jadedmc.jadedparty.bukkit.cache.CacheType;
import net.jadedmc.jadedparty.bukkit.cache.types.MemoryCache;
import net.jadedmc.jadedparty.bukkit.cache.types.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ConfigManager {
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

        // Replace newline characters from YAML with MiniMessage newline.
        message = message.replace("\\n", "<newline>");

        // TODO: PlaceholderAPI placeholders.

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