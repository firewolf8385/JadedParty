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
     * Deletes a party document from the cache given key.
     * @param nanoID NanoID for the document.
     */
    void deletePartyDocument(@NotNull final String nanoID);

    /**
     * Deletes a player document from the cache given key.
     * @param uuid UUID for the document.
     */
    void deletePlayerDocument(@NotNull final String uuid);

    /**
     * Get all party documents in the cache.
     * @return All party documents in the cache.
     */
    Collection<Document> getAllPartyDocuments();

    /**
     * Get all player documents in the cache.
     * @return All player documents in the cache.
     */
    Collection<Document> getAllPlayerDocuments();

    /**
     * Gets a party document from the cache based on a given NanoID.
     * @param nanoID NanoID to the document.
     */
    Document getPartyDocument(@NotNull final String nanoID);

    /**
     * Gets a player document from the cache based on a given uuid.
     * @param uuid UUID to the document.
     */
    Document getPlayerDocument(@NotNull final String uuid);

    /**
     * Get the Cache's message processor.
     * Used for processing pub/sub messages.
     * @return Message Processor.
     */
    MessageProcessor getMessageProcessor();

    /**
     * Adds a party document to the cache with a given NanoID.
     * @param nanoID NanoID of the document being added.
     * @param document Document being added to the cache.
     */
    void setPartyDocument(@NotNull final String nanoID, @NotNull final Document document);

    /**
     * Adds a player document to the cache with a given UUID.
     * @param uuid UUID of the document being added.
     * @param document Document being added to the cache.
     */
    void setPlayerDocument(@NotNull final String uuid, @NotNull final Document document);

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