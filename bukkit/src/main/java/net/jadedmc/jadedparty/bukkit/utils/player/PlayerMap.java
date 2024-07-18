package net.jadedmc.jadedparty.bukkit.utils.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerMap<V> extends HashMap<UUID, V> {

    public void add(@NotNull final Player player) {
        this.put(player.getUniqueId(), (V) player);
    }

    public void add(@NotNull final PluginPlayer pluginPlayer) {
        this.put(pluginPlayer.getUniqueId(), (V) pluginPlayer);
    }

    /**
     * Converts all the online PluginPlayers to Bukkit Players.
     * @return Collection of Bukkit Players.
     */
    public Collection<Player> asBukkitPlayers() {
        final Collection<Player> bukkitPlayers = new HashSet<>();

        // Loop through all objects.
        for(final V object : this.values()) {
            // If it's not a CustomPlayer, skip it.
            if(!(object instanceof final PluginPlayer pluginPlayer)) {
                continue;
            }

            // Gets the Bukkit player being represented.
            final Player player = pluginPlayer.getBukkitPlayer();

            // Skip them if they are not online.
            if(player == null || !player.isOnline()) {
                continue;
            }

            // Otherwise, add them to the Set.
            bukkitPlayers.add(player);
        }

        return bukkitPlayers;
    }

    public boolean contains(@NotNull final Player player) {
        return this.containsKey(player.getUniqueId());
    }

    public boolean contains(@NotNull final PluginPlayer pluginPlayer) {
        return this.containsKey(pluginPlayer.getUniqueId());
    }

    public boolean contains(@NotNull final UUID uuid) {
        return this.containsKey(uuid);
    }

    public boolean contains(@NotNull final  String username) {
        for(final V object : this.values()) {
            if(!(object instanceof PluginPlayer pluginPlayer)) {
                continue;
            }

            if(pluginPlayer.getName().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    public V get(@NotNull final Player player) {
        return get(player.getUniqueId());
    }

    public V get(@NotNull final PluginPlayer pluginPlayer) {
        return get(pluginPlayer.getUniqueId());
    }

    /**
     * Gets all PluginPlayers that are on this instance.
     * @return Collection of online CustomPlayers.
     */
    public Set<V> getOnlinePlayers() {
        final Set<V> onlinePlayers = new HashSet<>();

        // Loop through all objects.
        for(final V object : this.values()) {
            // If it's not a CustomPlayer, skip it.
            if(!(object instanceof final PluginPlayer pluginPlayer)) {
                continue;
            }

            // Gets the Bukkit player being represented.
            final Player player = pluginPlayer.getBukkitPlayer();

            // Skip them if they are not online.
            if(player == null || !player.isOnline()) {
                continue;
            }

            // Otherwise, add them to the Set.
            onlinePlayers.add((V) pluginPlayer);
        }

        return onlinePlayers;
    }

    public void put(@NotNull final Player player) {
        add(player);
    }

    public void put(@NotNull final PluginPlayer pluginPlayer) {
        add(pluginPlayer);
    }

    public void remove(@NotNull final Player player) {
        this.remove(player.getUniqueId());
    }

    public void remove(@NotNull final PluginPlayer pluginPlayer) {
        this.remove(pluginPlayer.getUniqueId());
    }
}