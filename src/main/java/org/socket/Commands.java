package org.socket;

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

    Commands(int intValue) {
        this.code = intValue;
    }

    public static int getCommandCode(String fieldName){
            return Commands.valueOf(fieldName).getCode();

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
