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

class AdminClient{

    int primaryPort = 5050;
    String primaryServer = "localhost";
    int secondaryPort = 5051;
    String secondaryServer = "localhost";

    int connectToPort;
    String connectToServer;

    private static void changeState(){
        try{
            sock = new Socket(server,port);
            from = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            to = new PrintStream(sock.getOutputStream());

            
        }
        catch(IOException exc){
            System.out.println("Socket Error.");
            exc.printStackTrace();
        }

    }

    public static void main(String args []){
        Socket sock;
        BufferedReader from;
        PrintStream to;
        String incoming;



        while(true){

        }
    }
}