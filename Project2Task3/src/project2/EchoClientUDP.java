//UDP client is in the package named project2
package project2;

/*
 * @author Sachal Magon
 * The EchoClientUDP class is the client in the localhost for this task
 * which helps to connect with the server in a menu driven
 * add, subtract and view
 * It also handles the exceptions!
 */

//importing the required libraries for the client
import java.net.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.InputMismatchException;
import java.util.Scanner;

//defining a class for the client to communicate with the server through UDP Datagram Sockets
public class EchoClientUDP{
    //defining the main function of the class
    public static void main(String args[]) {
        // args give message contents and server hostname
        //printing "Client Running" on the console to check if this program is running
        System.out.println("Client Running");
            //run the while loop until the nextLine is null
            while(true) {
                //Enter data using BufferReader
                int val = 0;
                Scanner scan = new Scanner(System.in);
                // Reading data using readLine
                try{
                    System.out.println("Enter user ID: ");

                int id = scan.nextInt();

                System.out.println("Enter number for operation:\n1 - add\n2 - subtract\n3 - view\n4 - exit");
                int oper = scan.nextInt();
                if (oper==1 || oper==2) {
                    System.out.println("Enter value: ");
                    val = scan.nextInt();
                    operation(id,oper,val);
                }
                else if(oper == 3){
                    operation(id,oper,val);
                }
                else if(oper>4){
                    System.out.println("Invalid Option!");
                }
                if(oper == 4){
                    System.out.println("Exiting client... Server still running!");
                    break;
                }
            }catch(InputMismatchException e){
                    System.out.println("Invalid option: Enter a number!\nStart again!");
                }
        }
    }
    public static void operation(int id, int oper, int num){
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
            //defining a string to read id, operation and value; added "|" to split the strings
            String nextLine = id + "," + oper + "," + num;
            //defining the array of bytes to read the console input
            byte[] m = nextLine.getBytes();
            //initializing the DatagramPacket to send the console input to the server
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            //sends the packet to the server with port 6789
            aSocket.send(request);
            //creates the array of bytes with length 1000
            byte[] buffer = new byte[1000];
            //initialize a datagramPacket to receive the reply from the server
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            //receives the reply from the server
            aSocket.receive(reply);
            //printing the sum of 1000 numbers from the server at the client console
            {
                System.out.println("Message from server: " + new String(reply.getData()));
            }
            //catching the SocketException and displaying the type of exception message
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
            //catching the IOException and displaying the type of exception message
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
            //closing the socket so that the port is released when the server is not needed
        }finally {if(aSocket != null) aSocket.close();}
    }
}