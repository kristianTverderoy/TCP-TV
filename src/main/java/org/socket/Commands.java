package org.socket;

public enum Commands {
    HELP(1),
    TURN_ON(2),
    TURN_OFF(3),
    STATUS(4),
    GET_CHANNEL(5),
    CHANNEL_1(6),
    CHANNEL_2(7),
    CHANNEL_3(8),
    CHANNEL_4(9),
    CHANNEL_5(10),
    EXIT(0);

    private final int code;

    Commands(int intValue) {
        this.code = intValue;
    }

    public int getCode() {
        return code;
    }

    public static Commands fromIntValue(int intValue) {
        for (Commands command : Commands.values()) {
            if (command.getCode() == intValue) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid command code: " + intValue);
    }
}
