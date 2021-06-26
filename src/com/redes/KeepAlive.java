package com.redes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeepAlive extends Thread{

    ConcurrentHashMap<Integer, Integer> times;

    Integer period = 5;

    Router router;

    public KeepAlive(Router router) {
        this.times = new ConcurrentHashMap<>();
        this.router = router;
    }

    public void alive(int port) {
        times.put(port, 0);
    }

    public void run() {
        while (true) {
            try {
                times.entrySet()
                        .forEach(e -> times.put(e.getKey(), e.getValue() + period));


//                times.entrySet()
//                        .forEach(e -> System.out.println(e.getKey() + " | " + e.getValue()));


                times.entrySet()
                        .stream().filter(p -> p.getValue() >= 30)
                        .forEach(f -> router.disable(f.getKey()));

                Thread.sleep(period * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
