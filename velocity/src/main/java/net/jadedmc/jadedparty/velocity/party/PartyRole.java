package net.jadedmc.jadedparty.velocity.party;

/**
 * Represents the role of someone in a party.
 */
public enum PartyRole {
    /**
     * Represents the party leader.
     */
    LEADER,

    /**
     * Represents a party moderator, which has increased permissions.
     */
    MODERATOR,

    /**
     * Represents a regular member of a party.
     */
    MEMBER,

    /**
     * Represents a player who is not in a party.
     * Useful for checking without looping through every party.
     */
    NONE;
}