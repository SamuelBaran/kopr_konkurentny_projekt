package kopr_projekt;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class SendFilesTask implements Runnable {
    private Socket socket;

    boolean run = true;

    public SendFilesTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (run){
            sendFile();
        }
    }

    private void sendFile(){
        String filepath = null;
        int offset = 0;
        try {
            BufferedReader in_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            filepath = in_br.readLine();
            offset = Integer.parseInt(in_br.readLine().trim());

        } catch (IOException e) {
//            e.printStackTrace();
            run = false;
            return;
        }

        FileInputStream in = null;
        OutputStream out = null;
        int length = 0;
        try {
            in = new FileInputStream(filepath);
            length = in.available();
            out = socket.getOutputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\tStart sending file: " + filepath);
        long startTime = new Date().getTime();



        int count = 0;
        int sended = offset;
        byte[] bytes = new byte[Config.PART_SIZE];

        try {
            in.skip(offset);
            for (; sended < length;) {
                sended += (count = in.read(bytes, 0, Config.PART_SIZE));
                out.write(bytes, 0, count);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.err.println("socket closed");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\tEnd sending file:   " + filepath +
                "\n\t\tTotal time " + (new Date().getTime() - startTime)/1000 +
                "\n\t\tsended: " + sended + " / " +length);

    }
}
