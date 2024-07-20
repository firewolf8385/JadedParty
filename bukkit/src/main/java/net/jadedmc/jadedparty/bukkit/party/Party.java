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
package net.jadedmc.jadedparty.bukkit.party;

import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.utils.player.PlayerMap;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a group of players playing together.
 */
public class Party {
    private final JadedPartyBukkit plugin;
    private final NanoID nanoID;
    private final PlayerMap<PartyPlayer> players = new PlayerMap<>();
    private final Collection<UUID> invites = new HashSet<>();

    /**
     * Creates the party using a Bson Document.
     * @param plugin Instance of the plugin.
     * @param document Bson document.
     */
    public Party(@NotNull final JadedPartyBukkit plugin, @NotNull final Document document) {
        this.plugin = plugin;
        this.nanoID = NanoID.fromString(document.getString("nanoID"));

        // Load the players from the document.
        final Document playersDocument = document.get("players", Document.class);
        for(final String player : playersDocument.keySet()) {
            players.add(new PartyPlayer(plugin, playersDocument.get(player, Document.class)));
        }

        // Load the pending invites of the party.
        final List<String> inviteUUIDs = document.getList("invites", String.class);
        for(final String uuid : inviteUUIDs) {
            this.invites.add(UUID.fromString(uuid));
        }
    }

    /**
     * Creates an empty party with a given leader.
     * @param plugin Instance of the plugin.
     * @param leader Leader of the party.
     */
    public Party(@NotNull final JadedPartyBukkit plugin, @NotNull final Player leader) {
        this.plugin = plugin;

        // Generates the party's NanoID with configured settings in config.yml.
        final NanoID.Settings nanoIDSettings = new NanoID.Settings()
                .setAlphabet(plugin.getConfigManager().getConfig().getString("Party.ID.alphabet"))
                .setMinimumSize(plugin.getConfigManager().getConfig().getInt("Party.ID.minLength"))
                .setMaximumSize(plugin.getConfigManager().getConfig().getInt("Party.ID.maxLength"))
                .setPrefix(plugin.getConfigManager().getConfig().getString("Party.ID.prefix"));
        this.nanoID = new NanoID(nanoIDSettings);

        // Adds the player to their party as the leader.
        addPlayer(leader, PartyRole.LEADER);
    }

    /**
     * Adds an invite to the party.
     * @param playerUUID UUID of the player being invited.
     */
    public void addInvite(@NotNull final UUID playerUUID) {
        this.invites.add(playerUUID);
    }

    /**
     * Adds a player to the party.
     * @param player Player to add to the party.
     * @param role Role the player has.
     */
    public void addPlayer(@NotNull final Player player, final PartyRole role) {
        final PartyPlayer partyPlayer = plugin.getPartyManager().getLocalPartyPlayers().get(player);
        partyPlayer.setRole(role);
        partyPlayer.update();

        this.players.add(partyPlayer);

        // Removes any potential pending invites for the player.
        this.invites.remove(player.getUniqueId());
    }

    /**
     * Disbands the party.
     */
    public void disband() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfigManager().getCache().publish("party", "disband", this.nanoID.toString());
            plugin.getConfigManager().getCache().deletePartyDocument(this.nanoID.toString());

            for(final PartyPlayer player : players.values()) {
                player.setRole(PartyRole.NONE);
                player.update();
            }
        });
    }

    /**
     * Gets a Collection of all current invites.
     * @return All active invites.
     */
    public Collection<UUID> getInvites() {
        return this.invites;
    }

    public NanoID getNanoID() {
        return this.nanoID;
    }

    /**
     * Get the PartyPlayer of a player, from their uuid.
     * @param playerUUID UUID of the player.
     * @return Corresponding PartyPlayer object.
     */
    public PartyPlayer getPlayer(@NotNull final UUID playerUUID) {
        return players.get(playerUUID);
    }

    /**
     * Get the PartyPlayer of a given Player.
     * @param player Player to get PartyPlayer of.
     * @return Corresponding PartyPlayer object.
     */
    @Nullable
    public PartyPlayer getPlayer(@NotNull final Player player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * Gets all party members who are on the current instance.
     * @return All online players.
     */
    public Collection<Player> getOnlinePlayers() {
        return this.players.asBukkitPlayers();
    }

    /**
     * Gets all players currently in the party.
     * @return All current players.
     */
    public PlayerMap<PartyPlayer> getPlayers() {
        return this.players;
    }

    /**
     * Removes an invite from the party.
     * @param playerUUID UUID of the player who was invited.
     */
    public void removeInvite(@NotNull final UUID playerUUID) {
        this.invites.remove(playerUUID);
    }

    /**
     * Removes a player from the party.
     * @param playerUUID UUID of the player to remove.
     */
    public void removePlayer(@NotNull final UUID playerUUID) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final PartyPlayer partyPlayer = this.players.get(playerUUID);
            partyPlayer.setRole(PartyRole.NONE);
            partyPlayer.update();

            players.remove(playerUUID);
        });
    }

    /**
     * Sends a message to all members of the party.
     * @param message Message to be sent.
     */
    public void sendMessage(@NotNull final String message) {
        final StringBuilder builder = new StringBuilder();
        players.values().forEach(partyPlayer -> {
            builder.append(partyPlayer.getUniqueId());
            builder.append(",");
        });

        final String targets = builder.substring(0, builder.length() - 1);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfigManager().getCache().publish("jadedparty", "message", targets + " " + message);
        });
    }

    /**
     * Updates the party in Redis without announcing the update.
     */
    public void silentUpdate() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfigManager().getCache().setPartyDocument(nanoID.toString(), toDocument());
        });
    }

    /**
     * Converts the cached party into a Bson Document.
     * @return Bson document of the party.
     */
    public Document toDocument() {
        final Document document = new Document();
        document.append("nanoID", this.nanoID.toString());

        final Document playersDocument = new Document();
        for(PartyPlayer player : players.values()) {
            playersDocument.append(player.getUniqueId().toString(), player.toDocument());
        }
        document.append("players", playersDocument);

        final List<String> invites = new ArrayList<>();
        this.invites.forEach(invite -> invites.add(invite.toString()));
        document.append("invites", invites);

        return document;
    }

    /**
     * Updates the party in Redis.
     */
    public void update() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfigManager().getCache().setPartyDocument(this.nanoID.toString(), toDocument());
            plugin.getConfigManager().getCache().publish("party", "update", this.nanoID.toString());
        });
    }

    /**
     * Updates the cached party with a given Bson document.
     * @param document Bson document to use.
     */
    public void update(@NotNull final Document document) {
        // Empty cached players.
        players.clear();

        // Loads the party players.
        final Document playersDocument = document.get("players", Document.class);
        for(@NotNull final String player : playersDocument.keySet()) {
            players.add(new PartyPlayer(plugin, playersDocument.get(player, Document.class)));
        }

        // Empty cached invites.
        this.invites.clear();

        // Loads new invites.
        final List<String> inviteUUIDs = document.getList("invites", String.class);
        for(@NotNull final String uuid : inviteUUIDs) {
            this.invites.add(UUID.fromString(uuid));
        }
    }
}