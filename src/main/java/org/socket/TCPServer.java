package org.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private String host;
    private int port;
    private ServerSocket serverSocket;
    private boolean isOn = false;
    private int channel = 1;

    public TCPServer( String host, int port){
        this.host = host;
        this.port = port;
        this.serverSocket = createServerSocket();
        bindServerSocket();

    }


    private ServerSocket createServerSocket(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            System.out.println("Server socket created.");
        } catch (IOException e){
            System.err.println("Could not create socket: " + e.getMessage());
        }
        return serverSocket;
    }

    public void bindServerSocket(){
        try{
            if (this.serverSocket == null || this.serverSocket.isClosed()) {
                this.serverSocket = createServerSocket();
            }
            this.serverSocket.bind((new InetSocketAddress(this.host, this.port)));
            System.out.println("Server bound to " + this.host + ":" + this.port);
        } catch (Exception e){
            System.err.println("Could not bind socket: " + e.getMessage());
        }

    }

    public void start() {
        if (this.serverSocket == null || !(this.serverSocket.isBound())) {
            System.err.println("Server socket is not bound or is closed.");
            return;
        }
        System.out.println("Smart TV server started. TV is " + (isOn ? "ON" : "OFF"));
        boolean keepConnectionActive = true;
        while (keepConnectionActive) {
            try {
                System.out.println("Waiting for commands...");
                Socket clientSocket = this.serverSocket.accept();
                processClientRequest(clientSocket);
                if (clientSocket.isClosed()) {
                    keepConnectionActive = false;
                    System.out.println("Client disconnected. Stopping server.");
                }
            } catch (IOException e) {
                System.err.println("Error accepting connection: " + e.getMessage());
            }
        }
    }

    private void processClientRequest(Socket clientSocket) {
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

    private String executeCommand(Commands command) {
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
                    return "TV turned ON";
                } else {
                    return "TV is already ON";
                }
            case TURN_OFF:
                if (isOn) {
                    isOn = false;
                    return "TV turned OFF";
                } else {
                    return "TV is already OFF";
                }
            case TURN_ON_OR_OFF:
                isOn = !isOn;
                return "TV turned " + (isOn ? "ON" : "OFF");

            case STATUS:
                return "TV is " + (isOn ? "ON" : "OFF");

                case GET_CHANNEL:
                if (isOn) {
                    return "Current channel is " + this.channel;
                } else {
                    return "TV is OFF. No active channel.";
                }

            case CHANNEL_UP:
                if (isOn) {
                    this.channel = (this.channel % 5) + 1; // Wrap around after channel 5
                    return "Channel increased to " + this.channel;
                } else {
                    return "TV is OFF. Cannot change channel.";
                }

            case CHANNEL_DOWN:
                if (isOn) {
                    this.channel = (this.channel == 1) ? 5 : this.channel - 1; // Wrap around to channel 5 if at channel 1
                    return "Channel decreased to " + this.channel;
                } else {
                    return "TV is OFF. Cannot change channel.";
                }

            case CHANNEL_1:
                if (isOn) {
                    this.channel = 1;
                    return "Switched to Channel 1: NRK";
                } else {
                    return "TV is OFF. Cannot change channel.";
                }

            case CHANNEL_2:
                if (isOn) {
                    this.channel = 2;
                    return "Switched to Channel 2: National Geographic";
                } else {
                    return "TV is OFF. Cannot change channel.";
                }
            case CHANNEL_3:
                if (isOn) {
                    this.channel = 3;
                    return "Switched to Channel 3: Discovery Channel";
                } else {
                    return "TV is OFF. Cannot change channel.";
                }
            case CHANNEL_4:
                if (isOn) {
                    this.channel = 4;
                    return "Switched to Channel 4: HBO";
                } else {
                    return "TV is OFF. Cannot change channel.";
                }
            case CHANNEL_5:
                if (isOn) {
                    this.channel = 5;
                    return "Switched to Channel 5: TV2";
                } else {
                    return "TV is OFF. Cannot change channel.";
                }

            case EXIT:
                return "Exiting. Goodbye!";


            default:
                return "Unknown command. Try 'HELP'";
        }
    }

    public boolean isOn() {
        return this.isOn;
    }

    public int getPort(){
        int port = this.port;
        if (serverSocket != null && serverSocket.isBound()){
            port = serverSocket.getLocalPort();
        }
        return port;
    }





}
