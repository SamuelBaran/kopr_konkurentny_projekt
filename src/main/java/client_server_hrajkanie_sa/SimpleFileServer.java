package client_server_hrajkanie_sa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SimpleFileServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4444);
        System.out.println("Server is running ...\n");

        Socket socket = serverSocket.accept();

//        jeden file
        sendFile(socket, "dir\\sk_dataset.zip");
        sendFile(socket, "dir\\sk_dataset.zip");
//        sendFile(socket, "dir\\file_orig");

////        viac fileov
//        sendFile(socket, "dir\\sk_dataset.zip");
//        sendFile(socket, "dir\\file_orig2");


        System.out.println("bezi uz len umelo");
        while(true){}

//        socket.close();
    }

    public static void sendFile(Socket socket, String filepath) throws IOException {
//        1. client sending request for filepath
        PrintWriter out_pw = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//        2. server recieving and sending request
        String request = in_br.readLine();
//        System.out.println(request);
        out_pw.println(filepath);

//        3. client working
        DataOutputStream out_dos = new DataOutputStream(socket.getOutputStream());

//        4. server recieving and sending request
        request = in_br.readLine();
//        System.out.println(request);
        File file = new File(filepath);
//        Get the size of the file
        int length = (int) file.length();
        out_dos.writeInt(length);
//        System.out.println(length);

//        5. client working
//        6.
        request = in_br.readLine();
//        System.out.println(request);

        System.out.println("Request to send file: "+ filepath + "\n" +
                "\tSize of File: " + length + "bytes");


        FileInputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        System.out.println("sending file");
        long startTime = new Date().getTime();

//        int len = in.available();
//        System.out.println("aval: " + len); // <=>
//        System.out.println("leng: " + length);

        int size = 1000;
        int count = 0;
        int sended = 0;
        byte[] bytes = new byte[size];

        for (; sended < length;) {
            sended += (count = in.read(bytes, 0, size));
            out.write(bytes, 0, count);
        }
        System.out.println("sended: " + sended + " / " +length );


        System.out.println("Total time " + (new Date().getTime() - startTime)/1000);




//        byte[] bytes = new byte[length];
//        FileInputStream in = new FileInputStream(file);
//        OutputStream out = socket.getOutputStream();
//
//        System.out.println("loading file");
//        long startTime = new Date().getTime();
//        int count = in.read(bytes);
//        long loadTime = new Date().getTime()-startTime;
//        System.out.println("\tTme of loading "+ loadTime/1000);
//
//        System.out.println("sending file");
//        out.write(bytes, 0, count);
//        long sendTime = new Date().getTime()-startTime-loadTime;
//        System.out.println("file sended\n");
//        System.out.println("\tTime of sending "+ sendTime/1000);
//        System.out.println("Total time " + (new Date().getTime() - startTime)/1000);
    }


}

