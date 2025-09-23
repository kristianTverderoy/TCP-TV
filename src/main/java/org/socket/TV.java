package org.socket;

public class TV {
    private String name;
    private String host;
    private int port;
    private boolean isOn = false;
    private int currentChannel = 1;

    public TV(String name, String host, int port){
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() { return name; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public boolean isOn() { return isOn; }
    public void setOn(boolean on) { isOn = on; }
    public int getCurrentChannel() { return currentChannel; }
    public void setCurrentChannel(int channel) { currentChannel = channel; }

    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}
