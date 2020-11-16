package client_server_hrajkanie_sa;

import java.io.*;
import java.net.Socket;

public class FileClient {
    public static void maina(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 4444);

        File file = new File("dir\\file_orig");

        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        System.out.println("start");
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
        System.out.println("start");


        out.close();
        in.close();
        socket.close();
    }
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "127.0.0.1";

        socket = new Socket(host, 4444);

        File file = new File("dir\\file_orig");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int count;
        //System.out.println(in.read(bytes));
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
    }
}
