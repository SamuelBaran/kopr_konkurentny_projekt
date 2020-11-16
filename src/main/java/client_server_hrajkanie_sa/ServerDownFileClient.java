package client_server_hrajkanie_sa;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ServerDownFileClient {
    public static void main(String[] args) throws IOException, InterruptedException {

        var out1 = newSocketOut();
        var out2 = newSocketOut();
        var out3 = newSocketOut();

        out1.println("1");
        out2.println("2");
        out3.println("3");

        for (int i = 0; i < 15; i++) {
            System.out.print("\rTime to restart server: " + (15 - i));
            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println(out1.checkError());

        out1.println("1");
        out2.println("2");
        out3.println("3");
        out1.flush();

    }

    private static PrintWriter newSocketOut() throws IOException {
        Socket socket = new Socket("localhost", 4445);
        return new PrintWriter(socket.getOutputStream(), true);
    }

}
