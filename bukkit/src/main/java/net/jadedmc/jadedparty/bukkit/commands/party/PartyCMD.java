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
import net.jadedmc.jadedparty.bukkit.settings.ConfigMessage;
import net.jadedmc.jadedparty.bukkit.utils.chat.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyCMD implements CommandExecutor {
    private final JadedPartyBukkit plugin;
    private final PartyAcceptCMD partyAcceptCMD;
    private final PartyCreateCMD partyCreateCMD;
    private final PartyDisbandCMD partyDisbandCMD;
    private final PartyHelpCMD partyHelpCMD;
    private final PartyLeaveCMD partyLeaveCMD;
    private final PartyListCMD partyListCMD;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public PartyCMD(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;

        // Load the sub commands.
        this.partyAcceptCMD = new PartyAcceptCMD(plugin);
        this.partyCreateCMD = new PartyCreateCMD(plugin);
        this.partyDisbandCMD = new PartyDisbandCMD(plugin);
        this.partyHelpCMD = new PartyHelpCMD(plugin);
        this.partyLeaveCMD = new PartyLeaveCMD(plugin);
        this.partyListCMD = new PartyListCMD(plugin);
    }

    /**
     * Executes the command.
     * @param sender Sender of the command.
     * @param command Command being sent.
     * @param label Actual String being used to start the command.
     * @param args Command arguments.
     * @return true.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only players can use party commands.
        if(!(sender instanceof Player player)) {
            ChatUtils.chat(sender, plugin.getConfigManager().getMessage(ConfigMessage.PARTY_ERROR_NOT_A_PLAYER));
            return true;
        }

        // If no arguments are provided, sends the player to the help screen.
        if(args.length == 0) {
            partyHelpCMD.execute(player, args);
            return true;
        }

        // Processes the sub command.
        switch(args[0].toLowerCase()) {
            case "accept" -> partyAcceptCMD.execute(player, args);
            case "create" -> partyCreateCMD.execute(player, args);
            case "disband" -> partyDisbandCMD.execute(player, args);
            case "help", "commands", "?" -> partyHelpCMD.execute(player, args);
            case "leave" -> partyLeaveCMD.execute(player, args);
            case "list", "online" -> partyListCMD.execute(player, args);
            // TODO: default: invite player with said username.
        }

        return true;
    }
}