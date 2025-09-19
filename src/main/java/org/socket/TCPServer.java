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

    public TCPServer(String host, int port){
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

            String command;
            // This loop keeps reading commands until client disconnects or sends EXIT
            while ((command = in.readLine()) != null) {


                String response;

                    response = executeCommand(command);

                out.println(response);

                // Only break the loop if EXIT command received
                if ("EXIT".equalsIgnoreCase(command)) {
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


    private String executeCommand(String command) {
        switch (command.toUpperCase()) {
            case "HELP":
                return "Available commands: TURN ON, TURN OFF, STATUS, CHANGE CHANNEL, EXIT";
            case "TURN ON":
                if (!isOn) {
                    isOn = true;
                    return "TV turned ON";
                } else {
                    return "TV is already ON";
                }
            case "TURN OFF":
                if (isOn) {
                    isOn = false;
                    return "TV turned OFF";
                } else {
                    return "TV is already OFF";
                }
            case "STATUS":
                return "TV is " + (isOn ? "ON" : "OFF");

            case "CHANGE CHANNEL":
                if (isOn) {
                    return "Which channel would you like to switch to? Available channels: 1, 2, 3, 4, 5.";
                } else {
                    return "TV is OFF. Cannot change channel.";
                }





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
