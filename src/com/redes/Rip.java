package com.redes;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Rip extends Thread {

    private Router router;

    public Rip(Router router) {
        this.router = router;
    }

    public void run() {
        while (true) {
            List<RoutingTable> routers = this.router.getRoutingTable();
            List<RoutingTable> directRouters = this.router.getDirectPorts();
            try {
                if (!directRouters.isEmpty()) {
                    // Sender
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(out);
                    outputStream.writeObject(routers);
                    outputStream.close();

                    byte[] listData = out.toByteArray();

                    directRouters.stream().forEach(router -> {
                        try {
                            DatagramPacket packet = new DatagramPacket(listData, listData.length, this.router.getIPAddress(), Integer.parseInt(router.getExitPort()));
//                            DatagramSocket socket = new DatagramSocket(Integer.parseInt(router.getDestinatioPort()), InetAddress.getLocalHost());
                            this.router.getSocket().send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                Thread.sleep(60000);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
