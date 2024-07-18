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

import org.jetbrains.annotations.NotNull;

/**
 * Represents a configurable message in messages.yml.
 * Used to easily access configured messages and acts as a failsafe if one of them is missing.
 */
public enum ConfigMessage {
    PARTY_ACCEPT_USAGE("Messages.Party.Accept.USAGE", "<red><bold>Usage</bold> <dark_gray>» <red>/party accept [player]"),
    PARTY_CREATE_PARTY_CREATED("Messages.Party.Create.PARTY_CREATED", "<green><bold>Party</bold> <dark_gray>» <green>Party has been created."),
    PARTY_DISBAND_NOW_ALLOWED("Messages.Party.Disband.NOT_ALLOWED", "<red><bold>Error</bold> <dark_gray>» <red>You do not have permission to disband the party!"),
    PARTY_DISBAND_PARTY_DISBANDED("Messages.Party.Disband.PARTY_DISBANDED", "<green><bold>Party</bold> <dark_gray>» <green>The party has been disbanded."),
    PARTY_ERROR_ALREADY_IN_PARTY("Messages.Party.Error.ALREADY_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>You are already in a party."),
    PARTY_ERROR_NOT_A_PLAYER("Messages.Party.Error.NOT_A_PLAYER", "<red><bold>Error</bold> <dark_gray>» <red>Only players can use that command."),
    PARTY_ERROR_NOT_IN_PARTY("Messages.Party.Error.NOT_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! Create one with /p create."),
    PARTY_LEAVE_PLAYER_LEFT("Messages.Party.Leave.PLAYER_LEFT", "<green><bold>Party</bold> <dark_gray>» <gray>%player_name% <green>has left the party.");

    private final String key;
    private final String defaultMessage;

    /**
     * Creates the config message.
     * @param key Key value for messages.yml.
     * @param defaultMessage Default message, used when nothing is found in messages.yml.
     */
    ConfigMessage(@NotNull final String key, @NotNull final String defaultMessage) {
        this.key = key;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Gets the default message.
     * @return Default message string.
     */
    @NotNull
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Get the key of the message in messages.yml.
     * @return Message key.
     */
    @NotNull
    public String getKey() {
        return key;
    }
}