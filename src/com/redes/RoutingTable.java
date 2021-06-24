package com.redes;

import java.io.Serializable;

public class RoutingTable implements Serializable {

    private String destinatioPort;
    private int metric;
    private String exitPort;

    public RoutingTable(String destinatioPort, int metric, String exitPort) {
        this.destinatioPort = destinatioPort;
        this.metric = metric;
        this.exitPort = exitPort;
    }

    public String getDestinatioPort() {
        return destinatioPort;
    }

    public void setDestinatioPort(String destinatioPort) {
        this.destinatioPort = destinatioPort;
    }

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public String getExitPort() {
        return exitPort;
    }

    public void setExitPort(String exitPort) {
        this.exitPort = exitPort;
    }

}
