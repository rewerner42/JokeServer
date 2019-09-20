/*
18.09.2019
Werner Reineke-Ryskiewicz
CSC 435 - 701
Professor Clark Elliott
HW 2: 

JokeServer

Version 1.0

you may compile this code as follows: 
javac JokeServer.java

If you wish to run this particular piece of hardware open a terminal and after comilation type:
$java JokeServer [secondary]

To run the Server you only require this file. However, to use its services you require the Client.
How to specifically use this program please read JokeClient.java.

Notes:
---


*/

import java.io.*;   // importing the java input and output libraries will help displaying text to the termianl and also taking inputs from the user
import java.net.*;  //networking libraries packages are usefull for any types of networking and mainly for transfering data from this server to the client and recieving the requests sent by it
import java.math.random; // because this server is required to send random jokes we will use the random function from the math library to generate quasi-random numbers


class Randomizer extends Thread{

    Socket sock;
    static String jokeLib[] = new String [4];
    jokeLib[0] = "Dentist: You need a crown. - Patient: Finally someone who understands me.";
    jokeLib[1] = "Q: Why couldn't the leopard play hide and seek? - A: Because he was always spottet!";
    jokeLib[2] = "I hate Russian dolls, they're always so full of themselves.";
    jokeLib[3] = "I like to hold hands at the movies....for some reason it always seems to startle strangers.";
    static String proverbLib[] = new String [4];
    proverbLib[0] = "Alea Iacta Est.";
    proverbLib[1] = "Amat Victoria Curam.";
    proverbLib[2] = "Veni. Vidi. Vici.";
    proverbLib[3] = "Credo, Ergo Sum.";

    Randomizer(Socket s){
        sock = s;
    }

    public void run(){
        try{
            PrintStream out = new PrintStream(sock.getOutputStream()); 
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            try{
                String requestType = in.readLine();
                System.out.println("Generating very amazing "+requestType+"!...");
                serve(requestType,out);
            }
            catch(IOException read){
                System.out.println("Server read error has occurred!"); // informing the user what type of error has occurred and where to find it
                x.printStackTrace(); //this statement will be useful for debugging in the case that out code creates a read error and where to find the mistakes
            }
            sock.close();
        }
        catch(IOException exc){
            System.out.println(exc); // this might catch the socket being closed too early by faulty code. As always catching this exception will be helpful in discovering errors in the code
        }

    }

    private static void serve(String jokeOrPro,PrintStream out){
        if(jokeOrPro.eqauls("joke")){
            sendJoke(out);

        }
        else{
            sendProverb(out);
        }
    }

    private static void sendProverb(PrintStream out){
        out.println("You requested a Joke:");
        out.println(jokeLib[getRandomNo(4)]);
    }

    private static void sendJoke(PrintStream out){
        out.println("You requested a Proverb:");
        out.println(proverbLib[getRandomNo(4)]);
    }

    private static String getRandomNo(int n){
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
            System.out.println("Waiting for incomming request....");
            
            new Randomizer(ss.accept()).start(); // the programm waits at this position for an incoming connection only when someone connects will the sofware keep running pass it on the Randomizer who will in turn return a random joke or proverb depending on the clients choice entertain the user to the utmost extent

            break;
        }
    }
}