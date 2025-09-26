package org.socket;

/**
 * Represents commands that can be sent to TV devices.
 * Each command has an associated numeric code for transmission over the network.
 */
public enum Commands {
    HELP(1),
    TURN_ON(2),
    TURN_OFF(3),
    TURN_ON_OR_OFF(4),
    STATUS(5),
    CHANNEL_UP(6),
    CHANNEL_DOWN(7),
    GET_CHANNEL(8),
    CHANNEL_1(9),
    CHANNEL_2(10),
    CHANNEL_3(11),
    CHANNEL_4(12),
    CHANNEL_5(13),
    EXIT(0);

    private final int code;

    /**
     * Constructs a command with the specified numeric code.
     *
     * @param intValue The numeric code for this command
     */    Commands(int intValue) {
        this.code = intValue;
    }

    /**
     * Gets the numeric code for a command by its name.
     *
     * @param fieldName The name of the command
     * @return The numeric code associated with the command
     * @throws IllegalArgumentException If the fieldName does not match any command
     */
    public static int getCommandCode(String fieldName){
            return Commands.valueOf(fieldName).getCode();

    }

    /**
     * Gets the numeric code for this command.
     *
     * @return The command's numeric code
     */
    public int getCode() {
        return code;
    }

    /**
     * Finds a command by its numeric code.
     *
     * @param intValue The numeric code to look up
     * @return The command associated with the numeric code
     * @throws IllegalArgumentException If no command matches the provided code
     */
    public static Commands fromIntValue(int intValue) {
        for (Commands command : Commands.values()) {
            if (command.getCode() == intValue) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid command code: " + intValue);
    }
}
