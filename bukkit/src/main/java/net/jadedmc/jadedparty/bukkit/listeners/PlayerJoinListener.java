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
import net.jadedmc.jadedparty.bukkit.party.PartyPlayer;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {
    private final JadedPartyBukkit plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerJoinListener(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerJoinEvent.
     */
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Caches the player as a PartyPlayer.
        if(plugin.getConfigManager().isStandalone()) {
            // Creates a PartyPlayer and caches it.
            final PartyPlayer partyPlayer = plugin.getPartyManager().cachePartyPlayer(player);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, partyPlayer::silentUpdate);
        }
        else {
            // For cross-server mode get their document from the remote cache.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                if(plugin.getConfigManager().getCache().hasPlayer(player)) {
                    final Document document = plugin.getConfigManager().getCache().getPlayerDocument(player.getUniqueId().toString());
                    plugin.getPartyManager().cachePartyPlayer(document);
                }
                else {
                    final PartyPlayer partyPlayer = plugin.getPartyManager().cachePartyPlayer(player);
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, partyPlayer::silentUpdate);
                }
            });
        }
    }
}
