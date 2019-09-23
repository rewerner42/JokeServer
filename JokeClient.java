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

    private static String cookie = "";

    private static void polishCookie(String whatToClean){
        String temp = "";
        String [] jokes = {"JA","JB","JC","JD"};
        String [] prov = {"PA","PB","PC","PD"};
        String[] cleaning = new String[4];
        if(whatToClean == "PROVERB"){
            cleaning = jokes;
        }
        else{
            cleaning = prov;
        }
        
        for(String i: cleaning){
            if(cookie.contains(i)){
                temp+=i;
            }
        }

        cookie = temp;
    }

    static void requestJoke(String server,int port, String name){
        Socket sock;
        BufferedReader from;
        PrintStream to;
        String incoming;
        try{
            sock = new Socket(server,port);
            from = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            to = new PrintStream(sock.getOutputStream());

            to.println(cookie);
            to.flush();

            int i = 0;
            while(i<4){
                incoming = from.readLine();
                if(incoming!=null){
                    if(incoming.equals("JOKE CYCLE COMPLETED")){
                        polishCookie("JOKE");
                        System.out.println(incoming);
                    }
                    else if(incoming.equals("PROVERB CYCLE COMPLETED")){
                        polishCookie("PROVERB");
                        System.out.println(incoming);
                    }
                    else if(incoming.indexOf("J")==0 || incoming.indexOf("P")==0){
                        System.out.print(incoming+name);
                        cookie += incoming;
                    }
                    else{
                        System.out.println(incoming);
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
                System.out.print("[Press Enter for request.]");
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