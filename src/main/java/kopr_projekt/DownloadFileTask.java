package kopr_projekt;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class DownloadFileTask implements Callable<Object> {

    private BlockingQueue<Socket> sockets;
    private String filename;
    private String fullDestinationFilePath;
    private long offset;
    private long size;
    private FileInfo file;

    public DownloadFileTask(BlockingQueue<Socket> sockets, String filename, String fullDestinationFilePath, long offset, long size) {
        this.sockets = sockets;
        this.filename = filename;
        this.fullDestinationFilePath = fullDestinationFilePath;
        this.offset = offset;
        this.size = size;
    }

    public DownloadFileTask(BlockingQueue<Socket> sockets, String filename, String fullDestinationFilePath, long size) {
        this.sockets = sockets;
        this.filename = filename;
        this.fullDestinationFilePath = fullDestinationFilePath;
        this.size = size;
    }

    public DownloadFileTask(BlockingQueue<Socket> sockets, FileInfo fileInfo) {
        this.sockets = sockets;
        this.file = fileInfo;

        // ???
        this.filename = fileInfo.getFilepath();
        this.fullDestinationFilePath = fileInfo.getFullDestinationPath();
        this.offset = fileInfo.getDownloadedAmount();
        this.size = fileInfo.getFileSize();
    }


    @Override
    public Object call() throws Exception {
        Socket socket = sockets.poll();

        InputStream in;
        FileOutputStream out;

        try {
            PrintWriter out_pw = new PrintWriter(socket.getOutputStream(), true);
            out_pw.println(filename);
            out_pw.println(offset);

            File file = null;
            synchronized (sockets) {
                String file_s = (file = new File(fullDestinationFilePath)).getParent();
                new File(file_s).mkdirs();
            }

            in = socket.getInputStream();
            out = ((offset == 0) ? new FileOutputStream(file) : new FileOutputStream(file, true));



            int count;
            int sum = (int)offset;
            byte[] bytes = new byte[Config.PART_SIZE];



            while (!Thread.currentThread().isInterrupted() &&
                    (count = in.read(bytes, 0, Config.PART_SIZE)) > 0){
//                if (Thread.currentThread().isInterrupted()){
//                    socket.close();
//                }

                out.write(bytes, 0, count);
                this.file.increaseDownloadedAmount(count);
                if((sum += count) == size)
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        sockets.offer(socket);
        return null;
    }
}
