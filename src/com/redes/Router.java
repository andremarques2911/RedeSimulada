package com.redes;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Router {

    private Map<Integer, DatagramSocket> sockets;
    private InetAddress IPAddress;
    private List<RoutingTable> routingTable;
    private KeepAlive keepAlive;

    public Router(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
        this.routingTable = new ArrayList<>();
        this.sockets = new HashMap<>();
        this.keepAlive = new KeepAlive(this);
        keepAlive.start();
    }

    public void addSocket(int port) {
        try {
            sockets.put(port, new DatagramSocket(port));
        } catch (Exception e) {
            System.out.println("Erro to insert new socket with port " + port);
        }
    }

    public void disable(int port) {

        System.out.println("Removing all of exit port " + port);

        this.routingTable = this.routingTable
                .stream()
                .filter(p -> !p.getExitPort().equals(String.valueOf(port)))
                .collect(Collectors.toList());

        this.keepAlive.times.remove(port);
    }

    public Map<Integer, DatagramSocket> getSockets() {
        return sockets;
    }

    public void setSockets(Map<Integer, DatagramSocket> sockets) {
        this.sockets = sockets;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    public List<RoutingTable> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(List<RoutingTable> routingTable) {
        this.routingTable = routingTable;
    }

    public List<RoutingTable> getLocalPorts() {
        return this.routingTable.stream().filter(rt -> rt.getExitPort().equals("Local")).collect(Collectors.toList());
    }

    public List<RoutingTable> getDirectPorts() {
        return this.routingTable.stream().filter(rt -> rt.getMetric() == 1).collect(Collectors.toList());
    }

    public void addPort(RoutingTable routingTable) {
        this.routingTable.add(routingTable);
    }

    public void alive(Integer port) {
        this.keepAlive.alive(port);
    }

    public void updateRoutingTable(Integer port, List<RoutingTable> routingTable) {
        for (RoutingTable received : routingTable) {
            this.routingTable.stream()
                    .filter(p -> p.getDestinationPort().equals(received.getDestinationPort())
                            && p.getMetric() > (received.getMetric() + 1)
                            && !received.getExitPort().equals("Local")
                    )
                    .forEach(r -> {
                        System.out.print(String.format("Alterada rota [%s %s %s] para [%s %s %s]", r.getDestinationPort(), r.getMetric(), r.getExitPort()));
                        r.setMetric(received.getMetric());
                        r.setExitPort(String.valueOf(port));
                        System.out.print(String.format(" para [%s %s %s]", r.getDestinationPort(), r.getMetric(), r.getExitPort()));
                        System.out.println();
                    });
        }

        routingTable
                .stream()
                .filter(p -> this.routingTable.stream().filter(r -> r.getDestinationPort().equals(p.getDestinationPort())).count() == 0 )
                .forEach(r -> {
                    int metric = r.getMetric() + 1;

                    RoutingTable item = new RoutingTable(r.getDestinationPort(), metric, port.toString());
                    
                    System.out.println(String.format("Adicionada rota [%s %s %s]", item.getDestinationPort(), item.getMetric(), item.getExitPort()));
                    this.routingTable.add(item);
                });

        for (RoutingTable received : this.routingTable) {
            if (received.getExitPort().equals(port)) {
                if (routingTable.stream().filter(p -> p.getDestinationPort().equals(received.getDestinationPort())).count() == 0) {
                    System.out.println(String.format("Removida rota [%s %s %s]", received.getDestinationPort(), received.getMetric(), received.getExitPort()));
                    this.routingTable.remove(received);
                }
            }
        }

    }

}
