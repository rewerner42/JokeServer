/*
18.09.2019
Werner Reineke-Ryskiewicz
CSC 435 - 701
Professor Clark Elliott
HW 2: JokeServer
*/

import java.io.*;   //importin___+g io libraries
import java.net.*;  //networking libraries are imported

class Randomizer extends Thread{
    Socket sock;
    Randomizer(Socket s){
        sock = s;
    }

    public void run(){

    }
}

public class JokeServer{

    static void doWork(String w){
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
            
            new Randomizer(ss.accept()).start(); // the programm waits at this position for an incoming connection only when someone connects will the sofware keep running

            break;
        }
    }
}