package com.neilalexander.jnacl.crypto;

import org.testng.annotations.Test;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by kabramovich on 19.08.2016.
 */
public class SecureServerTest {
    public static final int HEADER_LENGTH = 32;
    public static final int PACKET_LENGTH = 320;

    private void processClientSocket(Socket socket) {
        byte[] array = new byte[PACKET_LENGTH];
        System.out.println(socket.getRemoteSocketAddress().toString());
        System.out.println(socket.getPort());
    }

    @Test
    public void checkServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(9191);
        Executor executor = Executors.newFixedThreadPool(100);
        while (true) {
            final Socket clientSocket = serverSocket.accept();
            executor.execute(() -> {processClientSocket(clientSocket);});
        }
    }

    @Test
    public void checkCLient() throws Exception {
        Socket socket = new Socket("127.0.0.1", 9191);
        OutputStream out  = socket.getOutputStream();
        while (true) {
            out.write("hello\n".getBytes());
            Thread.sleep(1000);
        }
    }
}
