package ru.nsu.NetwTech.zolotorevskii.relaibleUdp;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Server {
    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        DatagramSocket socket = new DatagramSocket(1234, inetAddress);
        String string = "Massage sent correctly";
        byte Ack = 0;
        byte Seq = 0;
        byte[] buf = new byte[512];
        while (true) {
            DatagramPacket packetRecieve = new DatagramPacket(buf, buf.length);
            socket.receive(packetRecieve);
            buf = packetRecieve.getData();
            String dataStr = new String(buf);
            dataStr = dataStr.substring(0, packetRecieve.getLength() - 2);

            Seq = buf[packetRecieve.getLength() - 1];
            Ack = buf[packetRecieve.getLength()- 2];
            Ack += dataStr.length();
            System.out.println(dataStr + " SEQ=" + Seq + ", ACK=" + Ack);

            Random random = new Random();
            int randomValue = random.nextInt(10);
            byte[] bufSend ;
            if(randomValue > 3){
                System.out.println("Success");
                bufSend = ByteBuffer.allocate(string.length() + 2).put(string.getBytes(StandardCharsets.UTF_8)).array();
                bufSend[bufSend.length - 1] = Ack;
                bufSend[bufSend.length - 2] = Seq;

                InetAddress addrServer  =packetRecieve.getAddress();
                int portServer = packetRecieve.getPort();

                DatagramPacket packetSend = new DatagramPacket(bufSend, bufSend.length,addrServer,portServer);
                socket.send(packetSend);
            }
            else{
                System.out.println("Failure");
            }
        }
    }
}
