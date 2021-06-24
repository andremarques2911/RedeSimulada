package com.redes;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Router {

    private DatagramSocket socket;
    private InetAddress IPAddress;
    private List<RoutingTable> routingTable;

    public Router(DatagramSocket socket, InetAddress IPAddress) {
        this.socket = socket;
        this.IPAddress = IPAddress;
        this.routingTable = new ArrayList<>();
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
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

    public List<RoutingTable> updateRoutingTable(List<RoutingTable> routingTable) {
        return null;
    }

}
