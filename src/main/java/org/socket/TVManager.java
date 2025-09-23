package org.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TVManager {

    private final Map<String, TV> servers = new HashMap<>();
    private final Map<String, TCPClient> clients = new HashMap<>();

    public void addTV(TV tv) {
        this.servers.put(tv.getName(), tv);
        this.clients.put(tv.getName(), new TCPClient(tv.getHost(), tv.getPort()));
    }

        public List<String> getTCPServerNames(){
            return new ArrayList<>(this.servers.keySet());
        }

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

