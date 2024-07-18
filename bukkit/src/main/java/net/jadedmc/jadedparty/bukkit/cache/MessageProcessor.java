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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.party.Party;
import net.jadedmc.jadedparty.bukkit.party.PartyPlayer;
import net.jadedmc.jadedparty.bukkit.utils.StringUtils;
import net.jadedmc.jadedparty.bukkit.utils.chat.ChatUtils;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

/**
 * Reads and processes plugin messages.
 * Primarily exists due to Redis Pub/Sub.
 */
public class MessageProcessor {
    private final JadedPartyBukkit plugin;

    /**
     * Creates the message processor.
     * @param plugin Instance of the plugin.
     */
    public MessageProcessor(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Processes a message from a given channel.
     * @param channel Channel the message is from.
     * @param message Message to be processed.
     */
    public void process(@NotNull final String channel, @NotNull final String message) {
        switch (channel) {
            case "party" -> partyChannel(message);
            case "jadedparty" -> jadedPartyChannel(message);
        }
    }

    /**
     * Processes plugin messages from the "party" channel.
     * @param message Message to be processed.
     */
    private void partyChannel(@NotNull final String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final String[] args = message.split(" ");

            // Compare sub channels.
            switch(args[0].toLowerCase()) {
                // Disbands a specified party.
                case "disband" -> {
                    final NanoID partyNanoID = NanoID.fromString(args[1]);
                    final Party party = plugin.getPartyManager().getLocalPartyFromNanoID(partyNanoID);

                    // Make sure the party exists before deleting.
                    if(party == null) {
                        return;
                    }

                    // Delete the local party.
                    plugin.getPartyManager().deleteLocalParty(party);
                }

                // Adds a given player to the specified party.
                case "join" -> {
                    final NanoID partyNanoID = NanoID.fromString(args[1]);
                    final UUID playerUUID = UUID.fromString(args[2]);
                    final Party party = plugin.getPartyManager().getLocalPartyFromNanoID(partyNanoID);

                    final Player player = plugin.getServer().getPlayer(playerUUID);
                    if(player == null || !player.isOnline()) {
                        return;
                    }

                    final Document document = plugin.getConfigManager().getCache().getPartyDocument(partyNanoID.toString());

                    // Creates a local copy of the party if it exists, otherwise
                    if(party == null) {
                        plugin.getPartyManager().loadPartyFromDocument(document);
                    }
                    else {
                        party.update(document);
                    }
                }

                // Removes a given player from the specified party.
                case "leave" -> {
                    final NanoID partyNanoID = NanoID.fromString(args[1]);
                    final UUID playerUUID = UUID.fromString(args[2]);
                    final Party party = plugin.getPartyManager().getLocalPartyFromNanoID(partyNanoID);

                    if(party == null) {
                        return;
                    }

                    party.removePlayer(playerUUID);
                }

                // Update's a party by getting an updated version of it's document.
                case "update" -> {
                    final NanoID partyNanoID = NanoID.fromString(args[1]);
                    final Party party = plugin.getPartyManager().getLocalPartyFromNanoID(partyNanoID);

                    if(party == null) {
                        return;
                    }

                    final Document document = plugin.getConfigManager().getCache().getPartyDocument(partyNanoID.toString());
                    party.update(document);
                }

                case "updateplayer" -> {
                    final UUID playerUUID = UUID.fromString(args[1]);
                    final PartyPlayer partyPlayer = plugin.getPartyManager().getLocalPartyPlayers().get(playerUUID);

                    if(partyPlayer == null) {
                        return;
                    }

                    final Document document = plugin.getConfigManager().getCache().getPlayerDocument(playerUUID.toString());
                    partyPlayer.update(document);
                }
            }
        });
    }

    /**
     * Processes plugin messages for the "jadedparty" channel.
     * Contains non-party specific cross-server messages.
     * @param msg Message being processed.
     */
    private void jadedPartyChannel(@NotNull final String msg) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final String[] args = msg.split(" ");

            switch(args[0].toLowerCase()) {
                // Sends a message to a specific player or group of players no matter what server they are on.
                case "message" -> {
                    final String[] playerUUIDs = args[1].split(",");
                    final String message = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");

                    // Loop through all specified players in the message.
                    for(final String playerUUID : playerUUIDs) {
                        final UUID uuid = UUID.fromString(playerUUID);

                        // Skip the player if they are not online.
                        if(plugin.getServer().getPlayer(uuid) == null) {
                            continue;
                        }

                        // Sends the player the message.
                        final Player player = plugin.getServer().getPlayer(uuid);
                        ChatUtils.chat(player, message);
                    }
                }

                // Tells a player to connect to a different server.
                case "connect" -> {
                    final String[] playerUUIDs = args[1].split(",");
                    final String serverName = args[2];

                    // Loop through all specified players.
                    for(final String playerUUID : playerUUIDs) {
                        final UUID uuid = UUID.fromString(playerUUID);

                        // Skip the player if they are not on this server.
                        if(plugin.getServer().getPlayer(uuid) == null) {
                            continue;
                        }

                        // Switch the player's server.
                        final Player player = plugin.getServer().getPlayer(uuid);
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF(serverName);
                        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    }
                }
            }
        });
    }
}