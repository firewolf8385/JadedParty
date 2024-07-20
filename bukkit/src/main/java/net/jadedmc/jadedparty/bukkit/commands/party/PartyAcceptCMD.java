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
import net.jadedmc.jadedparty.bukkit.utils.chat.ChatUtils;
import net.jadedmc.jadedparty.bukkit.utils.player.PlayerMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

/**
 * Powers the /party accept command, which allows a player to accept a party invite.
 * <p>
 * Usage: /party accept [player]
 *   - [player]: The player who sent the invite.
 */
public class PartyAcceptCMD {
    private final JadedPartyBukkit plugin;

    /**
     * Creates the sub command.
     * @param plugin Instance of the plugin.
     */
    public PartyAcceptCMD(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    public void execute(@NotNull final Player player, final String[] args) {

        // Makes sure the player is using the command correctly.
        if(args.length == 1) {
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_ACCEPT_USAGE));
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final PlayerMap<PartyPlayer> remotePlayers = plugin.getPartyManager().getRemotePartyPlayers();
            String username = args[1];

            if(!remotePlayers.contains(username)) {
                ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not online");
                return;
            }

            UUID uuid = remotePlayers.get(username).getUniqueId();

            Collection<Party> remoteParties = plugin.getPartyManager().getRemoteParties();
            boolean inParty = false;
            Party party = null;

            for(Party remoteParty : remoteParties) {
                if(remoteParty.getPlayers().contains(uuid)) {
                    party = remoteParty;
                    inParty = true;
                    break;
                }
            }

            if(!inParty) {
                ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player is not in a party");
                return;
            }

            if(!party.getInvites().contains(player.getUniqueId())) {
                ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You do not have an invite to that party.");
                System.out.println("Invites Found: " + party.getInvites().size());

                for(UUID inviteUUID : party.getInvites()) {
                    System.out.println(inviteUUID.toString());
                }
                return;
            }

            // Display the other players in the party.
            {
                ChatUtils.chat(player, "<green>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</green>");
                ChatUtils.chat(player, ChatUtils.centerText("<green><bold>You are partying with"));
                ChatUtils.chat(player, "");

                final StringBuilder members = new StringBuilder();
                for(PartyPlayer partyPlayer : party.getPlayers().values()) {
                    if(partyPlayer.getUniqueId().equals(player.getUniqueId())) {
                        continue;
                    }

                    members.append("<gray>");
                    members.append(partyPlayer.getName());
                    members.append("<green>,");
                }

                ChatUtils.chat(player, members.substring(0, members.length() - 1));
                ChatUtils.chat(player, "");
                ChatUtils.chat(player, "<green>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</green>");
            }

            party.addPlayer(player, PartyRole.MEMBER);
            party.update();
            party.sendMessage("<green><bold>Party</bold> <dark_gray>» " + "<gray>" + player.getName() + " <green>has joined the party.");
        });
    }
}