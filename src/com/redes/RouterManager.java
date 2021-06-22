package com.redes;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class RouterManager {

    private Router router;
    private final Scanner scanner;
    private static int multicastPort = 4446;

    public RouterManager() throws IOException {
        // declara socket cliente e obtem endereço IP do servidor com o DNS
        this.router = new Router(new DatagramSocket(), InetAddress.getByName("localhost"));

        // cria o stream do teclado
        this.scanner = new Scanner(System.in);

        //Inicia thread responsável por receber mensagens
        new UnicastReceiver(this.router.getSocket(), this.router.getIPAddress()).start();

        new PrintRoutingTable(this.router).start();
    }

    public void run() throws IOException {
        while (true) {
            System.out.println("Digite 1 para configurar uma porta local do roteador.");
            System.out.println("Digite 2 para configurar uma porta vizinha do roteador.");
            // lê uma linha do teclado
            System.out.print("Comando: ");
            String sentence = this.scanner.nextLine();

            String destinationPort = null;
            RoutingTable rt = null;
            switch (sentence) {
                case "1":
                    if (this.router.getLocalPorts().size() == 2) {
                        System.err.println("Todas as portas disponíveis já estão configuradas.");
                        continue;
                    }
                    System.out.print("Informe a porta de destino: ");
                    destinationPort = this.scanner.nextLine();
                    rt = new RoutingTable(destinationPort, 0, "Local");
                    this.router.addPort(rt);
                    break;
                case "2":
                    System.out.print("Informe a porta de destino: ");
                    destinationPort = this.scanner.nextLine();
                    System.out.print("Informe a porta de saída: ");
                    String exitPort = this.scanner.nextLine();
                    rt = new RoutingTable(destinationPort, 1, exitPort);
                    this.router.addPort(rt);
                    break;
                default:
                    byte[] sendData = sentence.getBytes();

                    // cria pacote com o dado, o endereço do server e porta do servidor
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.router.getIPAddress(), 9880);

                    //envia o pacote
                    this.router.getSocket().send(sendPacket);
            }
        }
    }

}
