package net.jadedmc.jadedparty.bukkit.utils.chat;

import net.jadedmc.jadedparty.bukkit.utils.JadedUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some methods to make sending chat messages easier.
 */
public class ChatUtils {
    private final static int CENTER_PX = 154;

    /**
     * Broadcast a MiniMessage message to all online players.
     * @param message Message to broadcast.
     */
    public static void broadcast(String message) {
        JadedUtils.getAdventure().players().sendMessage(translate(message));
    }

    /**
     * Broadcast a MiniMessage message to all online players in a given world.
     * @param world World to broadcast message in.
     * @param message Message to broadcast.
     */
    public static void broadcast(World world, String message) {
        for(Player player : world.getPlayers()) {
            JadedUtils.getAdventure().player(player).sendMessage(translate(message));
        }
    }

    /**
     * Attempts to center a message in chat.
     * @param message Message to center.
     * @return Centered message.
     */
    public static String centerText(String message) {

        if(message.equals("")) {
            return message;
        }

        String translated = ChatColor.translateAlternateColorCodes('&', MiniMessage.miniMessage().stripTags(toLegacy(message)));

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : translated.toCharArray()) {
            if(c == '§') {
                previousCode = true;
            }
            else if(previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }
            else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }

    /**
     * Send a MiniMessage message to a given CommandSender.
     * @param commandSender CommandSender to send message to.
     * @param message Message to send.
     */
    public static void chat(CommandSender commandSender, String message) {
        JadedUtils.getAdventure().sender(commandSender).sendMessage(translate(message));
    }

    /**
     * Translates a String to a colorful String using methods in the BungeeCord API.
     * @param message Message to translate.
     * @return Translated Message.
     */
    public static Component translate(String message) {
        // Checks for the "<center>" tag, which centers a message.
        if(message.startsWith("<center>")) {
            message = centerText(message.replaceFirst("<center>", ""));
        }

        return MiniMessage.miniMessage().deserialize(replaceLegacy(message));
    }

    /**
     * Translates a legacy message using ChatColor instead of components.
     * @param message Message to translate.
     * @return Translated message.
     */
    public static String translateLegacy(String message) {
        return ChatColor.translateAlternateColorCodes('&', toLegacy(message));
    }

    /**
     * Replaces the legacy color codes used in a message with their MiniMessage counterparts.
     * @param message Message to replace color codes in.
     * @return Message with the color codes replaced.
     */
    public static String replaceLegacy(String message) {
        // Get the server version.
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int subVersion = Integer.parseInt(version.replace("1_", "").replaceAll("_R\\d", "").replace("v", ""));

        // If the version is 1.16 or greater, check for hex color codes.
        if(subVersion >= 16) {
            Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start() + 1, matcher.end());
                message = message.replace("&" + color, "<reset><color:" + color + ">");
                matcher = pattern.matcher(message);
            }
        }

        // Then replace legacy color codes.
        return message.replace("§", "&")
                .replace("&0", "<reset><black>")
                .replace("&1", "<reset><dark_blue>")
                .replace("&2", "<reset><dark_green>")
                .replace("&3", "<reset><dark_aqua>")
                .replace("&4", "<reset><dark_red>")
                .replace("&5", "<reset><dark_purple>")
                .replace("&6", "<reset><gold>")
                .replace("&7", "<reset><gray>")
                .replace("&8", "<reset><dark_gray>")
                .replace("&9", "<reset><blue>")
                .replace("&a", "<reset><green>")
                .replace("&b", "<reset><aqua>")
                .replace("&c", "<reset><red>")
                .replace("&d", "<reset><light_purple>")
                .replace("&e", "<reset><yellow>")
                .replace("&f", "<reset><white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<u>")
                .replace("&o", "<i>")
                .replace("&r", "<reset>");
    }

    /**
     * Convert a component to its legacy form.
     * Used because some important plugins don't play nice with MiniMessage.
     * @param component Component to turn into a legacy string.
     * @return Resulting legacy string.
     */
    public static String toLegacy(Component component) {
        return MiniMessage.miniMessage().serialize(component).replace("<black>", "§0")
                .replace("<dark_blue>", "&1")
                .replace("<dark_green>", "&2")
                .replace("<dark_aqua>", "&3")
                .replace("<dark_red>", "&4")
                .replace("<dark_purple>", "&5")
                .replace("<gold>", "&6")
                .replace("<gray>", "&7")
                .replace("<dark_gray>", "&8")
                .replace("<blue>", "&9")
                .replace("<green>", "&a")
                .replace("<aqua>", "&b")
                .replace("<red>", "&c")
                .replace("<light_purple>", "&d")
                .replace("<yellow>", "&e")
                .replace("<white>", "&f")
                .replace("<obfuscated>", "&k")
                .replace("<obf>", "&k")
                .replace("<bold>", "&l")
                .replace("<b>", "&l")
                .replace("<strikethrough>", "&m")
                .replace("<st>", "&m")
                .replace("<underline>", "&n")
                .replace("<u>", "&n")
                .replace("<i>", "&o")
                .replace("<italic>", "&o")
                .replace("<reset>", "&r")
                .replace("</black>", "")
                .replace("</dark_blue>", "")
                .replace("</dark_green>", "")
                .replace("</dark_aqua>", "")
                .replace("</dark_red>", "")
                .replace("</dark_purple>", "")
                .replace("</gold>", "")
                .replace("</gray>", "")
                .replace("</dark_gray>", "")
                .replace("</blue>", "")
                .replace("</green>", "")
                .replace("</aqua>", "")
                .replace("</red>", "")
                .replace("</light_purple>", "")
                .replace("</yellow>", "")
                .replace("</white>", "")
                .replace("</obfuscated>", "")
                .replace("</obf>", "")
                .replace("</bold>", "")
                .replace("</b>", "")
                .replace("</strikethrough>", "")
                .replace("</st>", "")
                .replace("</underline>", "")
                .replace("</u>", "")
                .replace("</i>", "")
                .replace("</italic>", "");
    }

    /**
     * Convert a MiniMessage string to its legacy form.
     * Used because some important plugins don't play nice with MiniMessage.
     * @param message MiniMessage String to turn into a legacy String.
     * @return Resulting legacy string.
     */
    public static String toLegacy(String message) {
        return message.replace("<black>", "§0")
                .replace("<dark_blue>", "&1")
                .replace("<dark_green>", "&2")
                .replace("<dark_aqua>", "&3")
                .replace("<dark_red>", "&4")
                .replace("<dark_purple>", "&5")
                .replace("<gold>", "&6")
                .replace("<gray>", "&7")
                .replace("<dark_gray>", "&8")
                .replace("<blue>", "&9")
                .replace("<green>", "&a")
                .replace("<aqua>", "&b")
                .replace("<red>", "&c")
                .replace("<light_purple>", "&d")
                .replace("<yellow>", "&e")
                .replace("<white>", "&f")
                .replace("<obfuscated>", "&k")
                .replace("<obf>", "&k")
                .replace("<bold>", "&l")
                .replace("<b>", "&l")
                .replace("<strikethrough>", "&m")
                .replace("<st>", "&m")
                .replace("<underline>", "&n")
                .replace("<u>", "&n")
                .replace("<i>", "&o")
                .replace("<italic>", "&o")
                .replace("<reset>", "&r")
                .replace("</black>", "")
                .replace("</dark_blue>", "")
                .replace("</dark_green>", "")
                .replace("</dark_aqua>", "")
                .replace("</dark_red>", "")
                .replace("</dark_purple>", "")
                .replace("</gold>", "")
                .replace("</gray>", "")
                .replace("</dark_gray>", "")
                .replace("</blue>", "")
                .replace("</green>", "")
                .replace("</aqua>", "")
                .replace("</red>", "")
                .replace("</light_purple>", "")
                .replace("</yellow>", "")
                .replace("</white>", "")
                .replace("</obfuscated>", "")
                .replace("</obf>", "")
                .replace("</bold>", "")
                .replace("</b>", "")
                .replace("</strikethrough>", "")
                .replace("</st>", "")
                .replace("</underline>", "")
                .replace("</u>", "")
                .replace("</i>", "")
                .replace("</italic>", "");
    }
}