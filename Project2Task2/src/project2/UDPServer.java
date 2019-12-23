//UDP server is in the package named project2
package project2;

//importing java.net and java.io, the required libraries for the server
import java.net.*;
import java.io.*;
import java.sql.SQLOutput;

//making the server class so that client can request and respond to the server by the Datagram Socket

public class UDPServer {
    //defining the main function of the class
    public static void main(String args[]){
        //printing "Server Running" on the console to check if this program is running
        System.out.println("Server Running");
        //integer value sum initialized to 0
        int sum = 0;
        //initializing a DatagramSocket object
        DatagramSocket aSocket = null;
        //used try&catch to catch SocketException and IOException
        try{
            //defining the port of the server as 6789 (it can be other numbers as well)
            aSocket = new DatagramSocket(6789);
            //running the server for forever so that it can keep Echoing from the client
            while(true){
                //initializing the buffer of array of bytes with length 1000
                byte[] buffer = new byte[1000];
                //initalizing a request of the type DatagramPacket
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                //aSocket stores the request from the client of the type Datagram Socket
                aSocket.receive(request);
                /* initializing new array of bytes with the length equal to the requested string
                from the client to remove the trailing zeroes */
                byte[] newBuffer = new byte[request.getLength()];
                //copying the requested data in the newBuffer to remove the trailing 0s by offsetting
                System.arraycopy(request.getData(), request.getOffset(), newBuffer, 0, request.getLength());
                //converting newBuffer to String
                String requestString = new String(newBuffer);
                //checking if the client wants to quit!
                if(requestString.equals("quit!")){
                    //prints on the console that the server is quitting
                    System.out.println("Quitting server...");
                    //breaks the program in between and stops the server
                    break;
                }
                //changing the request datagram packet to int
                int num = Integer.parseInt(requestString);
                //calculate sum
                sum+=num;
                //Convert sum to byte array
                String sumString = Integer.toString(sum);
                byte[] sumBuffer = sumString.getBytes();
                //creating a new DatagramPacket to reply to the client with the sum
                DatagramPacket reply = new DatagramPacket(sumBuffer, sumBuffer.length,
                        request.getAddress(), request.getPort());
                //printing the number and new sum on the server console
                if(requestString.equalsIgnoreCase("1000")){
                    System.out.println("New sum: " + sum);
                }
                //send the reply to the client's request
                aSocket.send(reply);
            }
            //catching the SocketException and displaying the type of exception message
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
            //catching the IOException and displaying the type of exception message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        //closing the socket so that the port is released when the server is not needed
        }finally {if(aSocket != null) aSocket.close();}
    }
}