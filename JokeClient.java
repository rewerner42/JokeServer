/*--------------------------------------------
1. Name / Date: 
Werner Reineke-Ryskiewicz
18.09.2019

2. Java Version Used: 1.8.0_211-b12

3. you may compile this code as follows: 
javac JokeClient.java

4.If you wish to run this particular piece of hardware open a terminal and after comilation type:
$java JokeClient [ipaddr] [ipaddr]

5. To run the Client you only require this file. To utilize its functionality you must connecto to a Server
that has the JokeServer software running. Please view JokeServer.java for further instructions on
running this piece of software.

6. Notes:

This is the JokeClient

Version 1.0

-----------------------------------------------*/

/*
including input-output libraries to print information to command line
the networking libraries are obviously for the networking part of this assignment (sockets and such)
*/
import java.io.*;
import java.net.*;

public class JokeClient{

    static void requestJoke(String server,int port, String name){
        Socket sock;
        BufferedReader from;
        PrintStream to;
        String incoming;
        try{
            sock = new Socket(server,port);
            from = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            to = new PrintStream(sock.getOutputStream());

            to.println("Joke please!");
            to.flush();

            int i = 0;
            while(i<3){
                incoming = from.readLine();
                if(incoming!=null){
                    if(incoming.indexOf("J")==0){
                        System.out.print(incoming);
                    }
                    else{
                        System.out.println(incoming);
                    }
                    if( i == 1){
                        System.out.print(name);
                    }
                }
                i++;
            }
            sock.close();
        }
        catch(IOException exc){
            System.out.println("Socket Error.");
            exc.printStackTrace();
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


    public static void main(String args[]){

        int primaryPort = 4545;
        String primaryServer = "localhost";
        int secondaryPort = 4546;
        String secondaryServer = "localhost";

        int connectToPort;
        String connectToServer;

        if(args.length < 1){
            connectToServer = primaryServer;
            connectToPort = primaryPort;
        }
        else if(args.length < 2){
            primaryServer = args[0];
            connectToServer = primaryServer;
            connectToPort = primaryPort;    
        }
        else if (args.length < 3){
            primaryServer = args[0];
            secondaryServer = args[1];
            connectToServer = primaryServer;
            connectToPort = primaryPort;    
        }
        else{
            System.out.println("Usage: $java JokeClient [ipaddr] [ipaddr]\n\n Terminating Program\n");
            return;
        }

        System.out.println("Werner's Joke Client is running!");
        System.out.println("type [quit] to quit program!\n");

        System.out.println("Server one: "+ primaryServer + ", port "+primaryPort);
        System.out.println("Server two: "+ secondaryServer + ", port "+secondaryPort);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));



        String type;
        try{

            System.out.print("Please enter username: ");
            String user = in.readLine();

            do{
                System.out.flush();
                type = in.readLine();
                if(type.indexOf("quit")<0){
                    if (type.equals("s")){
                        if(connectToPort == primaryPort){
                            connectToPort = secondaryPort;
                            connectToServer = secondaryServer;
                            System.out.println("Now communicating with: "+connectToServer+", port "+connectToPort);
                        }
                        else{
                            connectToPort = primaryPort;
                            connectToServer = primaryServer;
                        }
                    }
                    else{
                        requestJoke(connectToServer,connectToPort,user);
                    }
                }

            }while(type.indexOf("quit")<0);
            
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }
}