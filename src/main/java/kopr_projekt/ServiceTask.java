package kopr_projekt;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ServiceTask extends Task<Void> {

//    private String textValue;
//            private SpellChecker spellChecker = new SpellChecker();
    private String sourcePath;
    private String destinationPath;
    private int socketCount;


    private long totalSize = 0;
    private Map<String, Long> sizes = new HashMap<>();
    private BlockingQueue<Socket> sockets = new LinkedBlockingQueue<>();

    private ExecutorService executor;


    @Override
    protected Void call() throws Exception {
//        new Server().main(new String[0]);

//        getInfoFromGui();
        init();
        startDownloading();
        return null;
    }

//    private void getInfoFromGui(){
//        CountDownLatch latch = new CountDownLatch(3);
//
//        // potrebujem textArea resp aspon textProperty
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                sourcePath = sourcePathProperty.getValue();
//                latch.countDown();
//            }
//        });
//
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                destinationPath = destinationPathProperty.getValue();
//                latch.countDown();
//            }
//        });
//
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                socketNumber = socketCountProperty.getValue();
//                latch.countDown();
//            }
//        });
//
//        // cakam na uvolnenie
//        latch.await();
//
//
//    }

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
        out_pw.println(socketCount);

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
        for (int i = 0; i < socketCount; i++) {
            try {
                sockets.offer(new Socket(Config.SERVER_IP, Config.SERVER_PORT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        executor = Executors.newFixedThreadPool(socketCount);
        CompletionService<Object> completionService = new ExecutorCompletionService<>(executor);


        for(String file: sizes.keySet()){
            DownloadFileTask task = new DownloadFileTask(sockets, file, destinationPath+file, sizes.get(file));
            completionService.submit(task);
        }


        int downloadedFiles = 0;
        int allFiles = sizes.size();

        for(;downloadedFiles < allFiles; downloadedFiles++) {
            try {
                completionService.take();
                updateProgress(downloadedFiles, allFiles);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        System.out.println("Downloading done");
    }

}
