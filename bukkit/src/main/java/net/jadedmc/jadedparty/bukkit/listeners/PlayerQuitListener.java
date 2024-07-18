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
package net.jadedmc.jadedparty.bukkit.listeners;

import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.party.Party;
import net.jadedmc.jadedparty.bukkit.party.PartyPlayer;
import net.jadedmc.jadedparty.bukkit.party.PartyRole;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final JadedPartyBukkit plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerQuitListener(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerQuitEvent.
     */
    @EventHandler
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Party party = plugin.getPartyManager().getLocalParties().getFromPlayer(player);
        final PartyPlayer partyPlayer = plugin.getPartyManager().getLocalPartyPlayers().get(player);

        // Handle party logic on disconnect.
        if(party != null) {
            // Delete the party from local cache if no one else is online.
            final int onlinePlayers = party.getOnlinePlayers().size();

            if(onlinePlayers == 1 || partyPlayer.getRole() == PartyRole.LEADER) {
                plugin.getPartyManager().deleteLocalParty(party);

                // If the server is standalone, also disband the party.
                if(plugin.getConfigManager().isStandalone()) {
                    party.disband();
                }
            }
        }

        // Remove the PartyPlayer from the local cache.
        plugin.getPartyManager().getLocalPartyPlayers().remove(player);

        // If the server is standalone, also remove them from the remote cache.
        if(plugin.getConfigManager().isStandalone()) {
            final UUID uuid = player.getUniqueId();
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                plugin.getConfigManager().getCache().deletePlayerDocument(uuid.toString());

                if(plugin.getConfigManager().isDebugMode()) {
                    plugin.getLogger().info("deleted PartyPlayer for " + uuid);
                }
            }, 5);
        }
    }
}