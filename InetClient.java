/*
18.09.2019
Werner Reineke-Ryskiewicz
CSC 435 - 701
Professor Clark Elliott
HW 1: InetClient
*/

import java.io.*; // imports the io libraries
import java.net.*; // imoprts the networking libraries

public class InetClient{

    public static void main(String args[]){ //main function that runs when program is created
        String serverName; //create a place holder for the name of the server
        if(args.length<1){ // if no arguments are input then use localhost as server
            serverName = "localhost";
        }
        else{//this is the command that uses the first argument as the server
            serverName = args[0];
        }
        System.out.println("Werner Reineke's InetClient 4.2.\n"); //Letting the user know which server version is in use
        System.out.println("Using Server:"+serverName+", Port: 42421"); //Letting the user know which server and port numbers are used

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // creating an input Stream for the users input
        try{
            String name; // placeholder for website address to look up
            do{
                System.out.print("Enter a hostname of an IP address, (quit) to end: ");
                System.out.flush(); // makes sure all data is out
                name = in.readLine(); //get input from the terminal
                if(name.indexOf("quit")<0){
                    getRemoteAddress(name,serverName); // invoke getRemoteAddress to contact server for address information from terminal input
                }
            }while(name.indexOf("quit")<0); //repeat the do loop until "quit" is typed into the input
            System.out.println("Cancelled by user request."); //before exiting programm inform user that the programm was cancelled on his or her request
        }catch(IOException x){ // catch exceptions in the code sothat the programm doesn't crash
            x.printStackTrace(); // print the exception to output 
        }
    }

    static String toText (byte ip[]) { //creates a 128 bit format
        StringBuffer result = new StringBuffer (); //create a buffer to save the result
        for (int i = 0; i < ip.length; ++ i) {//transforms 32 bit IP into 128 bit format to the buffer
            if (i > 0) result.append (".");
            result.append (0xff & ip[i]);
        }
        return result.toString (); //returns buffer
    }

    static void getRemoteAddress(String name, String serverName){
        // declare the objects used in this function
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;
        
        try{ //try following code and catch errors which might happen while connecting to the server
            sock = new Socket(serverName, 42421); //initialize the socket to our InetServer with specified port
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream())); // initialize a buffer from the input stream of the socket
            toServer = new PrintStream(sock.getOutputStream()); //initialize the stream for information that is sent to the server
            toServer.println(name); //send the website that we wish to look up
            toServer.flush(); // flush the buffer to make sure all data is out of the buffer

            for(int i = 1; i <= 3;i++){ //read three line of input from the server
                textFromServer = fromServer.readLine(); //reading the line
                if(textFromServer != null){ // checking if the information is null
                    System.out.println(textFromServer); // if it is not then print data to terminal
                }
            }
            sock.close(); // close the socket and therefore the connection to the server
        }
        catch(IOException x){ // this is where we catch the errors which will most likely be socket errors in the case of a lost connection, closed socket etc.
            System.out.println("Socket Error.");
            x.printStackTrace();
        }
    }
}