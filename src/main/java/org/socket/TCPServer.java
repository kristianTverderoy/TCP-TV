package org.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A TCP server that simulates a Smart TV device.
 * This server listens for client connections and processes commands to control
 * the TV's power state and channel selection.
 */
public class TCPServer {

    private String host;
    private int port;
    private ServerSocket serverSocket;
    private boolean isOn = false;
    private int channel = 1;
    private ExecutorService threadPool;
    private boolean running = true;
    private final List<ClientHandler> connectedClients = Collections.synchronizedList(new ArrayList<>());
    private ServerSocket broadcastServerSocket;
    private final List<PrintWriter> broadcastClients = Collections.synchronizedList(new ArrayList<>());
    private int broadcastPort;

    /**
     * Constructs a new TCP server with the specified host and port.
     * Creates and binds a server socket during initialization.
     *
     * @param host The hostname or IP address to bind to
     * @param port The port number to bind to
     */
    public TCPServer(String host, int port, int threadPoolSize) {
        this.host = host;
        this.port = port;
        this.broadcastPort = port + 10000;
        this.serverSocket = createServerSocket();
        this.broadcastServerSocket = createBroadcastServerSocket();

        bindServerSocket();
        bindBroadcastServerSocket();
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    private ServerSocket createBroadcastServerSocket(){
        ServerSocket socket = null;
        try {
            socket = new ServerSocket();
            System.out.println("Broadcast server socket created");
        } catch (IOException e) {
            System.err.println("Could not create broadcast socket: " + e.getMessage());
        }
        return socket;
    }

    private void bindBroadcastServerSocket() {
        try {
            if (this.broadcastServerSocket == null || this.broadcastServerSocket.isClosed()) {
                this.broadcastServerSocket = createBroadcastServerSocket();
            }
            this.broadcastServerSocket.bind(new InetSocketAddress(this.host, this.broadcastPort));
        } catch (Exception e){
            System.err.println("Could not bind broadcast socket: " + e.getMessage());
        }
    }
    /**
     * Creates a new server socket.
     *
     * @return A new ServerSocket instance, or null if creation fails
     */
    private ServerSocket createServerSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            System.out.println("Server socket created.");
        } catch (IOException e) {
            System.err.println("Could not create socket: " + e.getMessage());
        }
        return serverSocket;
    }

    public void registerClient(ClientHandler client) {
        connectedClients.add(client);
    }

    public void unregisterClient(ClientHandler client){
        connectedClients.remove(client);
    }

    public void broadcastStateChange(String message) {
        synchronized (broadcastClients) {
            List<PrintWriter> deadClients = new ArrayList<>();
            for (PrintWriter writer : broadcastClients){
                try {
                    writer.println(message);
                    if (writer.checkError()) {
                        deadClients.add(writer);
                    }
                } catch (Exception e) {
                    deadClients.add(writer);
                }
            }
            broadcastClients.removeAll(deadClients);
        }
    }



    /**
     * Binds the server socket to the configured host and port.
     * If the server socket is null or closed, a new one is created first.
     */
    public void bindServerSocket() {
        try {
            if (this.serverSocket == null || this.serverSocket.isClosed()) {
                this.serverSocket = createServerSocket();
            }
            this.serverSocket.bind((new InetSocketAddress(this.host, this.port)));
            System.out.println("Server bound to " + this.host + ":" + this.port);
        } catch (Exception e) {
            System.err.println("Could not bind socket: " + e.getMessage());
        }

    }

    /**
     * Starts the server and begins listening for client connections.
     * Processes client commands until an EXIT command is received or an error occurs.
     */
    public void start() {
        if (this.serverSocket == null || !(this.serverSocket.isBound())) {
            System.err.println("Server socket is not bound or is closed.");
            return;
        }
        System.out.println("Smart TV server started. TV is " + (isOn ? "ON" : "OFF"));

        Thread broadcastThread = new Thread(this::acceptBroadcastConnections);
        broadcastThread.setDaemon(true);
        broadcastThread.start();

        try {
            while (running) {

                System.out.println("Waiting for client connections...");
                Socket clientSocket = this.serverSocket.accept();
                System.out.println("New client connected from " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, this));

            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error accepting connection: " + e.getMessage());
            }
        } finally {
            shutdown();
        }
    }

    private void acceptBroadcastConnections() {
        try {
            while (running) {
                Socket clientSocket = broadcastServerSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                synchronized (broadcastClients) {
                    broadcastClients.add(writer);
                }
            }
        } catch (IOException e){
            if (running) {
                System.err.println("Error accepting broadcast connection: " + e.getMessage());
            }
        }
    }



public void shutdown() {
    running = false;
    try {
        if (broadcastServerSocket != null && !broadcastServerSocket.isClosed()) {
            broadcastServerSocket.close();
            System.out.println("Server socket closed");
        }
    } catch (IOException e) {
        System.err.println("Error closing server socket: " + e.getMessage());
    }

    if (threadPool != null) {
        threadPool.shutdown();
    }

    try {
        if (serverSocket != null && !serverSocket.isClosed()){
            serverSocket.close();
        }
    } catch (IOException e) {
        System.err.println("Error closing server socket: " + e.getMessage());
    }
}

/**
 * Processes incoming client requests by reading commands and sending responses.
 * Executes the received commands and returns appropriate responses.
 *
 * @param clientSocket The socket connection to the client
 */
public void processClientRequest(Socket clientSocket) {
    try {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String inputLine;
        // This loop keeps reading commands until client disconnects or sends EXIT
        while ((inputLine = in.readLine()) != null) {

            int commandCode = Integer.parseInt(inputLine.trim());
            Commands command = Commands.fromIntValue(commandCode);
            String response = executeCommand(command);

            out.println(response);

            // Only break the loop if EXIT command received
            if (command == Commands.EXIT) {
                break;
            }
        }
    } catch (IOException e) {
        System.err.println("Error handling client connection: " + e.getMessage());
    } finally {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}

/**
 * Executes a TV command and returns the appropriate response message.
 *
 * @param command The command to execute
 * @return A string response describing the result of the command
 */
public String executeCommand(Commands command) {
    String response;
    switch (command) {
        case HELP:
            return "Available commands: 'HELP(" + Commands.getCommandCode(String.valueOf(Commands.HELP)) + ")', " +
                    "'TURN ON(" + Commands.getCommandCode(String.valueOf(Commands.TURN_ON)) + ")', " +
                    "'TURN OFF(" + Commands.getCommandCode(String.valueOf(Commands.TURN_OFF)) + ")', " +
                    "'STATUS(" + Commands.getCommandCode(String.valueOf(Commands.STATUS)) + ")', " +
                    "'GET ACTIVE CHANNEL(" + Commands.getCommandCode(String.valueOf(Commands.GET_CHANNEL)) + ")', " +
                    "'CHANNEL_1(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_1)) + ")', " +
                    "'CHANNEL_2(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_2)) + ")', " +
                    "'CHANNEL_3(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_3)) + ")', " +
                    "'CHANNEL_4(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_4)) + ")', " +
                    "'CHANNEL_5(" + Commands.getCommandCode(String.valueOf(Commands.CHANNEL_5)) + ")', " +
                    "'EXIT(" + Commands.getCommandCode(String.valueOf(Commands.EXIT)) + ")'";
        case TURN_ON:
            if (!isOn) {
                isOn = true;
                response = "TV turned ON";
                broadcastStateChange("TV_STATE_CHANGE: ON, Channel: " + this.channel);
                return response;
            } else {
                response = "TV is already ON";
            }
            return response;

        case TURN_OFF:
            if (isOn) {
                isOn = false;
                response = "TV turned OFF";
                broadcastStateChange("TV_STATE_CHANGE: OFF");
            } else {
                response = "TV is already OFF";
            }
            return response;

        case TURN_ON_OR_OFF:
            isOn = !isOn;
            response = "TV turned " + (isOn ? "ON" : "OFF");
            broadcastStateChange("TV_STATE_CHANGE: " + (isOn ? "ON" : "OFF") +
                    ", Channel: " + this.channel);
            return response;

        case STATUS:
            return "TV is " + (isOn ? "ON" : "OFF");

        case CHANNEL_UP:
            if (isOn) {
                this.channel = (this.channel % 5) + 1;
                response = "Channel increased to " + this.channel; //Wrap around after channel 5
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;

        case CHANNEL_DOWN:
            if (isOn) {
                this.channel = (this.channel == 1) ? 5 : this.channel - 1; // Wrap around to channel 5 if at channel 1
                response = "Channel decreased to " + this.channel;
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;

        case CHANNEL_1:
            if (isOn) {
                this.channel = 1;
                response = "Switched to Channel 1: NRK";
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;

        case CHANNEL_2:
            if (isOn) {
                this.channel = 2;
                response = "Switched to Channel 2: National Geographic";
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;


        case CHANNEL_3:
            if (isOn) {
                this.channel = 3;
                response = "Switched to Channel 3: Discovery Channel";
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;

        case CHANNEL_4:
            if (isOn) {
                this.channel = 4;
                response = "Switched to Channel 4: HBO";
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;

        case CHANNEL_5:
            if (isOn) {
                this.channel = 5;
                response = "Switched to Channel 5: TV2";
                broadcastStateChange("CHANNEL_CHANGE: " + this.channel);
                return response;
            } else {
                response = "TV is OFF. Cannot change channel.";
            }
            return response;

        case EXIT:
            return "Exiting. Goodbye!";


        default:
            return "Unknown command. Try 'HELP'";
    }
}

/**
 * Gets the power state of the TV.
 *
 * @return true if the TV is on, false if it's off
 */
public boolean isOn() {
    return this.isOn;
}

public int getBroadcastPort(){
    return broadcastServerSocket != null && broadcastServerSocket.isBound() ?
            broadcastServerSocket.getLocalPort() : broadcastPort;
}

/**
 * Gets the port number the server is bound to.
 * If the server socket is bound, returns its actual local port,
 * otherwise returns the configured port.
 *
 * @return The port number
 */
public int getPort() {
    int port = this.port;
    if (serverSocket != null && serverSocket.isBound()) {
        port = serverSocket.getLocalPort();
    }
    return port;
}





}
