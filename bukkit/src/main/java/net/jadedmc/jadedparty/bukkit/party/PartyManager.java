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

import java.util.Collection;
import java.util.UUID;

/**
 * Manages all existing party, both Local (stored in memory) and Remote (stored in Redis).
 */
public class PartyManager {
    private final JadedPartyBukkit plugin;
    private final PartySet localParties = new PartySet();
    private final PlayerMap<PartyPlayer> localPartyPlayers = new PlayerMap<>();

    /**
     * Creates the party manager.
     * @param plugin Instance of the plugin.
     */
    public PartyManager(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a Local Party with a given leader.
     * @param leader Leader of the party.
     * @return Created Party.
     */
    @NotNull
    public Party createLocalParty(@NotNull final Player leader) {
        final Party party = new Party(plugin, leader);
        this.localParties.add(party);
        return party;
    }

    /**
     * Caches a given Party by storing it as a Local Party.
     * @param party Party to be cached.
     */
    public void cacheParty(@NotNull final Party party) {
        this.localParties.add(party);
    }

    /**
     * Caches a Player in the local PartyPlayer cache.
     * @param player Player to be cached.
     */
    public PartyPlayer cachePartyPlayer(@NotNull final Player player) {
        final PartyPlayer partyPlayer = new PartyPlayer(plugin, player, PartyRole.NONE);
        this.localPartyPlayers.add(partyPlayer);

        return partyPlayer;
    }

    /**
     * Caches a Player in the local PartyPlayer cache using their document.
     * @param document Document to be cached.
     */
    public void cachePartyPlayer(@NotNull final Document document) {
        this.localPartyPlayers.add(new PartyPlayer(plugin, document));
    }

    /**
     * Deletes a Party from the local Party cache.
     * @param party Party to be deleted.
     */
    public void deleteLocalParty(@NotNull final Party party) {
        this.localParties.remove(party);
    }

    /**
     * Deletes a PartyPlayer from the local Party Player cache.
     * @param player Player to be deleted.
     */
    public void deleteLocalPartyPlayer(@NotNull final Player player) {
        this.localPartyPlayers.remove(player);
    }

    /**
     * Loads a party object from a document.
     * Allows for using Party class methods on Remote Parties.
     * @param document Document to load the party object from.
     * @return Party object from the document.
     */
    @NotNull
    public Party loadPartyFromDocument(@NotNull final Document document) {
        return new Party(plugin, document);
    }

    /**
     * Retrieves a Set of all locally cached Parties.
     * @return Set containing parties stored in RAM.
     */
    @NotNull
    public PartySet getLocalParties() {
        return this.localParties;
    }

    /**
     * Retrieves a PlayerMap of all locally cached PartyPlayers.
     * @return PlayerMap containing PartyPlayers stored in RAM.
     */
    @NotNull
    public PlayerMap<PartyPlayer> getLocalPartyPlayers() {
        return this.localPartyPlayers;
    }

    /**
     * Retrieves a locally-cached party from its UUID.
     * Returns null if non are found.
     * @param player Player to get the Party of.
     * @return Corresponding Party object.
     */
    @Nullable
    public Party getLocalPartyFromPlayer(@NotNull final Player player) {
        return this.localParties.getFromPlayer(player);
    }

    /**
     * Retrieves a locally-cached party from a player's UUID.
     * Returns null if the player is not in a party.
     * @param uuid UUID of the player to get the party of.
     * @return Corresponding Party object.
     */
    @Nullable
    public Party getLocalPartyFromPlayer(@NotNull final UUID uuid) {
        return this.localParties.getFromPlayer(uuid);
    }

    /**
     * Retrieves a locally-cached party from its NanoID.
     * Returns null if non are found.
     * @param partyNanoID NanoID of target Party.
     * @return Corresponding Party object.
     */
    @Nullable
    public Party getLocalPartyFromNanoID(@NotNull final NanoID partyNanoID) {
        return this.localParties.getFromNanoID(partyNanoID);
    }

    /**
     * Retrieves a Set of all parties stored in the remote cache.
     * <b>Warning: Database operation. Call asynchronously.</b>
     * @return Set containing Parties grabbed from the remote cache.
     */
    @NotNull
    public PartySet getRemoteParties() {
        final PartySet remoteParties = new PartySet();

        for(@NotNull final Document document : plugin.getConfigManager().getCache().getAllPartyDocuments()) {
            remoteParties.add(new Party(plugin, document));
        }

        return remoteParties;
    }

    /**
     * Retrieves a PlayerMap of all party players stored in the remote cache.
     * <b>Warning: Database operation. Call asynchronously.</b>
     * @return PlayerMap containing all PartyPlayers grabbed from the remote cache.
     */
    @NotNull
    public PlayerMap<PartyPlayer> getRemotePartyPlayers() {
        final PlayerMap<PartyPlayer> remotePartyPlayers = new PlayerMap<>();

        for(final Document document : plugin.getConfigManager().getCache().getAllPlayerDocuments()) {
            remotePartyPlayers.add(new PartyPlayer(plugin, document));
        }

        return remotePartyPlayers;
    }
}