/*--------------------------------------------
1. Name / Date: 
Werner Reineke-Ryskiewicz
18.09.2019

2. Java Version Used: 1.8.0_211-b12

3. you may compile this code as follows: 
javac JokeServer.java

4.If you wish to run this particular piece of hardware open a terminal and after comilation type:
$java JokeServer [secondary]

5. To run the Server you only require this file. However, to use its services you require the Client.
How to specifically use this program please read JokeClient.java.

6. Notes:

This is the JokeServer

Version 1.0

-----------------------------------------------*/

import java.io.*;
import java.net.*;

public class JokeClientAdmin{

    static void toggle(String server,int port,String data){
        Socket sock;
        BufferedReader from;
        PrintStream to;
        try{
            sock = new Socket(server,port);
            from = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            to = new PrintStream(sock.getOutputStream());

            to.println(data);
            to.flush();

            String incoming;
            incoming = from.readLine();
            System.out.println(incoming);

            sock.close();
        }
        catch(IOException exc){
            System.out.println("Socket Error.");
            exc.printStackTrace();
        }
    }

    public static void main(String args[]){
        int primaryPort = 5050;
        String primaryServer = "localhost";
        int secondaryPort = 5051;
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
            System.out.println("Usage: $java JokeClientAdmin [ipaddr] [ipaddr]\n\n Terminating Program\n");
            return;
        }

        System.out.println("Werner's Joke Client Admin is running!");
        System.out.println("type [quit] to quit program!\n");
        System.out.println("Press [Enter] to toggle Joke/Proverb mode.");
        System.out.println("Type [shutdown] to power off Server.");

        System.out.println("Server one: "+ primaryServer + ", port "+primaryPort);
        System.out.println("Server two: "+ secondaryServer + ", port "+secondaryPort);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String type;
        try{

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
                            System.out.println("Now communicating with: "+connectToServer+", port "+connectToPort);
                        }
                    }
                    else{
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