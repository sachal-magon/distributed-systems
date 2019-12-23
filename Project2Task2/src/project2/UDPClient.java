//UDP client is in the package named project2
package project2;
//importing java.net and java.io, the required libraries for the client
import java.net.*;
import java.io.*;
//defining a class for the client to communicate with the server through UDP Datagram Sockets
public class UDPClient {
    //defining the main function of the class
    public static void main(String args[]) {
        // args give message contents and server hostname
        //printing "Client Running" on the console to check if this program is running
        System.out.println("Client Running");
        //calling the add function 1000 times
        for (int i = 1; i <=1000 ; i++) {
            add(i);
        }
    }
    public static void add(int num){
        //initializing a DatagramSocket object
        DatagramSocket aSocket = null;
        //used try&catch to catch SocketException and IOException
        try {
            //initializing the host of the client as the localhost or the same machine
            InetAddress aHost = InetAddress.getByName("localhost");
            //defining the serverPort as 6789, same as used in the server
            int serverPort = 6789;
            //making a new Datagram Socket
            aSocket = new DatagramSocket();
            //defining a string to read the line from the console
            String nextLine = Integer.toString(num);
                //defining the array of bytes to read the console input
                byte [] m = nextLine.getBytes();
                //initializing the DatagramPacket to send the console input to the server
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
                //sends the packet to the server with port 6789
                aSocket.send(request);
                //checks if the console input is quit!
                if(new String(request.getData()).equals("quit!")){
                    //prints on the console that the client is quitting
                    System.out.println("Quitting client...");
                }
                //creates the array of bytes with length 1000
                byte[] buffer = new byte[1000];
                //initialize a datagramPacket to receive the reply from the server
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                //receives the reply from the server
                aSocket.receive(reply);
                //prints and checks the length of the reply anf if it has trailing zeroes present or not
                //System.out.println("Length: "+ reply.getLength());
                //printing the sum of 1000 numbers from the server at the client console
            if(num==1000) {
                System.out.println("New Sum: " + new String(reply.getData()).trim());
            }
            //catching the SocketException and displaying the type of exception message
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
            //catching the IOException and displaying the type of exception message
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
            //closing the socket so that the port is released when the server is not needed
        }finally {if(aSocket != null) aSocket.close();}
    }
}