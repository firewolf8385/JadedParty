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
package net.jadedmc.jadedparty.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.jadedmc.jadedparty.velocity.JadedPartyVelocity;
import net.jadedmc.jadedparty.velocity.party.Party;
import net.jadedmc.jadedparty.velocity.party.PartyPlayer;
import net.jadedmc.jadedparty.velocity.party.PartyRole;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class DisconnectListener {
    private final JadedPartyVelocity plugin;

    public DisconnectListener(@NotNull final JadedPartyVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onDisconnect(final DisconnectEvent event) {
        final Player player = event.getPlayer();
        plugin.getRedis().del("jadedparty:players:" + player.getUniqueId().toString());

        try(Jedis jedis = plugin.getRedis().jedisPool().getResource()) {
            Set<String> names = jedis.keys("jadedparty:parties:*");

            // Loops through each stored party.
            for(String key : names) {
                Document document = Document.parse(jedis.get("jadedparty:parties:" + key.replace("jadedparty:parties:", "")));
                Party party = new Party(plugin, document);

                // If the player is in that party, cache the party to memory.
                if(party.hasPlayer(player)) {
                    PartyPlayer partyPlayer = party.getPlayer(player.getUniqueId());

                    if(partyPlayer.getRole() != PartyRole.LEADER) {
                        party.sendMessage("<green><bold>Party</bold> <dark_gray>» " + partyPlayer.getPrefix() + "<gray>" + partyPlayer.getName() + " <green>has left the party.");
                        party.removePlayer(player);
                        return;
                    }

                    party.sendMessage("<green><bold>Party</bold> <dark_gray>» <green>The party has been disbanded!");
                    party.disband();

                    break;
                }
            }
        }
    }
}
