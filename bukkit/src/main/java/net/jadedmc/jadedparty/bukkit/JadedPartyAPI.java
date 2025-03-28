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
package net.jadedmc.jadedparty.bukkit;

import net.jadedmc.jadedparty.bukkit.party.Party;
import net.jadedmc.jadedparty.bukkit.party.PartySet;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class JadedPartyAPI {
    private static JadedPartyBukkit plugin;

    public static void initialize(JadedPartyBukkit pl) {
        plugin = pl;
    }

    public static Party getParty(Player player) {
        return plugin.getPartyManager().getLocalPartyFromPlayer(player);
    }

    public static Party getParty(@NotNull final UUID uuid) {
        return plugin.getPartyManager().getLocalPartyFromPlayer(uuid);
    }

    public static Party getParty(NanoID nanoID) {
        return plugin.getPartyManager().getLocalPartyFromNanoID(nanoID);
    }

    public static PartySet getRemoteParties() {
        final PartySet partySet = new PartySet();

        for(final Document document : plugin.getConfigManager().getCache().getAllPartyDocuments()) {

        }

        return partySet;
    }
}
