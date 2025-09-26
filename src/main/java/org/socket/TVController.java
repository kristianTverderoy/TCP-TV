package org.socket;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * A controller class for TV management that provides a command-line interface
 * for interacting with available TVs through a TVManager.
 * This class handles TV selection and command execution.
 */
public class TVController {
    private TVManager tvManager;
    private Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a new TVController with the specified TVManager.
     *
     * @param tvManager The TV manager to use for TV operations
     */
    public TVController(TVManager tvManager){
        this.tvManager = tvManager;
    }

    /**
     * Starts the interactive TV control session.
     * Runs in a loop allowing users to select TVs and issue commands until EXIT is selected.
     */
    public void start(){
        boolean running = true;
        while (running) {
            String tvName = selectTv();
            if (tvName == null) {
                running = false;
            }

            Commands command = readCommand();
            String response = this.tvManager.sendCommandToServer(tvName, command);
            System.out.println("Response: " + response);

            if (command == Commands.EXIT) {
                running = false;
            }
        }
    }

    /**
     * Displays available TVs and prompts the user to select one.
     *
     * @return The selected TV name, or null if no TV was selected or if input was invalid
     */
    private String selectTv(){
        List<String> tvNames = tvManager.getTCPServerNames();
        if (tvNames.isEmpty()) {
            System.out.println("No TVs available.");
        } else {
            System.out.println("Available TVs:");
        }
        for (int i = 0; i < tvNames.size(); i++) {
            System.out.println((i + 1) + ". " + tvNames.get(i));
        }
        System.out.println("Select a TV by number (or type '0' to quit)");
        try{
        int selection = Integer.parseInt(scanner.nextLine());
        String returnValue = null;
        if (selection > 0 && selection <= tvNames.size()) {
            returnValue = tvNames.get(selection - 1);
        }
        return returnValue;
    } catch (InputMismatchException e){
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
    }

    /**
     * Displays available commands and prompts the user to select one.
     * Handles numeric input for command selection.
     *
     * @return The selected command, defaults to HELP if input is invalid
     */
    Commands readCommand(){
        System.out.println("Available commands:\n'HELP(" + Commands.getCommandCode(String.valueOf(Commands.HELP)) + ")', " +
                "'TURN ON(" + Commands.getCommandCode(String.valueOf(Commands.TURN_ON)) + ")', " +
                "'TURN OFF(" + Commands.getCommandCode(String.valueOf(Commands.TURN_OFF)) + ")', " +
                "'TURN ON/OFF(" + Commands.getCommandCode(String.valueOf(Commands.TURN_ON_OR_OFF)) + ")', " +
                "\n'STATUS(" + Commands.getCommandCode(String.valueOf(Commands.STATUS)) + ")', " +
                "'CHANNEL UP(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_UP)) + ")', " +
                "'CHANNEL DOWN(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_DOWN)) + ")', " +
                "'GET ACTIVE CHANNEL(" + Commands.getCommandCode(String.valueOf(Commands.GET_CHANNEL)) + ")', " +
                "\n'CHANNEL_1(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_1)) + ")', " +
                "'CHANNEL_2(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_2)) + ")', " +
                "'CHANNEL_3(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_3)) + ")', " +
                "'CHANNEL_4(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_4)) + ")', " +
                "\n'CHANNEL_5(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_5)) + ")', " +
                "'EXIT(" + Commands.getCommandCode(String.valueOf(Commands.EXIT)) + ")'");
        try {
            int input = Integer.parseInt(this.scanner.nextLine().trim());
            return Commands.fromIntValue(input);
        } catch (Exception e){
            System.out.println("Invalid command. Available commands:");
            return Commands.HELP;
        }
    }

}
