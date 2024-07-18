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
import net.jadedmc.jadedparty.bukkit.utils.player.PluginPlayer;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyPlayer extends PluginPlayer {
    private final JadedPartyBukkit plugin;
    private PartyRole role;

    public PartyPlayer(@NotNull final JadedPartyBukkit plugin, @NotNull final Document document) {
        super(UUID.fromString(document.getString("uuid")), document.getString("username"));
        this.plugin = plugin;
        this.role = PartyRole.valueOf(document.getString("role"));
    }

    public PartyPlayer(@NotNull final JadedPartyBukkit plugin, @NotNull final Player player, final PartyRole role) {
        super(player.getUniqueId(), player.getName());
        this.plugin = plugin;
        this.role = role;
    }

    public PartyRole getRole() {
        return role;
    }

    public boolean isAOnline() {
        Player player = this.getBukkitPlayer();
        return (player != null && player.isOnline());
    }

    public void setRole(final PartyRole role) {
        this.role = role;
    }

    public Document toDocument() {
        return new Document()
                .append("uuid", getUniqueId().toString())
                .append("username", getName())
                .append("role", role.toString());
    }

    public void update(@NotNull final Document document) {
        this.role = PartyRole.valueOf(document.getString("role"));
    }

    public void update() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfigManager().getCache().setPlayerDocument(getUniqueId().toString(), toDocument());
            plugin.getConfigManager().getCache().publish("party", "updateplayer", getUniqueId().toString());
        });
    }

    public void silentUpdate() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfigManager().getCache().setPlayerDocument(getUniqueId().toString(), toDocument());
        });
    }
}