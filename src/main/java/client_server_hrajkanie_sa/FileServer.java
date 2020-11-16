package client_server_hrajkanie_sa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            out = new FileOutputStream("dir\\file_new");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }

        byte[] bytes = new byte[16*1024];

//        int count;
//        while ((count = in.read(bytes)) > 0) {
//            out.write(bytes, 0, count);
//        }
        System.out.println("start Recievig and saving");
        int count;
        int i = 0;
        while ((count = in.read(bytes)) > 0) {
            System.out.println(i++);
            out.write(bytes, 0, count);
        }
        System.out.println("end Recievig and saving");

        out.close();
        in.close();
        socket.close();
        serverSocket.close();
    }

    public static void maina(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            out = new FileOutputStream("dir\\file_new");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }


        byte[] bytes = new byte[16*1024];

        System.out.println("start Recievig and saving");
        int count;
        int i = 0;
        while ((count = in.read(bytes)) > 0) {
            System.out.println(i++);
            out.write(bytes, 0, count);
        }
        System.out.println("end Recievig and saving");


        out.close();
        in.close();
        socket.close();
        serverSocket.close();
    }
}
