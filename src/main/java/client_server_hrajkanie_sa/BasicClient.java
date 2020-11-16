package client_server_hrajkanie_sa;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class BasicClient {
    private Socket socket;
    private Scanner scanner;
    
    private BasicClient(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }
    
    private void start() throws IOException {
        String input;
        while (true) {
            input = scanner.nextLine();
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(input);
            out.flush();
        }
    }
    
    public static void main(String[] args) throws Exception {
        BasicClient basicClient = new BasicClient(
                InetAddress.getByName(args[0]), 
                Integer.parseInt(args[1]));
        
        System.out.println("\r\nConnected to Server: " + basicClient.socket.getInetAddress());
        basicClient.start();
    }
}