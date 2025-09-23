package org.socket;

public class Main {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 0;

        TCPServer tv1Server = new TCPServer(host, port);
        TCPServer tv2Server = new TCPServer(host, port);
        TCPServer tv3Server = new TCPServer(host, port);

        Thread tv1Thread = new Thread(tv1Server::start);
        Thread tv2Thread = new Thread(tv2Server::start);
        Thread tv3Thread = new Thread(tv3Server::start);
        tv1Thread.start();
        tv2Thread.start();
        tv3Thread.start();

        TVManager tvManager = new TVManager();
        tvManager.addTV(new TV("Master bedroom", host, tv1Server.getPort()));
        tvManager.addTV(new TV("Living room", host, tv2Server.getPort()));
        tvManager.addTV(new TV("Kitchen", host, tv3Server.getPort()));

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
