package com.redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UnicastPublisher {

    private final DatagramSocket serverSocket;

    public UnicastPublisher(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void send(String message, InetAddress IPAddress, int port) throws IOException {
        var buffer = message.getBytes();
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, IPAddress, port);
        this.serverSocket.send(datagram);
    }

}
