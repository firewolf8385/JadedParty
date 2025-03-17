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
    PARTY_DEMOTE_CANNOT_DEMOTE_MEMBER("Messages.Party.Demote.CANNOT_DEMOTE_MEMBER", "<green><bold>Party</bold> <dark_gray>» <white>%target_name% <green>has been demoted to member."),
    PARTY_DEMOTE_CANNOT_DEMOTE_SELF("Messages.Party.Demote.CANNOT_DEMOTE_SELF", "<red><bold>Error</bold> <dark_gray>» <red>You cannot demote yourself!"),
    PARTY_DEMOTE_NOT_ALLOWED("Messages.Party.Demote.NOW_ALLOWED", "<red><bold>Error</bold> <dark_gray>» <red>Only the party leader can demote members!"),
    PARTY_DEMOTE_TARGET_DEMOTED_MEMBER("Messages.Party.Demote.TARGET_DEMOTED_MEMBER", "<red><bold>Error</bold> <dark_gray>» <red>You cannot demote that player any lower!"),
    PARTY_DEMOTE_TARGET_NOT_IN_PARTY("Messages.Party.Demote.TARGET_NOT_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>That player is not in your party!"),
    PARTY_DEMOTE_TARGET_NOT_ONLINE("Messages.Party.Demote.TARGET_NOT_ONLINE", "<red><bold>Error</bold> <dark_gray>» <red>That player is not online"),
    PARTY_DEMOTE_USAGE("Messages.Party.Demote.USAGE", "<red><bold>Usage</bold> <dark_gray>» <red>/party demote [player]"),
    PARTY_DISBAND_NOT_ALLOWED("Messages.Party.Disband.NOT_ALLOWED", "<red><bold>Error</bold> <dark_gray>» <red>You do not have permission to disband the party!"),
    PARTY_DISBAND_PARTY_DISBANDED("Messages.Party.Disband.PARTY_DISBANDED", "<green><bold>Party</bold> <dark_gray>» <green>The party has been disbanded."),
    PARTY_ERROR_ALREADY_IN_PARTY("Messages.Party.Error.ALREADY_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>You are already in a party."),
    PARTY_ERROR_NOT_A_PLAYER("Messages.Party.Error.NOT_A_PLAYER", "<red><bold>Error</bold> <dark_gray>» <red>Only players can use that command."),
    PARTY_ERROR_NOT_IN_PARTY("Messages.Party.Error.NOT_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! Create one with /p create."),
    PARTY_INVITE_NOT_ALLOWED("Messages.Party.Invite.NOT_ALLOWED", "<red><bold>Error</bold> <dark_gray>» <red>You do not have permission to invite other players!"),
    PARTY_INVITE_CANNOT_INVITE_SELF("Messages.Party.Invite.CANNOT_INVITE_SELF", "<red><bold>Error</bold> <dark_gray>» <red>You cannot invite yourself!"),
    PARTY_INVITE_TARGET_NOT_ONLINE("Messages.Party.Invite.TARGET_NOT_ONLINE", "<red><bold>Error</bold> <dark_gray>» <red>That player is not online"),
    PARTY_INVITE_TARGET_IN_PARTY("Messages.Party.Invite.TARGET_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>That player is already in a party!"),
    PARTY_INVITE_PENDING_INVITE("Messages.Party.Invite.PENDING_INVITE", "<red><bold>Error</bold> <dark_gray>» <red>You already have a pending invite to that person."),
    PARTY_INVITE_INVITE_SEND("Messages.Party.Invite.INVITE_SENT", "<green><bold>Party</bold> <dark_gray>» <gray>username <green>has been invited to the party."),
    PARTY_INVITE_INVITE_RECEIVED("Messages.Party.Invite.INVITE_RECEIVED", "<red>If you see me something went wrong..."),
    PARTY_LEAVE_PLAYER_LEFT("Messages.Party.Leave.PLAYER_LEFT", "<green><bold>Party</bold> <dark_gray>» <gray>%player_name% <green>has left the party."),
    PARTY_PROMOTE_CANNOT_PROMOTE_SELF("Messages.Party.Promote.CANNOT_PROMOTE_SELF", "<red><bold>Error</bold> <dark_gray>» <red>You cannot promote yourself!"),
    PARTY_PROMOTE_NOT_ALLOWED("Messages.Party.Promote.NOT_ALLOWED", "<red><bold>Error</bold> <dark_gray>» <red>Only the party leader can promote members!"),
    PARTY_PROMOTE_TARGET_NOT_IN_PARTY("Messages.Party.Promote.TARGET_NOT_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>That player is not in your party!"),
    PARTY_PROMOTE_TARGET_NOT_ONLINE("Messages.Party.Promote.TARGET_NOT_ONLINE", "<red><bold>Error</bold> <dark_gray>» <red>That player is not online"),
    PARTY_PROMOTE_TARGET_PROMOTED_LEADER("Messages.Party.Promote.TARGET_PROMOTED_LEADER", "<green><bold>Party</bold> <dark_gray>» <white>%target_name% <green>has been promoted to leader."),
    PARTY_PROMOTE_TARGET_PROMOTED_MODERATOR("Messages.Party.Promote.TARGET_PROMOTED_MODERATOR", "<green><bold>Party</bold> <dark_gray>» <white>%target_name% <green>has been promoted to moderator."),
    PARTY_PROMOTE_USAGE("Messages.Party.Promote.USAGE", "<red><bold>Usage</bold> <dark_gray>» <red>/party promote [player]"),
    PARTY_TRANSFER_CANNOT_TRANSFER_TO_SELF("Messages.Party.Transfer.CANNOT_TRANSFER_TO_SELF", "<red><bold>Error</bold> <dark_gray>» <red>You are already the party leader!"),
    PARTY_TRANSFER_NOT_ALLOWED("Messages.Party.Transfer.NOT_ALLOWED", "<red><bold>Error</bold> <dark_gray>» <red>Only the party leader can transfer the party!"),
    PARTY_TRANSFER_PARTY_TRANSFERRED("Messages.Party.Transfer.PARTY_TRANSFERRED", "<green><bold>Party</bold> <dark_gray>» <green>The party has been transferred to <gray>%target_name%<green>!"),
    PARTY_TRANSFER_TARGET_NOT_IN_PARTY("Messages.Party.Transfer.TARGET_NOT_IN_PARTY", "<red><bold>Error</bold> <dark_gray>» <red>That player is not in your party!"),
    PARTY_TRANSFER_TARGET_NOT_ONLINE("Messages.Party.Transfer.TARGET_NOT_ONLINE", "<red><bold>Error</bold> <dark_gray>» <red>That player is not online"),
    PARTY_TRANSFER_USAGE("Messages.Party.Transfer.USAGE", "<red><bold>Usage</bold> <dark_gray>» <red>/party promote [player]");

    private final String key;
    private final String defaultMessage;

    /**
     * Creates the config message.
     * @param key Key value for messages.yml.
     * @param defaultMessage Default message, used when nothing is found in messages.yml.
     */
    ConfigMessage(@NotNull final String key, @NotNull final String defaultMessage) {
        this.key = key;
        this.defaultMessage = defaultMessage.replace("\n", "<newline>");
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