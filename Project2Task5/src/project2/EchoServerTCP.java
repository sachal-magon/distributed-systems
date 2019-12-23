//TCP server is in the package named project2
package project2;

/*
 * @author Sachal Magon
 * The EchoServerTCP class is the server in the localhost for this task
 * which helps to connect with the client in a menu driven
 * add, subtract and view and it performs all the computation
 * It uses RSA encryption as well as hashing, signing and verifying
 * To check if the correct client is sending to the server
 * It also handles the exceptions!
 */

//importing the required libraries for the server
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.HashMap;

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
                System.out.println("Echoing: " + data);

                //Splitting the String by ','
                String[]  array = data.split(",");
                String sumString = "";
                //storing the id
                String id = array[0];
                //storing the operation
                String oper = array[1];
                String e = array[3];
                String n = array[4];
                String sign = array[5];
                //verify if the signature is valid
                boolean check1 = verify(array[0]+","+array[1]+","+array[2]+","+array[3]+","+array[4],sign, e , n);
                //verify if the id is valid and sent by the client
                String idCheck = babyHash(e+n);
                //compute only if the 2 above checks are valid
                if(idCheck.equalsIgnoreCase(id) && check1) {
                    if (oper.trim().equalsIgnoreCase("1")) {
                        int num = Integer.parseInt(array[2]);
                        int sum = num;
                        if (idSum.get(id) != null) {
                            sum += idSum.get(id);
                        }
                        idSum.put(id, sum);
                        sumString = "OK";
                    }
                    if (oper.trim().equalsIgnoreCase("2")) {
                        int num = Integer.parseInt(array[2]);
                        int sum = -(num);
                        if (idSum.get(id) != null) {
                            sum = idSum.get(id) + sum;
                        }
                        idSum.put(id, sum);
                        sumString = "OK";
                    }
                    if (oper.trim().equalsIgnoreCase("3")) {
                        int sum = 0;
                        if (idSum.get(id) != null) {
                            sum = idSum.get(id);
                        }
                        sumString = Integer.toString(sum);
                    }

                    //send the reply to the client's request
                    out.println(sumString);
                    out.flush();
                }
            }

            //catching the SocketException and displaying the type of exception message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
            //closing the socket so that the port is released when the server is not needed
        } catch (Exception e) {
            e.printStackTrace();
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
    public static boolean verify(String messageToCheck, String encryptedHashStr, String E, String N)throws Exception  {

        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedHashStr);
        // Decrypt it
        BigInteger e = new BigInteger(E);
        BigInteger n = new BigInteger(N);
        BigInteger decryptedHash = encryptedHash.modPow(e, n);

        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");

        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);

        // messageToCheckDigest is a full SHA-256 digest
        // take two bytes from SHA-256 and add a zero byte
        int length_ = messageToCheckDigest.length + 1;
        byte[] extraByte = new byte[length_];

        extraByte[0] = 0;
        int i=1;
        while(i<length_)
        {
            extraByte[i]=messageToCheckDigest[i-1];
            i++;
        }

        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);

        // inform the client on how the two compare
        if(bigIntegerToCheck.compareTo(decryptedHash) == 0) {

            return true;
        }
        else {
            return false;
        }
    }
    public static String babyHash(String inputString)throws NoSuchAlgorithmException, UnsupportedEncodingException {

        inputString = inputString.toLowerCase();

        String hash = ComputeSHA_256_as_Hex_String(inputString);

        String babyHash = hash.substring(hash.length()-20);
        return babyHash;
    }
    public static String ComputeSHA_256_as_Hex_String(String text) {

        try {
            // Create a SHA256 digest
            MessageDigest digest;
            digest = MessageDigest.getInstance("SHA-256");
            // allocate room for the result of the hash
            byte[] hashBytes;
            // perform the hash
            digest.update(text.getBytes("UTF-8"), 0, text.length());
            // collect result
            hashBytes = digest.digest();
            return convertToHex(hashBytes);
        }
        catch (NoSuchAlgorithmException nsa) {
            System.out.println("No such algorithm exception thrown " + nsa);
        }
        catch (UnsupportedEncodingException uee ) {
            System.out.println("Unsupported encoding exception thrown " + uee);
        }
        return null;
    }
    // code from Stack overflow
    // converts a byte array to a string.
    // each nibble (4 bits) of the byte array is represented
    // by a hex characer (0,1,2,3,...,9,a,b,c,d,e,f)
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

}