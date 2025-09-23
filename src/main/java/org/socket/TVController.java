package org.socket;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class TVController {
    private TVManager tvManager;
    private Scanner scanner = new Scanner(System.in);

    public TVController(TVManager tvManager){
        this.tvManager = tvManager;
    }

    public void start(){
        boolean running = true;
        while (running) {
            String tvName = selectTv();
            if (tvName == null) {
                running = false;
                continue;
            }

            Commands command = readCommand();
            String response = this.tvManager.sendCommandToServer(tvName, command);
            System.out.println("Response: " + response);

            if (command == Commands.EXIT) {
                running = false;
            }
        }
    }

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

    Commands readCommand(){
        System.out.println("Available commands: 'HELP(1)', 'TURN ON(2)', 'TURN OFF(3)', 'STATUS(4)', 'GET ACTIVE CHANNEL(5)'" +
        ", 'CHANNEL_1(6)', 'CHANNEL_2(7)', 'CHANNEL_3(8)' 'CHANNEL_4(9)' 'CHANNEL_5(10)' 'EXIT(0)'");
        try {
            int input = Integer.parseInt(this.scanner.nextLine().trim());
            return Commands.fromIntValue(input);
        } catch (Exception e){
            System.out.println("Invalid command. Available commands:");
            return Commands.HELP;
        }
    }

}
