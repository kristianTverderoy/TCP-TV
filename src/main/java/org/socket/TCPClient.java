package org.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A TCP client implementation for socket-based communication with a server.
 * This class handles creating connections, sending commands, and receiving responses.
 */
public class TCPClient {

    private int port;
    private String host;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean disclaimerShown = false;

    /**
     * Constructs a new TCP client with the specified host and port.
     * Automatically creates a socket connection upon instantiation.
     *
     * @param host The hostname or IP address of the server
     * @param port The port number of the server
     */
    public TCPClient(String host, int port){
        this.host = host;
        this.port = port;
        createSocket();
    }

    /**
     * Starts the client operation, reading and sending commands in a loop
     * until the EXIT command is issued.
     */
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

    /**
     * Closes all open connections and resources.
     */
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

    /**
     * Sends a command to the server and returns the response.
     *
     * @param command The command to send
     * @return The server's response string
     */
    public String sendCommand(Commands command){
        try {
            this.out.println(command.getCode());
            return this.in.readLine();
        } catch (IOException e){
            System.err.println("Error sending command: " + e.getMessage());
            return "Error" + e.getMessage();
        }
    }

    /**
     * Creates a socket connection to the server using the specified host and port.
     * Initializes input and output streams for communication.
     */
    public void createSocket() {
        try {
            this.socket = new Socket(this.host, this.port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error creating socket: " + e.getMessage());
        }
    }

    /**
     * Reads user input to determine which command to send to the server.
     * Displays a disclaimer about using numeric inputs on first use.
     *
     * @return The selected command
     */
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

    /**
     * Retrieves the list of available commands from the server.
     *
     * @return A string containing available commands
     */
    public String getAvailableCommands(){
        return sendCommand(Commands.HELP);
    }

}
