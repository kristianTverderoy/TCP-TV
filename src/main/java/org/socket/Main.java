package org.socket;

/**
 * Main entry point for the TV control application.
 * This class initializes the TCP servers for TVs, creates the TV manager,
 * and starts the interactive TV controller.
 */
public class Main {
    /**
     * Application entry point. Sets up and starts TCP servers for TVs,
     * initializes TV management, and launches the interactive controller.
     *
     * @param args Command line arguments:
     *             args[0] - Host address (default: 127.0.0.1)
     *             args[1] - Port number (default: 1238)
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";
        if (args.length >= 1) {
            try {
                host = String.valueOf(args[0]);
            } catch (Exception e) {
                System.err.println("Invalid host argument, using default: " + host);
            }
        }
        int port = 1238;
        if (args.length >= 2) {
            try{
                port = Integer.parseInt(args[1]);
            } catch (Exception e){
                System.err.println("Invalid port argument, using default: " + port);
            }
        }
        System.out.println(host + ":" + port);
        System.out.println("Port 1: " + port);
        System.out.println("Port 2: " + (port + 1));
        System.out.println("Port 3: " + (port + 2));


        TCPServer tvServerPort2005 = new TCPServer(host, (port), 6);
        TCPServer tvServerPort3001 = new TCPServer(host, (port + 1), 4);
        TCPServer tvServerPort5060 = new TCPServer(host, (port + 2), 2);

        Thread tvServerPort2005Thread = new Thread(tvServerPort2005::start);
        Thread tvServerPort3001Thread = new Thread(tvServerPort3001::start);
        Thread tvServerPort5060Thread = new Thread(tvServerPort5060::start);

        tvServerPort2005Thread.start();
        tvServerPort3001Thread.start();
        tvServerPort5060Thread.start();

        TVManager tvManager = new TVManager();
        tvManager.addTV(new TV("Master bedroom", host, tvServerPort2005.getPort()));
        tvManager.addTV(new TV("Living room", host, tvServerPort3001.getPort()));
        tvManager.addTV(new TV("Kitchen", host, tvServerPort5060.getPort()));

        TVController controller = new TVController(tvManager);
        controller.start();




//        TCPServer tcpServer = new TCPServer(host, port);
//        Thread serverThread = new Thread(tcpServer::start);
//        serverThread.start();
//        int serverPort = tcpServer.getPort();
//
//
//        TCPClient tcpClient = new TCPClient(host,serverPort);
//        Thread clientThread = new Thread(tcpClient::start);
//        clientThread.start();


    }
}
