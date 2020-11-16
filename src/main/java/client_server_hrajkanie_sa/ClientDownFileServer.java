package client_server_hrajkanie_sa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

public class ClientDownFileServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(4445);
        System.out.println("Server is running ...\n");

        var in1 = newSocketIn(serverSocket);
        var in2 = newSocketIn(serverSocket);
        var in3 = newSocketIn(serverSocket);

        System.out.println(in1.readLine());
        System.out.println(in2.readLine());
        System.out.println(in3.readLine());

        for (int i = 0; i < 15; i++) {
            System.out.print("\rTime to restart client: " + (15 - i));
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("");

        System.out.println(in1.readLine());
        System.out.println(in2.readLine());
        System.out.println(in3.readLine());

//        System.out.println(newSocketIn(serverSocket).readLine());
//        System.out.println(newSocketIn(serverSocket).readLine());
//        System.out.println(newSocketIn(serverSocket).readLine());

        System.out.println("bezi uz len umelo");
        while(true){}
    }

    public static BufferedReader newSocketIn(ServerSocket ss) throws IOException {
        return new BufferedReader(
                new InputStreamReader(ss.accept().getInputStream())
        );
    }
}

