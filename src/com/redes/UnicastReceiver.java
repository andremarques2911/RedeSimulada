package com.redes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.List;

public class UnicastReceiver extends Thread {

    private Router router;
    private MulticastReceiver multicastReceiver;
    private final int MAX_BUF = 65000;

    public UnicastReceiver(Router router) {
        this.router = router;
        this.multicastReceiver = null;
    }

    public void run() {
        while(true) {
            try {
                byte[] data = new byte[MAX_BUF];
                DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
                this.router.getSocket().receive(receivedPacket);
                String listData = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(listData.getBytes()));
                List<RoutingTable> list = (List<RoutingTable>) inputStream.readObject();
                this.router.updateRoutingTable(list);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
