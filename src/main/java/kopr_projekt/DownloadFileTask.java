package kopr_projekt;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadFileTask<Void> implements Callable<Void> {

    private BlockingQueue<Socket> sockets;
    private FileInfo file;
    private AtomicLong downloadedBytes;

    public DownloadFileTask(BlockingQueue<Socket> sockets, FileInfo fileInfo, AtomicLong downloadedBytes) {
        this.sockets = sockets;
        this.file = fileInfo;
        this.downloadedBytes = downloadedBytes;
    }

    @Override
    public Void call() throws Exception {
        Socket socket = sockets.poll();

        InputStream in;
        FileOutputStream out;

        try {
            PrintWriter out_pw = new PrintWriter(socket.getOutputStream(), true);
            out_pw.println(file.getFilepath());
            out_pw.println(file.getDownloadedAmount());

            File f = new File(file.getFullDestinationPath());
            String file_s = f.getParent();
            synchronized (sockets) {
                new File(file_s).mkdirs();
            }

            in = socket.getInputStream();
            out = ((file.getDownloadedAmount() == 0) ? new FileOutputStream(f) : new FileOutputStream(f, true));


            int count;
            long sum = file.getDownloadedAmount();
            byte[] bytes = new byte[Config.PART_SIZE];

            while (!Thread.currentThread().isInterrupted() &&
                    (count = in.read(bytes, 0, Config.PART_SIZE)) > 0){
                out.write(bytes, 0, count);
                this.file.increaseDownloadedAmount(count);
                downloadedBytes.getAndAdd(count);
                if((sum += count) == file.getFileSize()) {
                    sockets.offer(socket);
                    return null;
                }
            }
        } catch (IOException e) {
            System.err.println("Connection lost.");
            throw new SocketClosedException();
        }

        if (Thread.currentThread().isInterrupted()){
            System.out.println("closing socket");
            socket.close();
            return null;
        }

        return null;
    }
}
