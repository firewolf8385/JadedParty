package net.jadedmc.jadedparty.velocity.party;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyPlayer {
    private final UUID uuid;
    private final String username;
    private PartyRole role;
    private String prefix;

    public PartyPlayer(@NotNull final Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.username = document.getString("username");
        this.role = PartyRole.valueOf(document.getString("role"));
        this.prefix = document.getString("prefix");
    }

    public PartyRole getRole() {
        return role;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public String getName() {
        return this.username;
    }

    public void setRole(final PartyRole role) {
        this.role = role;
    }

    public Document toDocument() {
        return new Document()
                .append("uuid", this.uuid.toString())
                .append("username", this.username)
                .append("role", role.toString());
    }

    public String getPrefix() {
        return prefix;
    }
}