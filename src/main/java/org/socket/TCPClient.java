package org.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    private int port;
    private String host;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean disclaimerShown = false;

    public TCPClient(String host, int port){
        this.host = host;
        this.port = port;
        createSocket();

    }

    public void start(){
        System.out.println("Client started. Connecting to " + host + ":" + port);
        boolean keepRunning = true;
        while (keepRunning){
            Commands command = readCommandToSend();
            if (command == Commands.EXIT){
                sendCommand(command);
                keepRunning = false;
                System.out.println("Exiting client.");
            } else {
                String response = sendCommand(command);
                System.out.println("Response: " + response);
            }
        }
        closeConnection();
        System.out.println("Client stopped.");
    }

    public void closeConnection(){
        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.in != null) {
                this.in.close();
            }
                System.out.println("Connection closed.");
        } catch (IOException e){
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    public String sendCommand(Commands command){
        try {
            this.out.println(command.getCode());
            return this.in.readLine();
        } catch (IOException e){
            System.err.println("Error sending command: " + e.getMessage());
            return "Error" + e.getMessage();
        }
    }

    public void createSocket() {
        try {
            this.socket = new Socket(this.host, this.port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error creating socket: " + e.getMessage());
        }
    }

        public Commands readCommandToSend(){
        Scanner scanner = new Scanner(System.in);
        if (!disclaimerShown) {
            System.out.println("DISCLAIMER! This system uses numbers instead of words as input." +
                    " Please input the corresponding number to each command!\n" +
                    "Enter command (type '1' for available commands): ");
            disclaimerShown = true;
        }

        try {
            int input = Integer.parseInt(scanner.nextLine().trim().toUpperCase());
            return Commands.fromIntValue(input);
        } catch (IllegalArgumentException e){
            System.out.println("Invalid command. Please try again.");
            return readCommandToSend();
        }
        }

    public String getAvailableCommands(){
        return sendCommand(Commands.HELP);
    }

}
