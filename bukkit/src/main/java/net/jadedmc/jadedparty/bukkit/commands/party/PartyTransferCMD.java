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
import net.jadedmc.jadedparty.bukkit.utils.Tuple;
import net.jadedmc.jadedparty.bukkit.utils.chat.ChatUtils;
import net.jadedmc.jadedparty.bukkit.utils.player.PlayerMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Powers the /party transfer command, which transfers the party to a new player.
 * <p>
 * Usage: /party transfer [player]
 *   - [player]: The new leader of the party.
 */
public class PartyTransferCMD {
    private final JadedPartyBukkit plugin;

    /**
     * Creates the sub command.
     * @param plugin Instance of the plugin.
     */
    public PartyTransferCMD(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    public void execute(@NotNull final Player player, @NotNull final String[] args) {
        final Party party = plugin.getPartyManager().getLocalPartyFromPlayer(player);

        // Makes sure the player is in a party.
        if(party == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You are not in a party! /party create.");
            return;
        }

        // Makes sure the player is using the command correctly.
        if(args.length == 1) {
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_TRANSFER_USAGE));
            return;
        }

        // Makes sure they have permission.
        if(party.getPlayer(player).getRole() != PartyRole.LEADER) {
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_TRANSFER_NOT_ALLOWED));
            return;
        }

        if(args[1].equalsIgnoreCase(player.getName())) {
            ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_TRANSFER_CANNOT_TRANSFER_TO_SELF));
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final PlayerMap<PartyPlayer> remotePlayers = plugin.getPartyManager().getRemotePartyPlayers();
            String username = args[1];

            if(!remotePlayers.contains(username)) {
                ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_TRANSFER_TARGET_NOT_ONLINE));
                return;
            }

            final PartyPlayer remoteTargetPlayer = remotePlayers.get(username);
            if(!party.getPlayers().contains(remoteTargetPlayer)) {
                ChatUtils.chat(player, plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_TRANSFER_TARGET_NOT_IN_PARTY));
                return;
            }

            final PartyPlayer localTargetPlayer = party.getPlayer(remoteTargetPlayer.getUniqueId());
            final Tuple<String, String> placeholder = new Tuple<>("%target_name%", localTargetPlayer.getName());

            // Transfers the party.
            final PartyPlayer localPartyPlayer = party.getPlayer(player);
            localPartyPlayer.setRole(PartyRole.MODERATOR);
            localPartyPlayer.update();

            localTargetPlayer.setRole(PartyRole.LEADER);
            localTargetPlayer.update();

            party.update();
            party.sendMessage(plugin.getConfigManager().getMessage(player, ConfigMessage.PARTY_PROMOTE_TARGET_PROMOTED_LEADER, placeholder));
        });
    }
}