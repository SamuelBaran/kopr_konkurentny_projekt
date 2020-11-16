package kopr_projekt;


import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ClientService<Void> extends Service<Void> {

//    StringProperty sourcePathProperty;
//    StringProperty destinationPathProperty;
//    IntegerProperty socketCountProperty;
    public IntegerProperty doneProperty;

    private String sourcePath;
    private String destinationPath;
    private int socketCount;
    private Map<String, FileInfo> files = new HashMap<>();

    public ClientService(String sourcePath, String destinationPath, int socketCount) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.socketCount = socketCount;


        doneProperty = new SimpleIntegerProperty();

//        NIEKDE VOLAT:
//        doneProperty.addListener(new ChangeListener<Long>() {
//                                 @Override
//                                 public void changed(ObservableValue<? extends Long> observable, Long oldValue, Long newValue) {
//                                     updateTitle("0/100");
//                                 };
//                             });


    }



    //    _______________________________________

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            private long totalSize = 0;
            private BlockingQueue<Socket> sockets = new LinkedBlockingQueue<>();
            private ExecutorService executor;


            @Override
            protected Void call() throws Exception {
//                for (int i = 0; i <= 10; i++) {
//                    updateTitle(i + "/" + 10);
//                    Thread.sleep(500);
//                }


                System.out.println("INIT\n" + files);
                init();
                System.out.println("\nDOWNLOAD\n" + files);
                startDownloading();
                System.out.println(files);
                return null;
            }

//            private void getInfoFromGui() throws Exception{
//                CountDownLatch latch = new CountDownLatch(3);
//
//                // potrebujem textArea resp aspon textProperty
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        sourcePath = sourcePathProperty.getValue();
//                        latch.countDown();
//                    }
//                });
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        destinationPath = destinationPathProperty.getValue();
//                        latch.countDown();
//                    }
//                });
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        socketCount = socketCountProperty.getValue();
//                        latch.countDown();
//                    }
//                });
//
//                // cakam na uvolnenie
//                latch.await();
//            }

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
                       if (!files.keySet().contains(filePath))
                            files.put(filePath, new FileInfo(filePath, createFullPath(filePath), 0L, fileSize) );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
           }

            public void startDownloading(){
                createSockets();
                int downloadedFiles = 0;

                executor = Executors.newFixedThreadPool(socketCount);
                CompletionService<Object> completionService = new ExecutorCompletionService<>(executor);


                for(String file: files.keySet()){
                    if( ! files.get(file).downloadingDone()){
                        DownloadFileTask task = new DownloadFileTask(sockets, files.get(file));
//                        DownloadFileTask task = new DownloadFileTask(sockets, file, createFullPath(file), downloadedAmount.get(file), sizes.get(file));
                        completionService.submit(task);
                    } else {
                        downloadedFiles++;
                    }
                }



                int allFiles = files.size();
                updateMessage(downloadedFiles + "/" + allFiles);

                for(; downloadedFiles < allFiles; downloadedFiles++) {
                    try {
                        completionService.take();
                        System.out.println("====== progres ====== " + (downloadedFiles + 1) + "/" + allFiles);
//                        updateProgress(downloadedFiles + 1, allFiles);
                        updateMessage((downloadedFiles+1) + "/" + allFiles);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        executor.shutdownNow();

                        //TODO: treba???
                        try {
                            executor.awaitTermination(1, TimeUnit.DAYS);
                        } catch (InterruptedException ignore) {}

                        System.err.println("Zastavene");
                        break;
                    }
                }

                executor.shutdown();
                System.out.println("Downloading ended");
            }

            private void createSockets(){
                //TODO: treba ?? nestaci doplnit do poctu ??
                sockets = new LinkedBlockingQueue<>();
                for (int i = 0; i < socketCount; i++) {
                    try {
                        sockets.offer(new Socket(Config.SERVER_IP, Config.SERVER_PORT));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private String createFullPath(String filePath){
                // https://www.programiz.com/java-programming/examples/get-relative-path

                Path fullDestPath =  Paths.get(destinationPath).toAbsolutePath();
                Path parentOfSourceDir = Paths.get(sourcePath).toAbsolutePath().getParent();
                String relative = parentOfSourceDir.toUri().
                                    relativize(new File(filePath).toURI())
                                    .getPath().replace('/', '\\');
                return fullDestPath.toString() + "\\" + relative;
            }
        };
    }
}
