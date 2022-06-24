package ru.nsu.fit.oop.zolotorevskii.lab5.Model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        DatagramSocket client = new DatagramSocket(4321,inetAddress);
        String string;
        byte Ack = 0;
        byte Seq = 0;
        client.setSoTimeout(1500);
        byte[] buf;
        while(true){
            Scanner scanner = new Scanner(System.in);
            string = scanner.nextLine();

            buf = ByteBuffer.allocate(string.length() + 2).put(string.getBytes(StandardCharsets.UTF_8)).array();
            buf[buf.length - 1] = Ack;
            buf[buf.length - 2] = Seq;

            DatagramPacket packet = new DatagramPacket(buf, string.length() + 2,inetAddress,1234);

            client.send(packet);
            byte[] bufReceive = new byte[512];
            DatagramPacket packetReceive = new DatagramPacket(bufReceive, bufReceive.length);
            boolean isReceive = true;
            try{
                client.receive(packetReceive);
                System.out.println("Receive packet");
            }catch(SocketTimeoutException ex){
                isReceive = false;
            }

            if(!isReceive){
                System.out.println("Lost 1/3 packet");
                int i;
                for(i = 2; i < 4; i++){
                    client.send(packet);
                    try{
                        client.receive(packetReceive);
                        System.out.println("Receive " + i + "/3 packet");
                        break;
                    }catch(SocketTimeoutException ex1){
                        System.out.println("Lost " + i + "/3 packet");
                    }
                }
                if(i == 4){
                    System.out.println("Server is broke or we are looser");
                    break;
                }
            }

            bufReceive = packetReceive.getData();
            String dataStr = new String(bufReceive);
            dataStr = dataStr.substring(0, packetReceive.getLength() - 2);

            Seq = bufReceive[packetReceive.getLength() - 1];
            Ack = bufReceive[packetReceive.getLength() - 2];
            Ack+=packetReceive.getLength() - 2;
            System.out.println(dataStr + " SEQ=" + Seq + ", ACK=" + Ack);
        }
    }
}
