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
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses a Redis database to cache party data and send plugin messages between servers.
 * Primarily used for sharing parties across servers.
 */
public class RedisCache implements Cache {
    private final JadedPartyBukkit plugin;
    private final MessageProcessor messageProcessor;

    /**
     * Creates the cache.
     * @param plugin Instance of the plugin.
     */
    public RedisCache(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;

        // Attempts to connect to Redis.
        plugin.getRedis().connect();

        this.messageProcessor = new MessageProcessor(plugin);
    }

    /**
     * Deletes a document from the cache given key.
     * @param key Key for the document.
     */
    @Override
    public void delete(@NotNull final String key) {
        plugin.getRedis().del(key);
    }

    /**
     * Gets a document from the cache based on it's given key.
     * @param key Key to the document.
     */
    @Override
    public Document get(@NotNull final String key) {
        try(Jedis jedis = plugin.getRedis().jedisPool().getResource()) {
            return Document.parse(jedis.get(key));
        }
    }

    /**
     * Get all documents in the cache, matching a given pattern.
     * @param pattern Pattern to be matched.
     * @return All documents in the cache.
     */
    @Override
    public Collection<Document> getAll(@NotNull final String pattern) {
        final Collection<Document> documents = new HashSet<>();

        try(Jedis jedis = plugin.getRedis().jedisPool().getResource()) {
            final Set<String> keys = jedis.keys(pattern);

            for(@NotNull final String key : keys) {
                final String json = jedis.get(key);
                final Document document = Document.parse(json);
                documents.add(document);
            }
        }

        return documents;
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
     * Caches a document with a given key.
     * @param key Key of the document being added.
     * @param document Document being added to the cache.
     */
    @Override
    public void set(@NotNull final String key, @NotNull final Document document) {
        plugin.getRedis().set(key, document.toJson());
    }

    /**
     * Publishes a message to the cache.
     * @param channel Channel the message should be sent to.
     * @param message Message that should be sent.
     */
    @Override
    public void publish(@NotNull final String channel, @NotNull final String message) {
        plugin.getRedis().publishAsync(channel, message);

        // Log debug message if debug mode is enabled.
        if(plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[REDIS PUB] " + channel + " " + message);
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