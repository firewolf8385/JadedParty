package net.jadedmc.jadedparty.velocity.party;

import com.velocitypowered.api.proxy.Player;
import net.jadedmc.jadedparty.velocity.JadedPartyVelocity;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a group of players playing together.
 */
public class Party {
    private final JadedPartyVelocity plugin;
    private final NanoID nanoID;
    private final Collection<PartyPlayer> players = new HashSet<>();
    private final Collection<UUID> invites = new HashSet<>();

    /**
     * Creates the party using a Bson Document.
     * @param document Bson document.
     */
    public Party(@NotNull final JadedPartyVelocity plugin, @NotNull final Document document) {
        this.plugin = plugin;
        this.nanoID = NanoID.fromString(document.getString("nanoID"));

        // Load the players from the document.
        final Document playersDocument = document.get("players", Document.class);
        for(final String player : playersDocument.keySet()) {
            players.add(new PartyPlayer(playersDocument.get(player, Document.class)));
        }
    }

    /**
     * Disbands the party.
     */
    public void disband() {
        plugin.getRedis().publish("jadedparty", "disband " + this.nanoID.toString());
        plugin.getRedis().del("jadedparty:parties:" + this.nanoID);
    }

    public NanoID getNanoID() {
        return this.nanoID;
    }

    /**
     * Get the PartyPlayer of a player, from their uuid.
     * @param playerUUID UUID of the player.
     * @return Corresponding PartyPlayer object.
     */
    public PartyPlayer getPlayer(@NotNull final UUID playerUUID) {
        for(final PartyPlayer partyPlayer : this.players) {
            if(partyPlayer.getUniqueID().equals(playerUUID)) {
                return partyPlayer;
            }
        }

        return null;
    }

    /**
     * Gets all players currently in the party.
     * @return All current players.
     */
    public Collection<PartyPlayer> getPlayers() {
        return this.players;
    }

    /**
     * Removes an invite from the party.
     * @param playerUUID UUID of the player who was invited.
     */
    public void removeInvite(@NotNull final UUID playerUUID) {
        this.invites.remove(playerUUID);
    }

    /**
     * Removes a player from the party.
     * @param player Player to remove.
     */
    public void removePlayer(final Player player) {
        for(PartyPlayer partyPlayer : players) {
            if(partyPlayer.getUniqueID().equals(player.getUniqueId())) {
                players.remove(partyPlayer);
                break;
            }
        }

        plugin.getRedis().publish("jadedparty", "leave " + this.nanoID.toString() + " " + player.getUniqueId().toString());
        update();
    }

    /**
     * Sends a message to all members of the party.
     * @param message Message to be sent.
     */
    public void sendMessage(@NotNull final String message) {
        final StringBuilder builder = new StringBuilder();
        players.forEach(partyPlayer -> {
            builder.append(partyPlayer.getUniqueID());
            builder.append(",");
        });

        final String targets = builder.substring(0, builder.length() - 1);
        plugin.getRedis().publish("jadedparty", "message " + targets + " " + message);
    }

    /**
     * Converts the cached party into a Bson Document.
     * @return Bson document of the party.
     */
    public Document toDocument() {
        final Document document = new Document();
        document.append("nanoID", this.nanoID.toString());

        final Document playersDocument = new Document();
        for(PartyPlayer player : players) {
            playersDocument.append(player.getUniqueID().toString(), player.toDocument());
        }
        document.append("players", playersDocument);

        final List<String> invites = new ArrayList<>();
        this.invites.forEach(invite -> invites.add(invite.toString()));
        document.append("invites", invites);

        return document;
    }

    /**
     * Updates the party in Redis.
     */
    public void update() {
        plugin.getRedis().set("jadedparty:parties:" + nanoID.toString(), toDocument().toJson());
        plugin.getRedis().publish("jadedparty", "update " + this.nanoID);
    }

    public boolean hasPlayer(final Player player) {
        for(final PartyPlayer partyPlayer : players) {
            if(partyPlayer.getUniqueID().equals(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }
}