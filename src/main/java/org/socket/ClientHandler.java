package org.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private TCPServer server;

    public ClientHandler(Socket clientSocket, TCPServer server){
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.err.println("Error sending message to client " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            server.registerClient(this);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while((inputLine = in.readLine()) != null) {
                int commandCode = Integer.parseInt(inputLine.trim());
                Commands command = Commands.fromIntValue(commandCode);
                String response = server.executeCommand(command);

                out.println(response);

                if (command == Commands.EXIT){
                    break;
                }

            }
        } catch (IOException e){
            System.err.println("Error handling client connection" + e.getMessage());
        } finally {
            server.unregisterClient(this);
            try {
                clientSocket.close();
            } catch (IOException e){
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
