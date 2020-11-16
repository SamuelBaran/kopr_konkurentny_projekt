package client_server_hrajkanie_sa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerDownFileServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4445);
        System.out.println("Server is running ...\n");

        var in1 = newSocketIn(serverSocket);
        var in2 = newSocketIn(serverSocket);
        var in3 = newSocketIn(serverSocket);

        System.out.println(in1.readLine());
        System.out.println(in2.readLine());
        System.out.println(in3.readLine());

//        System.out.println(in1.readLine());
//        System.out.println(in2.readLine());
//        System.out.println(in3.readLine());


        System.out.println("bezi uz len umelo");
        while(true){}
    }

    public static BufferedReader newSocketIn(ServerSocket ss) throws IOException {
        return new BufferedReader(
                new InputStreamReader(ss.accept().getInputStream())
        );
    }
}

