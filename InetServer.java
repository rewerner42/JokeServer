/*
18.09.2019
Werner Reineke-Ryskiewicz
CSC 435 - 701
Professor Clark Elliott
HW 1: InetServer
*/

import java.io.*;   //this imports the input and output libraries
import java.net.*;  //this imports the libraries for networking

class Worker extends Thread{    //The Worker Class begins here
    Socket sock; // This is the forward declaration of the class attribute Socket
    Worker (Socket s) { // THis is the Worker Class constructor
        sock = s; // this passes the Socket s to the class attribute sock
    }

    public void run(){ //This is the workerThread's main function that runs when the Class is run
        PrintStream out = null; 
        BufferedReader in = null; // The two previous lines are the initialisation of the input and output streams
        try{ //use try-catch in case the input incase there was an error with the connection
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));//assigning the input stream to the buffer
            out = new PrintStream(sock.getOutputStream()); // assigning the output stream to the output print stream
            try{ // use try-catch in case reading from the input stream caused an error
                String addr = in.readLine(); //this statement reads in the the address passed along from the client
                System.out.println("Looking up: "+addr); //prints what address is going to be looked up
                printRemoteAddress(addr,out); //this calls the printRemoteAdress function defined below (in short: it looks up the address and prints it with some statements to make it look nice)
            }catch(IOException x){ // catch and print error
                System.out.println("Server read Error!");
                x.printStackTrace();
            }
            sock.close();//close the connection
        }catch (IOException ioe){
            System.out.println(ioe);//print error
        }
    }

    static void printRemoteAddress(String ad, PrintStream out){ // this function prints the remote address
        try{
            out.println("Looking up "+ ad + "..."); // makes it look nice and tells user what its doing
            InetAddress machine = InetAddress.getByName(ad); // look up the IP of the website searched for
            out.println("Host Name: "+machine.getHostName()); // looks up the name of the IP just looked up
            out.println("Host IP: "+toText(machine.getAddress())); // print the IP address to the outstream
        }catch(UnknownHostException exc){
            out.println("Failed to attempt to look up: " + ad); //catches exception in case there was an error looking up the website
        }
    }

    static String toText (byte ip[]) { /* Make portable for 128 bit format */
        StringBuffer result = new StringBuffer (); //create a buffer to save the result
        for (int i = 0; i < ip.length; ++ i) {//transforms 32 bit IP into 128 bit format to the buffer
            if (i > 0) result.append (".");
            result.append (0xff & ip[i]);
        }
        return result.toString (); //returns buffer
      }
}

public class InetServer{
    public static void main(String a[])throws IOException{ // this is the main function that runs automatically when the InetServer is started
        int sim_conn = 6; // This variable declares how many simulataneous connections can be established
        int port = 42421; // This is the port number on which the Server will be listening to Connections
        Socket sock; // This is a forward declaration of the Socket that will be used to accept incoming connections from clients

        ServerSocket serverSock = new ServerSocket(port,sim_conn);  // This declares and initializes the ServerSocket at the defined 
                                                                    //port while defining "sim_conn" as the amount of connections that can simultaneously be established  

        System.out.println("Werner Reineke's Inet server 4.2 starting up, listening at port 42421.\n");
        while(true){ // This while loop waits for incomming connections and runs until the server is shut down
            sock = serverSock.accept(); // This is where the server waits for the incoming connection
            new Worker(sock).start();   // at this point the server starts an instance of the Worker Class to retrieve the 
                                        // website searched for and returns the IP address to the client
        }
    }
}