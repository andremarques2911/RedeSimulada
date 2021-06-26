package com.redes;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(out);
                    outputStream.writeObject(routers);
                    outputStream.flush();

                    byte[] listData = out.toByteArray();

                    directRouters.stream().forEach(router -> {
                        DatagramPacket packet = new DatagramPacket(listData, listData.length, this.router.getIPAddress(), Integer.parseInt(router.getExitPort()));
                        DatagramSocket socket = this.router.getSockets().get(Integer.parseInt(router.getLocalPort()));
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            System.out.println("Error on sending message to port " + router.getLocalPort());
                        }
                    });
                }
                Thread.sleep(20000);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
