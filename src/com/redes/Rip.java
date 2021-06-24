package com.redes;

import java.io.*;
import java.net.DatagramPacket;
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
                    outputStream.flush();

                    byte[] listData = out.toByteArray();

                    directRouters.stream().forEach(router -> {
                            DatagramPacket packet = new DatagramPacket(listData, listData.length, this.router.getIPAddress(), Integer.parseInt(router.getExitPort()));
                            this.router.getSockets().entrySet().forEach(entry -> {
                                try {
                                    entry.getValue().send(packet);
                                } catch (IOException e) {
                                    System.out.println("Error on sending message to port " + entry.getKey());
                                }
                            });
                    });
                }
                Thread.sleep(20000);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
