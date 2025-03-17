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
package net.jadedmc.jadedparty.bukkit.commands.party;

import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import net.jadedmc.jadedparty.bukkit.party.Party;
import net.jadedmc.jadedparty.bukkit.party.PartyPlayer;
import net.jadedmc.jadedparty.bukkit.party.PartyRole;
import net.jadedmc.jadedparty.bukkit.settings.ConfigMessage;
import net.jadedmc.jadedparty.bukkit.utils.JadedUtils;
import net.jadedmc.jadedparty.bukkit.utils.Tuple;
import net.jadedmc.jadedparty.bukkit.utils.chat.ChatUtils;
import net.jadedmc.jadedparty.bukkit.utils.player.PlayerMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Powers the /party invite command, which invite's a player to the party.
 * <p>
 * Usage: /party invite [player]
 *   - [player]: The player to be invited.
 */
public class PartyInviteCMD {
    private final JadedPartyBukkit plugin;

    /**
     * Creates the sub command.
     * @param plugin Instance of the plugin.
     */
    public PartyInviteCMD(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    public void execute(@NotNull final Player player, @NotNull final String[] args) {
        final boolean newParty;
        Party party = plugin.getPartyManager().getLocalPartyFromPlayer(player);

        if(party == null) {
            // Creates the party.
            party = plugin.getPartyManager().createLocalParty(player);
            party.silentUpdate();
            newParty = true;

            party.getPlayer(player).update();
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_CREATE_PARTY_CREATED));
        }
        else {
            newParty = false;
        }

        PartyPlayer partyPlayer = party.getPlayer(player.getUniqueId());
        if(partyPlayer == null || partyPlayer.getRole() == PartyRole.MEMBER) {
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_NOT_ALLOWED));
            return;
        }

        if(args[1].equalsIgnoreCase(player.getName())) {
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_CANNOT_INVITE_SELF));
            return;
        }

        Party finalParty = party;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final PlayerMap<PartyPlayer> remotePlayers = plugin.getPartyManager().getRemotePartyPlayers();
            String username = args[1];

            if(!remotePlayers.contains(username)) {
                ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_TARGET_NOT_ONLINE));
                return;
            }

            final PartyPlayer targetPlayer = remotePlayers.get(username);
            if(targetPlayer.getRole() != PartyRole.NONE) {
                ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_TARGET_IN_PARTY));
                return;
            }

            // Makes sure the player wasn't already invited.
            if(finalParty.getInvites().contains(targetPlayer.getUniqueId())) {
                ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_PENDING_INVITE));
                return;
            }

            // Delay invite if the party is new.
            if(newParty) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }

            final Tuple<String, String> placeholder = new Tuple<>("%target_name%", targetPlayer.getName());

            finalParty.addInvite(targetPlayer.getUniqueId());
            finalParty.sendMessage(plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_INVITE_SEND, placeholder));
            finalParty.update();

            JadedUtils.sendMessage(targetPlayer.getUniqueId(), plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_INVITE_INVITE_RECEIVED));
        });
    }
}
