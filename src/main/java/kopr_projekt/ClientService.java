package kopr_projekt;


import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ClientService extends Service<ProgressData> {

    private String sourcePath;
    private String destinationPath;
    private int socketCount;
    private Map<String, FileInfo> files = new HashMap<>();
    private AtomicLong downloadedBytes;


    public ClientService(String sourcePath, String destinationPath, int socketCount) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.socketCount = socketCount;
    }


    @Override
    protected Task<ProgressData> createTask() {
        return new Task<ProgressData>() {

            private long totalSize = 0;
            private BlockingQueue<Socket> sockets = new LinkedBlockingQueue<>();
            private ExecutorService executor;
            private ProgressData progressData;


            @Override
            protected ProgressData call() throws Exception {
                                    System.out.println("INIT\n" + files);
                init();
                                    System.out.println("\nDOWNLOAD\n" + files);
                startDownloading();
                                    System.out.println(files);
                return null;
            }

            private void init() throws IOException {
                //          POSIELAM DIR A POCET SOCKETOV
                Socket socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
                PrintWriter out_pw = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out_pw.println(sourcePath);
                out_pw.println(socketCount);


                //       PRIJIMANIE INFO O FILEOCH
                String filePath;
                long fileSize;
                downloadedBytes = new AtomicLong(0);
                long downloadedFiles = 0;

                while ((filePath = in_br.readLine()) != null) {
                    fileSize = Long.parseLong(in_br.readLine());

                    // Celkova velkost
                    totalSize += fileSize;

                    // ak nemam pridam
                    if (!files.keySet().contains(filePath)){
                        File f = new File(createFullPath(filePath));
                        long downloadedAmount = f.exists() ? f.length() : 0;
                        files.put(filePath, new FileInfo(filePath, createFullPath(filePath), downloadedAmount, fileSize) );
                    }

                    // na init value ProgressData
                    long downloadedBytesForFile = files.get(filePath).getDownloadedAmount();
                    downloadedBytes.getAndAdd(downloadedBytesForFile);
                    if(downloadedBytesForFile == fileSize)
                        downloadedFiles++;
                }

                progressData = new ProgressData(downloadedFiles, files.size(), downloadedBytes.get(), totalSize);
            }

            public void startDownloading() throws Exception {
                var progressExecutor = Executors.newScheduledThreadPool(1);
                progressExecutor.scheduleAtFixedRate(
                        ()->{
                            progressData.getByteAmountProgress().setDownloaded(downloadedBytes.get());
                            updateValue(progressData.copy());
                        },
                        0,
                        100,
                        TimeUnit.MILLISECONDS
                        );


                createSockets();
                int downloadedFiles = 0;

                executor = Executors.newFixedThreadPool(socketCount);
                CompletionService<Object> completionService = new ExecutorCompletionService<>(executor);

                for(FileInfo fileInfo: files.values()){
                    if( ! fileInfo.downloadingDone()){
                        DownloadFileTask task = new DownloadFileTask(sockets, fileInfo, downloadedBytes);
                        completionService.submit(task);
                    } else {
                        downloadedFiles++;
                    }
                }

                int allFiles = files.size();
                for(; downloadedFiles < allFiles; downloadedFiles++) {
                    try {
                        completionService.take().get();
                        System.out.println("====== progres ====== " + (downloadedFiles + 1) + "/" + allFiles);
                        progressData.getFileAmountProgress().addToDownloaded(1);
                        updateValue(progressData.copy());
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        progressExecutor.shutdownNow();

                        try {
                            executor.awaitTermination(1, TimeUnit.DAYS);
                            progressExecutor.awaitTermination(1, TimeUnit.DAYS);
                        } catch (InterruptedException ignore) {}

                        System.err.println("Zastavene");
                        break;
                    } catch (ExecutionException e) {
                        // server zomrel
                        if (e.getCause() instanceof SocketClosedException){
                            throw new SocketClosedException();
                        }
                    }
                }

                progressData.getByteAmountProgress().setDownloaded(downloadedBytes.get());
                updateValue(progressData.copy());
                executor.shutdownNow();
                progressExecutor.shutdownNow();

                try {
                    executor.awaitTermination(1, TimeUnit.DAYS);
                    progressExecutor.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ignore) {}
                System.out.println("Downloading ended");

                // upracem sockety
                System.err.println("S: "+sockets.size());
                while(sockets.size() > 0) {
                    try {
                        sockets.poll().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.err.println("Sk: "+sockets.size());
            }

            private void createSockets(){
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
