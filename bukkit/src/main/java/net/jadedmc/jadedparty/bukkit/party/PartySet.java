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

import net.jadedmc.jadedparty.bukkit.utils.player.PluginPlayer;
import net.jadedmc.nanoid.NanoID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;

/**
 * Stores a HashSet of {@link net.jadedmc.jadedparty.bukkit.party.Party} objects.
 */
public class PartySet extends HashSet<Party> {

    /**
     * Checks if one of the parties in the Set have a given Player.
     * @param playerUUID UUID of the player to check the parties for.
     * @return Whether one of the parties contains this player.
     */
    public boolean containsPlayer(@NotNull final UUID playerUUID) {
        // Loop through each party looking for the player.
        for(Party party : this) {
            // If the player is found, exit the loop.
            if(party.getPlayers().contains(playerUUID)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if one of the parties in the Set have a given Player.
     * @param player Player to check the parties for.
     * @return Whether one of the parties contains this player.
     */
    public boolean containsPlayer(@NotNull final Player player) {
        return containsPlayer(player.getUniqueId());
    }

    /**
     * Retrieves a Party containing a given Player, identified via their UUID.
     * Returns null if a corresponding Party is not found.
     * @param playerUUID UUID of the player we are looking for.
     * @return Party that contains the player.
     */
    @Nullable
    public Party getFromPlayer(@NotNull final UUID playerUUID) {
        // Loop through each party looking for the player.
        for(Party party : this) {
            // If the player is found, return that party.
            if (party.getPlayers().contains(playerUUID)) {
                return party;
            }
        }

        // If no matching parties are found, return null.
        return null;
    }

    /**
     * Retrieves a Party containing a given Player.
     * Returns null if a corresponding Party is not found.
     * @param player Player we are looking for.
     * @return Party that contains the player.
     */
    @Nullable
    public Party getFromPlayer(@NotNull final Player player) {
        return getFromPlayer(player.getUniqueId());
    }

    /**
     * Retrieves a Party containing a given Player.
     * Returns null if a corresponding Party is not found.
     * @param pluginPlayer Player we are looking for.
     * @return Party that contains the player.
     */
    @Nullable
    public Party getFromPlayer(@NotNull final PluginPlayer pluginPlayer) {
        return getFromPlayer(pluginPlayer.getUniqueId());
    }

    /**
     * Retrieves a Party containing a player with a given username.
     * Returns null if a corresponding Party is not found.
     * @param playerUsername Username of the player being looked for.
     * @return Party that contains the player.
     */
    @Nullable
    public Party getFromUsername(@NotNull String playerUsername) {
        // Loop through each party looking for the player.
        for(Party party : this) {
            // If the player is found, return that party.
            if (party.getPlayers().contains(playerUsername)) {
                return party;
            }
        }

        // If no matching parties are found, return null.
        return null;
    }

    /**
     * Retrieves a Party object from the set given its NanoID.
     * Returns null if a corresponding Party is not found.
     * @param partyNanoID NanoID of the target party.
     * @return Party that corresponds to the given NanoID. Null if none found.
     */
    @Nullable
    public Party getFromNanoID(@NotNull final NanoID partyNanoID) {
        for(Party party : this) {
            if(party.getNanoID().equals(partyNanoID)) {
                return party;
            }
        }

        return null;
    }
}