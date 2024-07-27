package com.josemarcellio.minecraftstress;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyManager {

    private static final String PROXY_FILE = "proxy.txt";
    private static final String PROXY_URL = "https://api.proxyscrape.com/v2/?request=getproxies&protocol=socks4&timeout=10000&country=all";

    // Load proxy dari file atau download jika file tidak ada
    public static List<Proxy> loadProxies() throws IOException {
        File proxyFile = new File(PROXY_FILE);
        if (!proxyFile.exists()) {
            downloadProxies();
        }
        return readProxiesFromFile(proxyFile);
    }

    // Download proxy dari URL dan dan save ke .txt
    private static void downloadProxies() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(PROXY_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("ERROR Saat download proxy: " + response);
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Null");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream()));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(PROXY_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    // Read proxy dari file
    private static List<Proxy> readProxiesFromFile(File file) throws IOException {
        List<Proxy> proxies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String ip = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    proxies.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip, port)));
                }
            }
        }
        return proxies;
    }
}
