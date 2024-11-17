import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Server {
    private static String pw = "12345";
    private ServerSocket serverSocket;
    private ArrayList<LocalDateTime> connectedTimes = new ArrayList<LocalDateTime>();
    private int port;

    public Server(int port) throws IOException{
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    public void serve(int numClients){
        for(int i = 0; i < numClients; i++){
            try{
                Socket clientSocket = serverSocket.accept();
                connectedTimes.add(LocalDateTime.now());
                //start thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
                //System.out.println("\nclientHandler starting....");
            }catch(IOException e){
                System.err.print("IOException");
            }
        }
    }

    public static int factor(String number){
        int numFactors = 0;
        int n = Integer.parseInt(number);
        for(int i = 1; i <= n; i++){
            if(n % i == 0){
                numFactors++;
            }
        }
        return numFactors;
    }

    private static class ClientHandler extends Thread{
        private Socket cSocket;
        private PrintWriter out;
        private BufferedReader in;
        public ClientHandler(Socket clientSocket){
            cSocket = clientSocket;
        }

        public void run(){
            try{
                out = new PrintWriter(cSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

                //handshake
                if(!in.readLine().equals(pw)){
                    out.println("couldn't handshake");
                    //System.out.println("\ncouldn't handshake");
                    //disconnect client
                    cSocket.close();
                    return;
                }

                //something is just wrong with my while loop
                cSocket.setSoTimeout(10000);
                //handling subsequent requests
                while(true){
                    //System.out.println("\nEntering loop...");
                    String request = null;
                    try{
                        request = in.readLine();
                    }catch(SocketTimeoutException e){
                        //System.out.println("Timeout exit loop");
                        break;
                    }catch (IOException e){
                        //System.out.println("Error exit loop");
                        break;
                    }
                    if(request == null || request.trim().isEmpty()){
                        //System.out.println("\nExiting loop.");
                        break;
                    }
                    int numFactors = factor(request);
                    out.println("The number " + request + " has " + numFactors + " factors");
                    //System.out.println("\n Server responded to client request.");
                }
            }catch(IOException e){
                System.err.print("IOException");
            }finally{
                try{
                    if(out != null){
                        out.close();
                    }
                    if(in != null){
                        in.close();
                    }
                    if(cSocket != null){
                        cSocket.close();
                    }
                }catch(IOException e){
                    System.err.print("IOException");
                }
            }
        }
    }

    public ArrayList<LocalDateTime> getConnectedTimes(){
        //sort connectedTimes;
        Collections.sort(connectedTimes);
        return connectedTimes;
    }

    public void disconnect(){
        try {
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("IOException");
        }
    }
}
