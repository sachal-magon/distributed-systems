//UDP server is in the package named project2
package project2;

/*
 * @author Sachal Magon
 * The EchoServerUDP class is the server in the localhost for this task
 * which helps to connect with the client in a menu driven
 * add, subtract and view and it performs all the computation
 * It also handles the exceptions!
 */

//importing the required libraries for the server
import java.net.*;
import java.io.*;
import java.util.HashMap;


//making the server class so that client can request and respond to the server by the Datagram Socket
public class EchoServerUDP{
    //defining the main function of the class
    public static void main(String args[]){
        //printing "Server Running" on the console to check if this program is running
        System.out.println("Server Running");
        //initializing a DatagramSocket object
        DatagramSocket aSocket = null;
        //used try&catch to catch SocketException and IOException
        try{
            //defining the port of the server as 6789 (it can be other numbers as well)
            aSocket = new DatagramSocket(6789);
            HashMap<String, Integer> idSum = new HashMap<String, Integer>();
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
                //Splitting the String by ','
                String[]  array = requestString.split(",");

                String sumString = "";
                //storing the id
                String id = array[0];
                //storing the operation
                String oper = array[1];
                //1 is used for addition
                if(oper.trim().equalsIgnoreCase("1")){
                    //converting number/value to integer
                    int num = Integer.parseInt(array[2]);
                    //defining sum to num for addition calculation
                    int sum = num;
                    //checking if the id is present in hashmap
                    if(idSum.get(id)!=null){
                        //adding the number to the previosus value
                    sum += idSum.get(id);
                    }
                    //puting into the hashmap
                    idSum.put(id,sum);
                    sumString = "OK";
                }
                //same as addition, we just use it for subtraction/2
                if(oper.trim().equalsIgnoreCase("2")){
                    int num = Integer.parseInt(array[2]);
                    int sum = -(num);
                    if(idSum.get(id)!=null){
                        sum = idSum.get(id) + sum;
                    }
                    idSum.put(id,sum);
                    sumString = "OK";
                }
                //in view we just take the value and put the value in the sum
                if(oper.trim().equalsIgnoreCase("3")){
                    int sum = 0;
                    if(idSum.get(id)!=null){
                        sum = idSum.get(id);
                    }
                    sumString = Integer.toString(sum);
                }

                byte[] sumBuffer = sumString.getBytes();
                //creating a new DatagramPacket to reply to the client with the sum
                DatagramPacket reply = new DatagramPacket(sumBuffer, sumBuffer.length,
                        request.getAddress(), request.getPort());
                //send the reply to the client's request
                aSocket.send(reply);
            }
            //catching the SocketException and displaying the type of exception message
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
            //catching the IOException and displaying the type of exception message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
            //closing the socket so that the port is released when the server is not needed
        } finally {if(aSocket != null) aSocket.close();}
    }
}