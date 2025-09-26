package org.socket;

/**
 * Represents a TV device with network connectivity.
 * This class stores information about a TV including its name, network address,
 * power state, and current channel.
 */
public class TV {
    private String name;
    private String host;
    private int port;
    private boolean isOn = false;
    private int currentChannel = 1;

    /**
     * Constructs a new TV with the specified name and network address.
     * TVs are initialized in the off state with channel 1 selected.
     *
     * @param name The display name of the TV
     * @param host The hostname or IP address of the TV
     * @param port The port number used for connecting to the TV
     */
    public TV(String name, String host, int port){
        this.name = name;
        this.host = host;
        this.port = port;
    }

    /**
     * Gets the name of the TV.
     *
     * @return The TV's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the hostname or IP address of the TV.
     *
     * @return The TV's host address
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port number used for connecting to the TV.
     *
     * @return The TV's port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the power state of the TV.
     *
     * @return true if the TV is on, false if it's off
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * Sets the power state of the TV.
     *
     * @param on true to turn the TV on, false to turn it off
     */
    public void setOn(boolean on) {
        isOn = on;
    }

    /**
     * Gets the current channel number.
     *
     * @return The currently selected channel
     */
    public int getCurrentChannel() {
        return currentChannel;
    }

    /**
     * Sets the TV to a specific channel.
     *
     * @param channel The channel number to set
     */
    public void setCurrentChannel(int channel) {
        currentChannel = channel;
    }

    /**
     * Returns a string representation of the TV including its name and network address.
     *
     * @return A string in the format "name (host:port)"
     */
    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}
