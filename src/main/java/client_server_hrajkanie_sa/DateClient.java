package client_server_hrajkanie_sa;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * A command line client for the date server. Requires the IP address of the
 * server as the sole argument. Exits after printing the response.
 */
public class DateClient {
    public static void main(String[] args) throws IOException {
//        if (args.length != 1) {
//            System.err.println("Pass the server IP as the sole command line argument");
//            return;
//        }
        var socket = new Socket("localhost", 59090);
        var in = new Scanner(socket.getInputStream());
        for (int i = 0; i < 10; i++) {
        	System.out.println("Server response: " + in.nextLine());
		}
    }
}