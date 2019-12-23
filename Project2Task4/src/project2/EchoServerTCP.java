//TCP server is in the package named project2
package project2;

//importing the required libraries for the server
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

/*
 * @author Sachal Magon
 * The EchoServerTCP class is the server in the localhost for this task
 * which helps to connect with the client in a menu driven
 * add, subtract and view and it performs all the computation
 * It also handles the exceptions!
 */

//making the server class so that client can request and respond to the server by the Datagram Socket
public class EchoServerTCP{
    //defining the main function of the class
    public static void main(String args[]){
        //printing "Server Running" on the console to check if this program is running
        System.out.println("Server Running");

        //initializing a Socket object
        Socket clientSocket = null;
        //used try&catch to catch SocketException and IOException
        try{
            int serverPort = 7777;
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);
            HashMap<String, Integer> idSum = new HashMap<String, Integer>();
            //running the server for forever so that it can keep Echoing from the client

                /*
                 * Forever,
                 *   read a line from the socket
                 *   print it to the console
                 *   echo it (i.e. write it) back to the client
                 */
            while(true){
                /*
                 * Block waiting for a new connection request from a client.
                 * When the request is received, "accept" it, and the rest
                 * the tcp protocol handshake will then take place, making
                 * the socket ready for reading and writing.
                 */
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "in" to read from the client socket
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());

                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                String data = in.nextLine();

                //Splitting the String by ','
                String[]  array = data.split(",");
                String sumString = "";
                //storing the id
                String id = array[0];
                //storing the operation
                String oper = array[1];

                if(oper.trim().equalsIgnoreCase("1")){
                    int num = Integer.parseInt(array[2]);
                    int sum = num;
                    if(idSum.get(id)!=null){
                        sum += idSum.get(id);
                    }
                    idSum.put(id,sum);
                    sumString = "OK";
                }
                if(oper.trim().equalsIgnoreCase("2")){
                    int num = Integer.parseInt(array[2]);
                    int sum = -(num);
                    if(idSum.get(id)!=null){
                        sum = idSum.get(id) + sum;
                    }
                    idSum.put(id,sum);
                    sumString = "OK";
                }
                if(oper.trim().equalsIgnoreCase("3")){
                    int sum = 0;
                    if(idSum.get(id)!=null){
                        sum = idSum.get(id);
                    }
                    sumString = Integer.toString(sum);
                }

                //send the reply to the client's request
                out.println(sumString);
                out.flush();
            }

            //catching the SocketException and displaying the type of exception message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
            //closing the socket so that the port is released when the server is not needed
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();

                }
            } catch (IOException e) {
                // ignore exception on close
                System.out.println("IO: " + e.getMessage());
            }
        }
    }
}