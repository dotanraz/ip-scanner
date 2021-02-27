package networkMonitor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkScanner {

    public static void main(String[] args) {
        ConcurrentSkipListSet<String> networkIps = networkScanner("192.168.1.0", 254);
        System.out.println("Devices connected to the network:");
        networkIps.forEach(ip -> System.out.println(ip));
    }

    /**
     *
     * @param firstIpInTheNetwork e.g: 192.168.1.0
     * @param numOfIps e.g: 254
     * @return
     */
    public static ConcurrentSkipListSet<String> networkScanner(String firstIpInTheNetwork, int numOfIps) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        final String networkId = firstIpInTheNetwork.substring(0, firstIpInTheNetwork.length() - 1);
        ConcurrentSkipListSet<String> ipsSet = new ConcurrentSkipListSet();

        AtomicInteger ips = new AtomicInteger(0);
        while (ips.get() <= numOfIps) {
            String ip = networkId + ips.getAndIncrement();
            executorService.submit(() -> {
                try {
                    InetAddress inAddress = InetAddress.getByName(ip);
                    if (inAddress.isReachable(500)) {
                        System.out.println("found ip: " + ip);
                        ipsSet.add(ip);
                    }
                }
                catch (IOException e) {

                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        return ipsSet;
    }

}
