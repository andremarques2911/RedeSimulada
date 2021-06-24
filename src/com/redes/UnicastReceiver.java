package com.redes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class UnicastReceiver extends Thread {

    private Router router;
    private MulticastReceiver multicastReceiver;
    private DatagramSocket datagramSocket;
    private final int MAX_BUF = 65000;

    public UnicastReceiver(Router router, DatagramSocket socket) {
        this.router = router;
        this.datagramSocket = socket;
        this.multicastReceiver = null;
    }

    public void run() {
        while(true) {
            try {
                byte[] data = new byte[MAX_BUF];
                DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
                this.datagramSocket.receive(receivedPacket);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));

                List<RoutingTable> list = (List<RoutingTable>) ois.readObject();
                this.router.updateRoutingTable(receivedPacket.getPort(), list);
                this.router.alive(receivedPacket.getPort());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
