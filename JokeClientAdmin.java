/*--------------------------------------------
1. Name / Date: 
Werner Reineke-Ryskiewicz
18.09.2019

2. Java Version Used: 1.8.0_211-b12

3. you may compile this code as follows: 
$javac JokeClientAdmin.java
$javac *.java
second is usefull if you are compiling many files

4.If you wish to run this particular piece of hardware open a terminal and after comilation type:
$java JokeClientAdmin [ipaddr] [ipaddr]

5. This program switches the JokeServer from Joke to Proverb Mode and from Proverb to Joke Mode.
It can also shutdown the Server.

Please refer the JokeServer.java for information on the Server

If you wish to use the Servers capabilites please refer to the JokeClient.java software.


6. Notes:

This is the JokeClientAdmin

Version 28.9.19

-----------------------------------------------*/

/**We are importing the networking library and Input/Output library for obvious reasons
 * We will neet them for printing to the screen taking input from the user
 * and connecting to the server
 */

import java.io.*;
import java.net.*;

/**
 * The JokeClientAdmin is a useful piece of software that connects to the JokeServer and
 * enables the user to switch from Joke to Proverb Mode. Essentially this tells the Server 
 * what to forward to the clients connected to the server. This states is saved and not 
 * changed unless this client accesses the server and does so. This Client can also shutdown
 * the server remotely and is of corse capable of administrating primary and secondary servers.
 * 
 */

public class JokeClientAdmin{

    /**
     * This toggle function of the JokeCLientAdmin is used to toggle the server from Joke to swap the state from 
     * Joke to Proverb Mode. The Data sent to the Joke Server is evaluated and only in the case that "shutdown" is sent as
     * data to the JokeServer it will power off. In any other case the Server will swap states.
     * @param server this is the server IP/URL that we are connecting to
     * @param port this is the port at which the server is listening to. Because this is an admin channel it will either 
     * be 5050 for primary server and 5051 for a secondary server
     * @param data data is the input of the user that is sent to the client as already stated the server will shutdown
     * if "shutdown" is sent to the Server.
     */
    static void toggle(String server,int port,String data){
        //initializing the data objects
        Socket sock;
        BufferedReader from;
        PrintStream to;
        try{
            // opening the sockets and Streams to the server (establishing a connection to the Server)
            sock = new Socket(server,port);
            from = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            to = new PrintStream(sock.getOutputStream());

            to.println(data); //send the data to the server
            to.flush();

            String incoming;
            incoming = from.readLine(); //read any incoming information
            if(incoming != null){ //only a check in the case the server has shutdown because of our implicit command or otherwise
                System.out.println(incoming);
            }

            sock.close(); //always close the socket as this may cause issues
        }
        catch(IOException exc){
            System.out.println("Socket Error.");
            exc.printStackTrace();
        }
    }

    /**
     * Essentially the beginning of the JokeClientAdmin is the same to the JokeClient software.
     * The main difference being the function of this code which swaps mode of the server and
     * powers off the server and also the server ports. These are chosen as not to impede on the
     * traffic of the regular JokeClients.
     * This Code has 3 steps:
     * 1. assign the primary and secondary server information in order to connect to them at any 
     * given time.
     * 
     * 2.Inform the user of the usage/functions of the programm and the connections that will be 
     * established such as IP and Port of primary and secondary servers.
     * 
     * 3.The third step is the loop that checks for user input switches from primary to secondary
     * server or vice versa. It will also trigger the toggle command for any other input other than quit
     * or s (switches server).
     */
    public static void main(String args[]){

        //defauls values for primary and secondary servers.
        int primaryPort = 5050;
        String primaryServer = "localhost";
        int secondaryPort = 5051;
        String secondaryServer = "localhost";

        int connectToPort;
        String connectToServer;

        /**
         * assigns default values to connected server if no argumens are added to the starting command
         * for one and two arguments they will be assigned to the primary and secondary servrers 
         * respectively.
         */
        if(args.length < 1){ //no arguments
            connectToServer = primaryServer;
            connectToPort = primaryPort;
        }
        else if(args.length < 2){ // one argument
            primaryServer = args[0];
            connectToServer = primaryServer;
            connectToPort = primaryPort;    
        }
        else if (args.length < 3){ // two arguments
            primaryServer = args[0];
            secondaryServer = args[1];
            connectToServer = primaryServer;
            connectToPort = primaryPort;    
        }
        else{ // inform the user of wrong amount of arguments usage
            System.out.println("Usage: $java JokeClientAdmin [ipaddr] [ipaddr]\n\n Terminating Program\n");
            return;
        }

        //Here information is printed to the standard output to inform the user of the Name of the software running,
        //the usage of the server (the commands that can be typed) and the connections that can be established to 
        //primary and secondary servers.

        System.out.println("Werner's Joke Client Admin is running!");
        System.out.println("type [quit] to quit program!");
        System.out.println("Press [Enter] to toggle Joke/Proverb mode.");
        System.out.println("Type [shutdown] to power off Server.");
        System.out.println("Server one: "+ primaryServer + ", port "+primaryPort);
        System.out.println("Server two: "+ secondaryServer + ", port "+secondaryPort+"\n");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String type;
        try{

            do{ // this is the loop that keeps the software running and requests inputs in the manner described at the beginning of the main function
                System.out.flush();
                type = in.readLine();
                if(type.indexOf("quit")<0){
                    if (type.equals("s")){ // "s" command switches from primary to secondary server and vice versa
                        if(connectToPort == primaryPort){
                            connectToPort = secondaryPort;
                            connectToServer = secondaryServer;
                            System.out.println("Now communicating with: "+connectToServer+", port "+connectToPort);
                        }
                        else{
                            connectToPort = primaryPort;
                            connectToServer = primaryServer;
                            System.out.println("Now communicating with: "+connectToServer+", port "+connectToPort);
                        }
                    }
                    else{ // initiating toggle to switch from Joke to Proverb Mode or shutdown Server for "shutdown" command
                        toggle(connectToServer,connectToPort,type);
                    }
                }

            }while(type.indexOf("quit")<0);
            
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }
}