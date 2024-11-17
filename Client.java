import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int port) throws IOException{
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public String request(String number) throws IOException{
        out.println(number);
        out.flush();
        System.out.println("\nClient request: " + number);
        String str = in.readLine();
        System.out.println("\nServer reply: " + str);
        
        return str;
    }
    public void handshake(){
        //give pw
        out.println("12345");
        out.flush();
    }

    public void disconnect(){
        try{
            if(out != null){
                out.close();
            }
            if(in != null){
                in.close();
            }
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }catch(IOException e){
            System.err.print("IOException");
        }
    }

    public Socket getSocket(){
        return socket; 
    }
     
}
