//TCP client is in the package named project2
package project2;
//importing the required libraries for the client

/*
 * @author Sachal Magon
 * The EchoClientTCP class is the client in the localhost for this task
 * which helps to connect with the server in a menu driven
 * add, subtract and view
 * It also handles the exceptions!
 */

import java.net.*;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

//defining a class for the client to communicate with the server through UDP Datagram Sockets
public class EchoClientTCP {
    //defining the main function of the class
        public static void main (String args[]){
        // args give message contents and server hostname
        //printing "Client Running" on the console to check if this program is running
        System.out.println("Client Running");
        //run the while loop until the nextLine is null
        while (true) {
            //Enter data using BufferReader
            int val = 0;
            Scanner scan = new Scanner(System.in);
            // Reading data using readLine
            try {
                System.out.println("Enter user ID: ");

                int id = scan.nextInt();
            //Menu driven add, subtract, view and exit
                System.out.println("Enter number for operation:\n1 - add\n2 - subtract\n3 - view\n4 - exit");
                int oper = scan.nextInt();
                if (oper == 1 || oper == 2) {
                    System.out.println("Enter value: ");
                    val = scan.nextInt();
                    operation(id, oper, val);
                } else if (oper == 3) {
                    operation(id, oper, val);
                } else if (oper > 4) {
                    System.out.println("Invalid Option!");
                }
                if (oper == 4) {
                    break;
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid option: Enter a number!\nStart again!");
            }
        }
    }

        public static void operation ( int id, int oper, int num){
        // arguments supply hostname

        //used try&catch to catch SocketException and IOException
        try {

            Socket clientSocket = null;
            //defining the serverPort as 7777, same as used in the TCPserver
            int serverPort = 7777;

            //initializing the host of the client as the localhost or the same machine
            clientSocket = new Socket("localhost", serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            //defining a string to read id, operation and value; added "," to split the strings
            String nextLine = id + "," + oper + "," + num;
            //prints in the network for the setver
            out.println(nextLine);
            //flushes the output
            out.flush();
            String data = in.readLine(); // read a line of data from the stream
            System.out.println("Message from server: " + data);
            //catching the SocketException and displaying the type of exception message
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            //closing the socket so that the port is released when the server is not needed
            }
        }

}

