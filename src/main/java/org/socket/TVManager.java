package org.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Manages a collection of TVs and their corresponding TCP client connections.
 * This class serves as a central point for managing TV devices and communicating with them.
 */
public class TVManager {

    private final Map<String, TV> servers = new HashMap<>();
    private final Map<String, TCPClient> clients = new HashMap<>();

    /**
     * Adds a TV to the manager and creates a corresponding TCP client connection.
     *
     * @param tv The TV object to add to the manager
     */
    public void addTV(TV tv) {
        this.servers.put(tv.getName(), tv);
        this.clients.put(tv.getName(), new TCPClient(tv.getHost(), tv.getPort()));
    }

    /**
     * Retrieves the list of all TV names registered in the manager.
     *
     * @return A list of TV names
     */
        public List<String> getTCPServerNames(){
            return new ArrayList<>(this.servers.keySet());
        }

    /**
     * Sends a command to a specific TV server.
     *
     * @param serverName The name of the TV server to send the command to
     * @param command The command to send
     * @return The response from the TV server or an error message if the client doesn't exist
     */
        public String sendCommandToServer(String serverName, Commands command){
            TCPClient client = this.clients.get(serverName);
            String returnValue;
            if (client != null) {
                returnValue = client.sendCommand(command);
            } else {
                returnValue = "No client found for server: " + serverName;
            }
            return returnValue;
        }
    }

