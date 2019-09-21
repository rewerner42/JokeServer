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

import java.io.*;   // importing the java input and output libraries will help displaying text to the termianl and also taking inputs from the user
import java.net.*;  //networking libraries packages are usefull for any types of networking and mainly for transfering data from this server to the client and recieving the requests sent by it
import java.math.*; // because this server is required to send random jokes we will use the random function from the math library to generate quasi-random numbers

class AdminListener extends Thread{
    int portNum;
    AdminListener(int port){
        portNum = port;
    }

    public void run(){

    }
}

class Randomizer extends Thread{

    Socket sock;
    static String[] jokeLib = {"I like to hold hands at the movies....for some reason it always seems to startle strangers.","I hate Russian dolls, they're always so full of themselves.","Q: Why couldn't the leopard play hide and seek? - A: Because he was always spottet!","Dentist: You need a crown. - Patient: Finally someone who understands me."};
    static String proverbLib[] = {"Credo, Ergo Sum.","Veni. Vidi. Vici.","Amat Victoria Curam.","Alea Iacta Est."};

    Randomizer(Socket s){
        sock = s;
    }

    public void run(){
        try{
            PrintStream out = new PrintStream(sock.getOutputStream()); 
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            try{
                String requestType = in.readLine();
                serve("joke",out);
            }
            catch(IOException read){
                System.out.println("Server read error has occurred!"); // informing the user what type of error has occurred and where to find it
                read.printStackTrace(); //this statement will be useful for debugging in the case that out code creates a read error and where to find the mistakes
            }
            sock.close();
        }
        catch(IOException exc){
            System.out.println(exc); // this might catch the socket being closed too early by faulty code. As always catching this exception will be helpful in discovering errors in the code
        }

    }

    private static void serve(String jokeOrPro,PrintStream out){
            if(jokeOrPro.equals("joke")){
                sendJoke(out);
            }
            else{
                sendProverb(out);
            }
    }

    private static void sendJoke(PrintStream out){
        System.out.println("Sending Joke.");
        out.println("You requested a Joke:");
        int jokeLetter = getRandomNo(4);
        out.println("J"+(char)(jokeLetter+65)+" ");
        out.println(" " + jokeLib[jokeLetter]);
    }

    private static void sendProverb(PrintStream out){
        System.out.println("Sending Proverb.");
        out.println("You requested a Proverb:");
        int provLetter = getRandomNo(4);
        out.print("P"+(char)(provLetter+65)+" ");
        out.println(" " + proverbLib[provLetter]);
    }

    private static int getRandomNo(int n){
        return (int)(Math.random()*n);
    }
}

public class JokeServer{

    private static void doWork(String w){
        System.out.println("hello mofo" + w);
    }
    public static void main(String args[]) throws IOException{

        String type;
        int portNo;

        if(args.length>0 && args[0].toString().equals("secondary")){
            portNo = 4546;
            type = "secondary";
        }
        else if(args.length>0 && !args[0].toString().equals("secondary")){
            System.out.println("Usage: $java JokeServer [secondary] \nTerminating Server.");
            return;
        }
        else{
            portNo = 4545;
            type = "primary";
        }
        System.out.println("\n   Reineke JokeServer 1.0 \n   Now running as a "+type+" Server on Port: "+portNo+".\n");
        
        ServerSocket ss = new ServerSocket(portNo,6);

        while(true){
            new Randomizer(ss.accept()).start(); // the programm waits at this position for an incoming connection only when someone connects will the sofware keep running pass it on the Randomizer who will in turn return a random joke or proverb depending on the clients choice entertain the user to the utmost extent
        }
    }
}