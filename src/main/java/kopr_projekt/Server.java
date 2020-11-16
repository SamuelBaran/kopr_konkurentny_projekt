package kopr_projekt;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private int socketCount;
    private String sourcePath;
    private ExecutorService executor;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(Config.SERVER_PORT);
            System.out.println("Server is running ...\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(){
        try {
            Socket socket = serverSocket.accept();
            PrintWriter out_pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sourcePath = in_br.readLine();
            socketCount = Integer.parseInt(in_br.readLine());

            // https://stackoverflow.com/questions/2056221/recursively-list-files-in-java
            Files.find( Paths.get(sourcePath),
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile()
            ).forEach((Path path) -> {
                System.out.println(path);

                    out_pw.println(path.toString());
                    try {
                        out_pw.println(Files.size(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        executor = Executors.newFixedThreadPool(socketCount);

        for (int i = 0; i < socketCount; i++) {
            try {
                Socket s = serverSocket.accept();
                executor.execute(new SendFilesTask(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init();
        server.run();

    }

}
