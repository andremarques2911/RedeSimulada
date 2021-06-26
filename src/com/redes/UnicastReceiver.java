package com.redes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class UnicastReceiver extends Thread {

    private Router router;
    private DatagramSocket datagramSocket;
    private final int MAX_BUF = 65000;

    public UnicastReceiver(Router router, DatagramSocket socket) {
        this.router = router;
        this.datagramSocket = socket;
    }

    public void run() {
        while(true) {
            try {
                byte[] data = new byte[MAX_BUF];
                DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
                this.datagramSocket.receive(receivedPacket);
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                if (message.startsWith("::msg")) {
                    System.out.println("Pacote recebido da porta " + receivedPacket.getPort());
                    String[] splitMessage = message.split(" ");
                    message = this.getMessage(splitMessage);
                    String destinationPort = splitMessage[1];

                    if (this.router.getSockets().get(Integer.parseInt(destinationPort)) != null) {
                        System.out.println("O pacote era para este roteador");
                        System.out.println("Mensagem recebida: " + message);
                    } else {
                        System.out.println("O pacote não era para este roteador");
                        String send = "::msg " + destinationPort + " " + message;
                        byte[] sendData = send.getBytes();
                        Integer port = this.router.getExitPort(destinationPort);
                        // cria pacote com o dado, o endereço do server e porta do servidor
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.router.getIPAddress(), port);
                        System.out.println("Enviando para o destino pela porta " + this.datagramSocket.getLocalPort());
                        //envia o pacote
                        DatagramSocket socket = this.router.getSocketByPort(port);
                        if (socket != null) {
                            socket.send(sendPacket);
                        }
                    }
                } else {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                    List<RoutingTable> list = (List<RoutingTable>) ois.readObject();
                    this.router.updateRoutingTable(receivedPacket.getPort(), datagramSocket.getLocalPort(), list);
                    this.router.alive(receivedPacket.getPort());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getMessage(String[] splitMessage) {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < splitMessage.length; i++) {
            sb.append(splitMessage[i]);
            if (i + 1 != splitMessage.length) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
