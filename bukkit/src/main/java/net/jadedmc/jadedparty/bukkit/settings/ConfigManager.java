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
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ConfigManager {
    private final Cache cache;
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(@NotNull final JadedPartyBukkit plugin) {
        this.config = plugin.getConfig();
        this.config.options().copyDefaults(true);
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        plugin.saveConfig();

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

    public boolean isDebugMode() {
        return this.config.getBoolean("debugMode");
    }

    public boolean isStandalone() {
        return this.config.getBoolean("standalone");
    }
}