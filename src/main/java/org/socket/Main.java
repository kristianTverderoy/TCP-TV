package org.socket;

public class Main {
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

        TCPServer tv1Server = new TCPServer(host, port);
//        TCPServer tv2Server = new TCPServer(host, port);
//        TCPServer tv3Server = new TCPServer(host, port);

        Thread tv1Thread = new Thread(tv1Server::start);
//        Thread tv2Thread = new Thread(tv2Server::start);
//        Thread tv3Thread = new Thread(tv3Server::start);
        tv1Thread.start();
//        tv2Thread.start();
//        tv3Thread.start();

        TVManager tvManager = new TVManager();
        tvManager.addTV(new TV("Master bedroom", host, tv1Server.getPort()));
//        tvManager.addTV(new TV("Living room", host, tv2Server.getPort()));
//        tvManager.addTV(new TV("Kitchen", host, tv3Server.getPort()));

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
