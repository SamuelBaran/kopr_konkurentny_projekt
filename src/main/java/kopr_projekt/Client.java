package kopr_projekt;

import javafx.beans.property.SimpleDoubleProperty;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class Client implements Callable<Void> {
    private String sourcePath;
    private String destinationPath;
    private int socketNumber;

    SimpleDoubleProperty downloadedFilespercentage;

    private long totalSize = 0;
    private Map<String, Long> sizes = new HashMap<>();
    private BlockingQueue<Socket> sockets = new LinkedBlockingQueue<>();

    private ExecutorService executor;

    public Client(String sourcePath, String destinationPath, int socketNumber) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.socketNumber = socketNumber;
        downloadedFilespercentage = new SimpleDoubleProperty(0);
        this.init();
    }

    private void init() {
        PrintWriter out_pw = null;
        BufferedReader in_br = null;
        try {
            Socket socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
            out_pw = new PrintWriter(socket.getOutputStream(), true);
            in_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        out_pw.println(sourcePath);
        out_pw.println(socketNumber);

        String filePath;
        long fileSize;
        try {
            while ((filePath = in_br.readLine()) != null) {
                fileSize = Long.parseLong(in_br.readLine());
                totalSize += fileSize;
                sizes.put(filePath, fileSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startDownloading(){
        System.out.println("Downloading started");
        for (int i = 0; i < socketNumber; i++) {
            try {
                sockets.offer(new Socket(Config.SERVER_IP, Config.SERVER_PORT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        executor = Executors.newFixedThreadPool(socketNumber);
        CompletionService<Object> completionService = new ExecutorCompletionService<>(executor);


        for(String file: sizes.keySet()){
            DownloadFileTask task = new DownloadFileTask(sockets, file, destinationPath+file, sizes.get(file));
            completionService.submit(task);
        }


        int downloadedFiles = 0;

        for(;downloadedFiles < sizes.size(); downloadedFiles++) {
            try {
                completionService.take();
                downloadedFilespercentage.set(downloadedFiles / sizes.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        System.out.println("Downloading done");
    }

    public static void main(String[] args) {
//        Client client = new Client("dir\\", "dir1\\", 2);
//        client.startDownloading();

        Map<Integer, FileInfo> m = new HashMap<>();
        m.put(1, new FileInfo("", "", 0, 10));
        System.out.println(m);

        FileInfo f = m.get(1);
        f.increaseDownloadedAmount(5);
        System.out.println(f);

        System.out.println(m);

        m.put(1, f);
        System.out.println(m);




    }

    public SimpleDoubleProperty getFilesCount() {
        return downloadedFilespercentage;
    }

    public void plus(){
        downloadedFilespercentage.set(downloadedFilespercentage.get() + 0.1);
    }



    @Override
    public Void call() throws Exception {
        startDownloading();
        return null;
    }
}
