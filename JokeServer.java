/*--------------------------------------------
1. Name / Date: 
Werner Reineke-Ryskiewicz
18.09.2019

2. Java Version Used: 1.8.0_211-b12

3. you may compile this code as follows: 
javac JokeServer.java
javac *.java
second is usefull if you are compiling many files

4.If you wish to run this particular piece of hardware open a terminal and after comilation type:
$java JokeServer [secondary]

5. To run the Server you only require this file. However, to use its services you require the Client.
How to specifically use this program please read JokeClient.java.

For administration purposes please use JokeClientAdmin.java

6. Notes:

This is the JokeServer

Listening Ports:
(depending on Server type)

Administration:
-Primary: 5050
-Secondary: 5051

Clients:
-Primary: 4545
-Secondary: 4546

Version 1.0

-----------------------------------------------*/

import java.io.*;   // importing the java input and output libraries will help displaying text to the termianl and also taking inputs from the user
import java.net.*;  //networking libraries packages are usefull for any types of networking and mainly for transfering data from this server to the client and recieving the requests sent by it
import java.sql.Struct;
import java.math.*; // because this server is required to send random jokes we will use the random function from the math library to generate quasi-random numbers

/**
 * Simple Class which extends the thread and is only implemented to connect to the main JokeServer
 * and stop it from waiting for requests form JokeClients when shutting down. This happens when the Adminlistener
 * recieves the shutdown command form the JokeClientAdmin to terminate the JokeServer who is in tern listening to the 
 * AdminListener and terminates when it is not running anymore. So all this class does is release the JokeServer from listeining 
 * for incoming requests.
 */
class Closer extends Thread{
    int closerPort ;
    Closer(int port){ //handing over port used by JokeClient of either primary or secondary server for shutdown
        closerPort = port;
    }

    public void run(){
       try{ // chekcing for socket errors and then connecting to server for release
            Socket sock = new Socket("localhost",closerPort);
            PrintStream out = new PrintStream(sock.getOutputStream());
            out.println("off");
            sock.close(); // but still closing the socket
        }
        catch(IOException exc){
            System.out.println("Socket Error.");
            exc.printStackTrace();
        }
    }
}
/**
 * This is the code that is brought to life by the JokeServer itself and listens on either port 5050 or 5051
 * for incoming connections by the JokeClientAdmin to either toggle from Joke to Proverb Mode or shutdown the Server
 * The Randomzier class has a reference to this Thread sothat it can call the "Status" which is the mode (Joke/Proverb) and 
 * the server type which is important for the <S2> output when connecting to a secondary server
 */
class AdminListener extends Thread{
    
    int portNum; // port that the Listener will be listening for incoming connections from JokeClient Admin
    int jServer; // port for the Closer so that we JokeServer can be shut down
    String serverType; // primary or secondary server mode (not really necessary but nice for the programmer)
    static String status = "Joke"; // inital mode is the joke Mode

    AdminListener(int port){ //assinging values on start up depending on Server type
        portNum = port;
        if(portNum == 5050){
            jServer = 4545;
            serverType = "prime";
        }
        else{
            jServer = 4546;
            serverType = "sec";
        }
    }
    
    // simple get method to return the status of the Listener and tell the Randomizer whether he needs to return Jokes or Proverbs
    public String getStatus(){
        return status;
    }

    //simple get method to return the ServerType to the Randomizer so that the Randomizer knows to add <S2> when running in secondary modus
    public String getServerType(){
        return serverType;
    }

    // swap state method is to swap the state from Joke to Proverb and vice versa
    private static void swapState(){
        if(status == "Joke"){
            status = "Proverb";
        }
        else{
            status = "Joke";
        }
        System.out.println("Swapping to "+status+" mode."); //informing user of the swap
    }

    /**
     * Run method from the Thread that starts when the thread is started. We create a new ServerSocket at the port we are listening at
     * (primary or seconday) and then either switch for any input sent by the JokeClientAdmin or shutdown for the shutdown command. If the 
     * shutdown command is called we also call a new Closer Thread and start it sothat the JokeServer loop doesn't wait for new incomming
     * connections.
     */
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(portNum,6);
            while(true){
                Socket temp = ss.accept(); //here wait for new incoming connection
                BufferedReader in = new BufferedReader(new InputStreamReader(temp.getInputStream()));
                if(in.readLine().equals("shutdown")){ //inform the Users viewing the JokeServer of the JokeClientAdmin's Input and break the loop
                    System.out.println("Shutting Down Joke Server.");
                    break;
                }
                PrintStream out = new PrintStream(temp.getOutputStream()); 
                swapState();
                out.println(status + " Mode.");//Inform the User of the JokeClientAdmin that the Mode was switched between Joke and Proverb Mode
            }
            ss.close();
        }
        catch(IOException exc){
            System.out.println(exc); // this might catch the socket being closed too early by faulty code. As always catching this exception will be helpful in discovering errors in the code
        }
        new Closer(jServer).start(); // start the Closer so the JokeServer doesn't continue to wait for incoming connections.
    }
}

/**
 * The Randomizer class extends the Thread class and is the class that sends the Jokes and Proverbs back and forth after recieving a cookie
 * from the JokeClient. Put in words this Thread recieves the Socket from the JokeServer Class and with the cookie - determining which Jokes and Proverbs have
 * already been sent to the Client - and the controller(an instance of the Administrator class) - which determins wether the Server is in Joke or Proverb Mode
 * - and sends a random Joke or Proverb to the Client in a special manner to 
 */

class Randomizer extends Thread{

    Socket sock;
    static AdminListener stat;

    // All Jokes were taken from ReaderDigest (rd.com). Some of these Jokes I would say are common knowledge but really it is hard to say who invented them
    static String[] jokeLib = {"I like to hold hands at the movies....for some reason it always seems to startle strangers.","I hate Russian dolls, they're always so full of themselves.","Q: Why couldn't the leopard play hide and seek? - A: Because he was always spottet!","Dentist: You need a crown. - Patient: Finally someone who understands me."};
    // Proverb quotes are taken from Gaius Julius Ceaser, the first Roman Emperor at various times in his life. I took Latin in High School and did something called the "Latinum" 
    // it means something like Latin proficiency so unfortunately they are burnt into my mind...
    static String proverbLib[] = {"Credo, Ergo Sum.","Veni. Vidi. Vici.","Amat Victoria Curam.","Alea Iacta Est."};
    static String serverType;
    Randomizer(Socket s,AdminListener status){
        sock = s;
        stat = status;
        serverType = stat.getServerType();
    }

    public void run(){
        try{
            PrintStream out = new PrintStream(sock.getOutputStream()); 
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            try{
                String chocolateChipCookie = in.readLine();
                if(!chocolateChipCookie.equals("off")){
                    serve(chocolateChipCookie,out);
                }
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

    /**
     * GENERAL INFORMATION on the following funtions.
     * This has all been done to make my code unique. That is to say that I am aware there are simpler ways to do this but
     * chose to do it my way.
     */

     
    /**
     * server is a simple function called by the run fucntion of the thread and is meant to start the "serving", like in a restaurant, 
     * of Jokes or Proverbs. In essence it decides wether a Joke or Proverb is to be sent.
     * @param data is the passingo on of the cookie that came from the Client
     * @param out out is the outputstream that will be used to send data to the Client
     */
    private static void serve(String data,PrintStream out){
            if(stat.getStatus().equals("Joke")){
                sendJoke(out,data);
            }
            else{
                sendProverb(out,data);
            }
    }

    /**
     * Magic choosing of the Joke!
     * using temporary String we will choose the Jokes that have not been seen by the client.
     * Then at random choose a  Joke from this set and return a String array with the Index such as
     * "PA" and the Joke. We also inform the Client if the cycle has been completed if this is the last
     * Joke from the Joke Library.
     * @param data this is cookie 
     * @return we return an array with [0] being the index and [1] being the actual Joke
     */
    private static String [] chooseJoke(String data){
        String[] tempIndex = new String[4]; 
        String[] tempJokes = new String[4];
        String[] index = {"JA","JB","JC","JD"};
        int read = 0; // two counter one for reading from the JokeLibrary and the other for writing to the library to choose from.
        int wright = 0;
        for(String i : index){ // choose all Jokes not sent and put in String array tempProv
            if(!data.contains(i)){
                tempJokes[wright]=jokeLib[read];
                tempIndex[wright]=i;
                wright++;
            }
            read++;
        }
        int rand = getRandomNo(wright);// choose a random joke from the set
        String[]ret = new String[2];
        if(wright>1){  // if the cycle is finished inform CLient of the finished cycle otherwise just send index and joke
            ret[0] = tempJokes[rand];
            ret[1] = tempIndex[rand];
        }
        else{
            ret[0] = tempJokes[rand]+"\nJOKE CYCLE COMPLETED\n";
            ret[1] = tempIndex[rand];
        }
        return ret; 
    }
    /**
     * Magic choosing of the Proverb!
     * using temporary String we will choose the Proverbs that have not been seen by the client.
     * Then at random choose a  Proverb from this set and return a String array with the Index such as
     * "PA" and the Proverb. We also inform the Client if the cycle has been completed if this is the last
     * Proverb from the Proverb Library.
     * @param data this is cookie 
     * @return we return an array with [0] being the index and [1] being the actual Proverb
     */
    private static String [] chooseProverb(String data){
        String[] tempIndex = new String[4];
        String[] tempProv = new String[4];
        String[] index = {"PA","PB","PC","PD"};
        int read = 0;  // two counter one for reading from the proverbLibrary and the other for writing to the library to choose from.
        int wright = 0;
        for(String i : index){ // choose all Proverbs not sent and put in String array tempProv
            if(!data.contains(i)){
                tempProv[wright]=proverbLib[read];
                tempIndex[wright]=i;
                wright++;
            }
            read++;
        }
        int rand = getRandomNo(wright); // choose a random proverb from the set
        String[]ret = new String[2];
        if(wright>1){  // if the cycle is finished inform CLient of the finished cycle otherwise just send index and proverb
            ret[0] = tempProv[rand];
            ret[1] = tempIndex[rand];
        }
        else{
            ret[0] = tempProv[rand]+"\nPROVERB CYCLE COMPLETED\n";
            ret[1] = tempIndex[rand];
        }
        return ret;
    }

    /**
     * This will send a Joke to the client while calling the chooseJoke function to choose the actual Joke
     * The sending occurs in a special way to esure the output is as desired
     * @param out
     * @param data
     */
    private static void sendJoke(PrintStream out, String data){
        System.out.println("Sending Joke.");
        String[] nData = chooseJoke(data); // will choose a Joke depending on the cookie
        if(serverType.equals("sec")){//if it is a secondary server send <S2> before every message
            out.println("<S2>"+nData[1]+" ");   // The Information is sent in two pieces and pieced back together at the Clients end for the desired
                                                // output as requested by Professor Elliott
        }
        else{
            out.println(nData[1]+" ");
        }
        out.println(": " + nData[0]);
    }

    /**
     * This function sends the Proverb to the CLient it calls chooseProverb which does the actual choosing of the Proverb.
     * That function returns an Array of Strings from which the information is sent to the Client.
     * @param out out is the output stream to send information to the client
     * @param data is the cookie that informs the chooseProverb function which proverbs have already been used.
     */
    private static void sendProverb(PrintStream out, String data){
        System.out.println("Sending Proverb.");
        String[] nData = chooseProverb(data); //as the functions says choose Proverb with the help of the cookie
        if(serverType.equals("sec")){ //if it is a secondary server send <S2> before every message
            out.println("<S2>"+nData[1]+" ");// The Information is sent in two pieces and pieced back together at the Clients end for the desired
                                            // output as requested by Professor Elliott
        }
        else{
            out.println(nData[1]+" ");
        }
        out.println(": " + nData[0]);
    }
    
    /**
     * This function is a unitility function I created to randomzie the Jokes. This is the core randomization function and is used for the numerical calculation.
     * in shot this function uses the java.Math library and recieves a random double between 0 and 1 this number is multiplied by the largest number we 
     * wish to recieve and then cast it to an in to return a natural number in the needed range.
     * @param n is the largest number you wish to recieve
     * @return a random Number between 0 and n
     */
    private static int getRandomNo(int n){
        return (int)(Math.random()*n);
    }
}


/**
 * The actual JokeServer Class is a short amount of code it is used to either create a primary or secondary JokeServer.
 * This is done with the argument "secondary" when starting the program in the terminal.
 * It then assigns the port numbers it must listen on for administration (5050 for primary 5051 for secondary) and 
 * the actual JokeServer service which is providing jokes and proverbs (4545 for primary and 4546 for secondary services).
 * It then informs the user what type of service it is running and also names the port number it will be listening for 
 * JokeClient connections.
 */

public class JokeServer{
    /**
     * This main functions loops to take the user input
     * @param args used for secondary server
     * @throws IOException for socket errors and other issues such as the Buffers.
     */
    public static void main(String args[]) throws IOException{

        //intializing variables
        String type;
        int portNo; 
        int contPort;
        AdminListener controller;

        //assigning ports depending on server type and informing about wrong usage depending on amount of arguments
        if(args.length>0 && args[0].toString().equals("secondary")){
            portNo = 4546;
            type = "secondary";
            contPort = 5051;
        }
        else if(args.length>0 && !args[0].toString().equals("secondary")){
            System.out.println("Usage: $java JokeServer [secondary] \nTerminating Server.");
            return;
        }
        else{
            portNo = 4545;
            type = "primary";
            contPort = 5050;
        }

        //information about server (Name, type and port)
        System.out.println("\n   Reineke JokeServer 1.0 \n   Now running as a "+type+" Server on Port: "+portNo+".\n");
        
        

        ServerSocket ss = new ServerSocket(portNo,6);// this is to help to listen to incoming connecitons and pass them on the class that does the randomization
        controller = new AdminListener(contPort); // the "controller" is an AdminListener class and listen on admin ports to switch mode and turn off the server
        controller.start();
        

        while(controller.isAlive()){ //isAlive statement is used to switch off server in the end. Capturing the state of the AdminListener Class will determine wether the Server can be shut off or not
            /**
             *  the programm waits at this position for an incoming connection only when someone connects will the sofware 
             * keep running pass it on the Randomizer who will in turn return a random joke or proverb depending on the clients
             *  choice entertain the user to the utmost extent
             */
            new Randomizer(ss.accept(),controller).start(); 
        }
    }
}