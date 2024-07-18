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
package net.jadedmc.jadedparty.bukkit.cache;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents a method of storing Party-related JSON documents.
 */
public interface Cache {

    /**
     * Deletes a document from the cache given key.
     * @param key Key for the document.
     */
    void delete(@NotNull final String key);

    /**
     * Gets a document from the cache based on it's given key.
     * @param key Key to the document.
     */
    Document get(@NotNull final String key);

    /**
     * Get all documents in the cache, matching a given pattern.
     * @param pattern Pattern to be matched.
     * @return All documents in the cache.
     */
    Collection<Document> getAll(@NotNull final String pattern);

    /**
     * Get the Cache's message processor.
     * Used for processing pub/sub messages.
     * @return Message Processor.
     */
    MessageProcessor getMessageProcessor();

    /**
     * Adds a document to the cache with a given key.
     * @param key Key of the document being added.
     * @param document Document being added to the cache.
     */
    void set(@NotNull final String key, @NotNull final Document document);

    /**
     * Publishes a message to the cache.
     * @param channel Channel the message should be sent to.
     * @param message Message that should be sent.
     */
    void publish(@NotNull final String channel, @NotNull final String message);

    /**
     * Publishes a message to the cache.
     * @param channel Channel the message should be sent to.
     * @param subChannel Sub channel the message should be sent to.
     * @param message Message that should be sent.
     */
    void publish(@NotNull final String channel, @NotNull final String subChannel, @NotNull final String message);
}