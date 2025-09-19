package org.socket;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 0;

        TCPServer tcpServer = new TCPServer(host, port);
        Thread serverThread = new Thread(tcpServer::start);
        serverThread.start();
        int serverPort = tcpServer.getPort();


        TCPClient tcpClient = new TCPClient(host,serverPort);

        tcpClient.start();

    }
}
