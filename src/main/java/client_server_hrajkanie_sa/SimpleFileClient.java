package client_server_hrajkanie_sa;

import kopr_projekt.Config;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SimpleFileClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 4444);

//        jeden file
        saveFile(socket,  "dir\\aa.zip", false);
        saveFile(socket,  "dir\\bb.zip", true);
////        viac fileov
//        saveFile(socket,  "dir\\aa.zip");
//        saveFile(socket,  "dir\\file_new2");

        socket.close();
    }

    private static void saveFile(Socket socket, String destinationPath, boolean fixed) throws IOException {
//        1.    client posle spravu "filepath"
//        2.    server posle filep[ath posielaneho suboru
//        3.    client
//                  vytvori adresarovu strukturu
//                  posle spravu "count" na pocet bajtov
//        4.    server posle pocet bajtov
//        5.    client
//                  vytvori potrebu pamat
//                  posle spravu "data"
//        6.    server posiela pole bajtov dlzky count
//        7.    client prijima pole bajtov dlzky count
        PrintWriter out_pw = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//  1.
        System.out.println("Client request:   \"filepath\"");
        out_pw.println("filepath");

//  2. server working
        String filepath = in_br.readLine();
        System.out.println("Server response:  " + filepath);

//  3.
        System.out.println("Client request:   \"count\"");
        out_pw.println("count");

//  4. server working
        DataInputStream in_dis = new DataInputStream(socket.getInputStream());
        int length = in_dis.readInt();
        System.out.println("Server response:  " + length);

//  5.



        System.out.println("sending file");
        long startTime = new Date().getTime();

        if (fixed) {
            byte[] bytes = new byte[1000];
            System.out.println("Client request:   \"data\"");
            out_pw.println("data");

//  6.
            InputStream in = socket.getInputStream();
            FileOutputStream out = new FileOutputStream(destinationPath);
            System.out.println("Server response:");
            System.out.println("\tstart recievig and saving data");
            int count;
            int sum = 0;


            while ((count = in.read(bytes, 0, 1000)) > 0){
//        while ((count = pom(in, bytes)) > 0) {

                out.write(bytes, 0, count);
                if((sum += count) == length)
                    break;
//            System.out.println(count + "\t" + sum);
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            }
        } else {
            byte[] bytes = new byte[length];
            System.out.println("Client request:   \"data\"");
            out_pw.println("data");

//  6.
            InputStream in = socket.getInputStream();
            FileOutputStream out = new FileOutputStream(destinationPath);
            System.out.println("Server response:");
            System.out.println("\tstart recievig and saving data");
            int count;
            int sum = 0;


            while ((count = in.read(bytes)) > 0){
//        while ((count = pom(in, bytes)) > 0) {

                out.write(bytes, 0, count);
                if((sum += count) == length)
                    break;
//            System.out.println(count + "\t" + sum);
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            }
        }



        System.out.println("\tend recievig and saving data");
        System.out.println("Total time " + (new Date().getTime() - startTime)/1000);

    }

    public static int pom(InputStream in, byte[] bytes){
        try{
            return in.read(bytes);
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Waiting for server 10 seconds");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }
            return pom(in, bytes);
        }
    }
}
