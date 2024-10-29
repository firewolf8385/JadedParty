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
package net.jadedmc.jadedparty.bukkit.cache.types;

import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.cache.Cache;
import net.jadedmc.jadedparty.bukkit.cache.MessageProcessor;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches all party data in the server's RAM.
 * Only useful for standalone mode. For cross-server communication use {@link net.jadedmc.jadedparty.bukkit.cache.types.RedisCache}
 */
public class MemoryCache implements Cache {
    private final JadedPartyBukkit plugin;
    private final Map<String, Document> partyDocumentCache = new HashMap<>();
    private final Map<String, Document> playerDocumentCache = new HashMap<>();
    private final MessageProcessor messageProcessor;

    /**
     * Creates the cache.
     * @param plugin Instance of the plugin.
     */
    public MemoryCache(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
        this.messageProcessor = new MessageProcessor(plugin);
    }

    /**
     * Deletes a party document from the cache given key.
     * @param nanoID NanoID for the document.
     */
    @Override
    public void deletePartyDocument(@NotNull final String nanoID) {
        this.partyDocumentCache.remove(nanoID);
    }

    /**
     * Deletes a player document from the cache given key.
     * @param uuid UUID for the document.
     */
    @Override
    public void deletePlayerDocument(@NotNull final String uuid) {
        this.playerDocumentCache.remove(uuid);
    }

    /**
     * Gets a party document from the cache based on it's given NanoID.
     * @param nanoID NanoID to the document.
     */
    @Override
    public Document getPartyDocument(@NotNull final String nanoID) {
        return this.partyDocumentCache.get(nanoID);
    }

    /**
     * Gets a player document from the cache based on a given uuid.
     * @param uuid UUID to the document.
     */
    @Override
    public Document getPlayerDocument(@NotNull final String uuid) {
        return this.playerDocumentCache.get(uuid);
    }

    /**
     * Get all party documents in the cache.
     * @return All documents in the cache.
     */
    @Override
    public Collection<Document> getAllPartyDocuments() {
        return partyDocumentCache.values();
    }

    /**
     * Get all player documents in the cache.
     * @return All player documents in the cache.
     */
    @Override
    public Collection<Document> getAllPlayerDocuments() {
        return playerDocumentCache.values();
    }

    /**
     * Get the Cache's message processor.
     * Used for processing pub/sub messages.
     * @return Message Processor.
     */
    @Override
    public MessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }

    /**
     * Check if a player is stored in the cache.
     * Mostly important for Redis.
     * @param player Player to check if they are stored.
     * @return true if they're in the cache, false if not.
     */
    @Override
    public boolean hasPlayer(@NotNull final Player player) {
        return playerDocumentCache.containsKey("jadedparty:players:" + player.getUniqueId().toString());
    }

    /**
     * Adds a party document to the cache with a given NanoID.
     * @param nanoID NanoID of the document being added.
     * @param document Document being added to the cache.
     */
    @Override
    public void setPartyDocument(@NotNull final String nanoID, @NotNull final Document document) {
        this.partyDocumentCache.put(nanoID, document);
    }

    /**
     * Adds a player document to the cache with a given UUID.
     * @param uuid UUID of the document being added.
     * @param document Document being added to the cache.
     */
    @Override
    public void setPlayerDocument(@NotNull final String uuid, @NotNull final Document document) {
        this.playerDocumentCache.put(uuid, document);
    }

    /**
     * Publishes a message to the cache.
     * @param channel Channel the message should be sent to.
     * @param message Message that should be sent.
     */
    @Override
    public void publish(@NotNull final String channel, @NotNull final String message) {
        this.messageProcessor.process(channel, message);

        // Log debug message if debug mode is enabled.
        if(plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[MEMORY PUB] " + channel + " " + message);
        }
    }

    /**
     * Publishes a message to the cache.
     * @param channel Channel the message should be sent to.
     * @param subChannel Sub channel the message should be sent to.
     * @param message Message that should be sent.
     */
    @Override
    public void publish(@NotNull final String channel, @NotNull final String subChannel, @NotNull final String message) {
        this.publish(channel, subChannel + " " + message);
    }
}