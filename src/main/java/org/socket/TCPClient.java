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
    private Socket broadcastSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean disclaimerShown = false;
    private volatile boolean running = true;

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

    public void startBroadcastListener() {
        running = true;
        //Separate thread to listen for changes made by other clients.
        try {
            this.broadcastSocket = new Socket(this.host, (this.port + 10000));
            Thread listenerThread = new Thread(this::listenForBroadcasts);
            listenerThread.setDaemon(true);
            listenerThread.start();
        } catch (IOException e) {
            System.err.println("Error connecting to broadcast socket: " + e.getMessage());
        }
    }


    private void listenForBroadcasts() {
        try {
            BufferedReader broadcastIn = new BufferedReader(
                    new InputStreamReader(broadcastSocket.getInputStream()));

            while (running) {
                try {
                    String broadcast = broadcastIn.readLine();
                    if (broadcast != null) {
                        System.out.println("\n" + "=".repeat(50));
                        System.out.println("*** BROADCAST: " + broadcast + " ***");
                        System.out.println("=".repeat(50));
                        System.out.print("\nEnter command: ");
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error receiving broadcast: " + e.getMessage());
                        try {
                            Thread.sleep(1000); // Avoid tight loop on error
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error in broadcast listener: " + e.getMessage());
            }
        }
    }

    /**
     * Closes all open connections and resources.
     */
    public void closeConnection() {
        running = false;
        try {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
            if (this.broadcastSocket != null && !this.broadcastSocket.isClosed()) {
                this.broadcastSocket.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.in != null) {
                this.in.close();
            }
            System.out.println("Connections closed.");
        } catch (IOException e) {
            System.err.println("Error closing connections: " + e.getMessage());
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
