package com.redes;

import java.util.List;

public class PrintRoutingTable extends Thread {

    private Router router;

    public PrintRoutingTable(Router router) {
        this.router = router;
    }

    public void run() {

        while (true) {
            if (!this.router.getRoutingTable().isEmpty()) {
                System.out.println("\n\n##################################");
                for (RoutingTable rt : this.router.getRoutingTable()) {
                    System.out.println(rt.getDestinatioPort() + " " + rt.getMetric() + " " + rt.getExitPort());
                }
                System.out.println("##################################");
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
