//TCP client is in the package named project2
package project2;

/*
 * @author Sachal Magon
 * The EchoClientTCP class is the client in the localhost for this task
 * which helps to connect with the server in a menu driven
 * add, subtract and view
 * It uses RSA encryption as well as hashing, signing and verifying
 * To check if the correct client is sending to the server
 * It also handles the exceptions!
 */

//importing the required libraries for the client
import java.net.*;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.math.BigInteger;
import java.util.Random;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//defining a class for the client to communicate with the server through UDP Datagram Sockets
public class EchoClientTCP {
    //defining the main function of the class
    public static void main (String args[]) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // args give message contents and server hostname
        //printing "Client Running" on the console to check if this program is running
        System.out.println("Client Running");
        //calls RSA and recieves public and private key
        String pK = RSA();
        //splits d,e,n for public and private keys
        String [] keys = pK.split(",");
        //stores d,e,n
        String e = keys[0], d = keys[1], n = keys[2];
        //makes public key using e and n
        String pubK = e + n;
        //sends public key for hashing
        String id = babyHash(pubK);
        //run the while loop until the nextLine is null
        while (true) {
            //Enter data using BufferReader
            int val = 0;
            Scanner scan = new Scanner(System.in);
            // Reading data using readLine
            //menu driven for add, subtract and view like before
            try {
                System.out.println("Enter number for operation:\n1 - add\n2 - subtract\n3 - view\n4 - exit");
                int oper = scan.nextInt();
                if (oper == 1 || oper == 2) {
                    System.out.println("Enter value: ");
                    val = scan.nextInt();
                    //sends in the proxy
                    operation(id, oper, val, d, e, n);
                } else if (oper == 3) {
                    //sends in the proxy
                    operation(id, oper, val, d, e, n);
                } else if (oper > 4) {
                    System.out.println("Invalid Option!");
                }
                if (oper == 4) {
                    System.out.println("Client Exiting... Server still running!");
                    break;
                }
                //handles exceptions
            } catch (InputMismatchException e2) {
                System.out.println("Invalid option: Enter a number!\nStart again!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void operation (String id, int oper, int num, String d, String e, String n) throws Exception {
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
            String nextLine = id + "," + oper + "," + num + "," + e + "," + n;
            //converts into bigInteger for computation of sign
            BigInteger bigD = new BigInteger(d);
            BigInteger bigN = new BigInteger(n);
            //calls sign to make the signature of the message
            String signature = sign(nextLine, bigD, bigN);
            String signedMessage = nextLine + "," + signature;

            //sends the message with the signature
            out.println(signedMessage);
            out.flush();
            String data = in.readLine(); // read a line of data from the stream
            System.out.println("Message from server: " + data);
            //catching the SocketException and displaying the type of exception message
        } catch (IOException | NoSuchAlgorithmException e1) {
            System.out.println("IO: " + e1.getMessage());
            //closing the socket so that the port is released when the server is not needed
        }
    }

    /**
     *  RSA Algorithm from CLR
     *
     * 1. Select at random two large prime numbers p and q.
     * 2. Compute n by the equation n = p * q.
     * 3. Compute phi(n)=  (p - 1) * ( q - 1)
     * 4. Select a small odd integer e that is relatively prime to phi(n).
     * 5. Compute d as the multiplicative inverse of e modulo phi(n). A theorem in
     *    number theory asserts that d exists and is uniquely defined.
     * 6. Publish the pair P = (e,n) as the RSA public key.
     * 7. Keep secret the pair S = (d,n) as the RSA secret key.
     * 8. To encrypt a message M compute C = M^e (mod n)
     * 9. To decrypt a message C compute M = C^d (mod n)
     * @return
     */

    public static String RSA() {

            // Each public and private key consists of an exponent and a modulus
            BigInteger n; // n is the modulus for both the private and public keys
            BigInteger e; // e is the exponent of the public key
            BigInteger d; // d is the exponent of the private key

            Random rnd = new Random();

            // Step 1: Generate two large random primes.
            // We use 400 bits here, but best practice for security is 2048 bits.
            // Change 400 to 2048, recompile, and run the program again and you will
            // notice it takes much longer to do the math with that many bits.
            BigInteger p = new BigInteger(400,100,rnd);
            BigInteger q = new BigInteger(400,100,rnd);

            // Step 2: Compute n by the equation n = p * q.
            n = p.multiply(q);

            // Step 3: Compute phi(n) = (p-1) * (q-1)
            BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

            // Step 4: Select a small odd integer e that is relatively prime to phi(n).
            // By convention the prime 65537 is used as the public exponent.
            e = new BigInteger ("65537");

            // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
            d = e.modInverse(phi);

        return e + "," + d + "," + n;
    }

    // BabyHash is a program to read any input and compute a SHA-256 hash.
    // The program then displays 20 hex-digits taken from the
    // leftmost 2 bytes of the SHA-256 Hash.

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

    /**
     * Signing proceeds as follows:
     * 1) Get the bytes from the string to be signed.
     * 2) Compute a SHA-1 digest of these bytes.
     * 3) Copy these bytes into a byte array that is one byte longer than needed.
     *    The resulting byte array has its extra byte set to zero. This is because
     *    RSA works only on positive numbers. The most significant byte (in the
     *    new byte array) is the 0'th byte. It must be set to zero.
     * 4) Create a BigInteger from the byte array.
     * @param message a sting to be signed
     * @return a string representing a big integer - the encrypted hash.
     * @throws Exception
     */
    public static String sign(String message, BigInteger d, BigInteger n) throws Exception {

        // compute the digest with SHA-256
        byte[] bytesOfMessage = message.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        // we only want two bytes of the hash for BabySign
        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        int length_ = bigDigest.length + 1;
        byte[] messageDigest = new byte[length_];

        messageDigest[0] = 0;
        int i=1;
        while(i<length_)
        {
            messageDigest[i]=bigDigest[i-1];
            i++;
        }
        // The message digest now has three bytes. Two from SHA-256
        // and one is 0.

        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);

        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);

        // return this as a big integer string
        return c.toString();
    }
}