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
    int jServer;
    static String status = "joke";
    AdminListener(int port){
        portNum = port;
        if(portNum == 5050){
            jServer = 4545;
        }
        else{
            jServer = 4546;
        }
    }

    public static String getStatus(){
        return status;
    }

    private static void swapState(){
        if(status == "joke"){
            status = "proverb";
        }
        else{
            status = "joke";
        }
    }
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(portNum,6);
            while(true){
                ss.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(ss.getInputStream()));
                if(in.readLine().equals("quit")){
                    break;
                }
                swapState();
            }
            ss.close();
        }
        catch(IOException exc){
            System.out.println(exc); // this might catch the socket being closed too early by faulty code. As always catching this exception will be helpful in discovering errors in the code
        }
    }
}

class Randomizer extends Thread{

    Socket sock;
    AdminListener stat;
    static String[] jokeLib = {"I like to hold hands at the movies....for some reason it always seems to startle strangers.","I hate Russian dolls, they're always so full of themselves.","Q: Why couldn't the leopard play hide and seek? - A: Because he was always spottet!","Dentist: You need a crown. - Patient: Finally someone who understands me."};
    static String proverbLib[] = {"Credo, Ergo Sum.","Veni. Vidi. Vici.","Amat Victoria Curam.","Alea Iacta Est."};

    Randomizer(Socket s,AdminListener status){
        sock = s;
        stat = status;
    }

    public void run(){
        try{
            PrintStream out = new PrintStream(sock.getOutputStream()); 
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            try{
                String chocolateChipCookie = in.readLine();
                serve(chocolateChipCookie,out);
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

    private static void serve(String data,PrintStream out){
            if(stat.getState().equals("joke")){
                sendJoke(out,data);
            }
            else{
                sendProverb(out,data);
            }
    }

    private static String [] chooseJoke(String data){
        String[] tempIndex = new String[4]; 
        String[] tempJokes = new String[4];
        String[] index = {"JA","JB","JC","JD"};
        int read = 0;
        int wright = 0;
        for(String i : index){
            if(!data.contains(i)){
                tempJokes[wright]=jokeLib[read];
                tempIndex[wright]=i;
                wright++;
            }
            read++;
        }
        int rand = getRandomNo(wright);
        String[]ret = new String[2];
        if(wright>1){  
            ret[0] = tempJokes[rand];
            ret[1] = tempIndex[rand];
        }
        else{
            ret[0] = tempJokes[rand]+"\nJOKE CYCLE COMPLETED\n";
            ret[1] = tempIndex[rand];
        }
        return ret;
    }
    
    private static String [] chooseProverb(String data){
        String[] tempIndex = new String[4];
        String[] tempProv = new String[4];
        String[] index = {"PA","PB","PC","PD"};
        int read = 0;
        int wright = 0;
        for(String i : index){
            if(!data.contains(i)){
                tempProv[wright]=proverbLib[read];
                tempIndex[wright]=i;
                wright++;
            }
            read++;
        }
        int rand = getRandomNo(wright);
        String[]ret = new String[2];
        if(wright>1){  
            ret[0] = tempProv[rand];
            ret[1] = tempIndex[rand];
        }
        else{
            ret[0] = tempProv[rand]+"\nPROVERB CYCLE COMPLETED\n";
            ret[1] = tempIndex[rand];
        }
        return ret;
    }


    private static void sendJoke(PrintStream out, String data){
        System.out.println("Sending Joke.");
        String[] nData = chooseJoke(data);
        out.println(nData[1]+" ");
        out.println(": " + nData[0]);
    }

    private static void sendProverb(PrintStream out, String data){
        System.out.println("Sending Proverb.");
        String[] nData = chooseProverb(data);
        out.print(nData[1]+" ");
        out.println(": " + nData[0]);
    }

    private static int getRandomNo(int n){
        return (int)(Math.random()*n);
    }
}

public class JokeServer{

    public static void main(String args[]) throws IOException{

        String type;
        int portNo;
        AdminListener controller;

        if(args.length>0 && args[0].toString().equals("secondary")){
            portNo = 4546;
            type = "secondary";
            controller = new AdminListener(5050);
        }
        else if(args.length>0 && !args[0].toString().equals("secondary")){
            System.out.println("Usage: $java JokeServer [secondary] \nTerminating Server.");
            return;
        }
        else{
            portNo = 4545;
            type = "primary";
            controller = new AdminListener(5051);
        }
        System.out.println("\n   Reineke JokeServer 1.0 \n   Now running as a "+type+" Server on Port: "+portNo+".\n");
        
        

        ServerSocket ss = new ServerSocket(portNo,6);
        controller.start();
        
        while(controller.isAlive()){
            new Randomizer(ss.accept(),controller).start(); // the programm waits at this position for an incoming connection only when someone connects will the sofware keep running pass it on the Randomizer who will in turn return a random joke or proverb depending on the clients choice entertain the user to the utmost extent
        }
    }
}