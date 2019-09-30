/*--------------------------------------------
1. Name / Date: 
Werner Reineke-Ryskiewicz
18.09.2019

2. Java Version Used: 1.8.0_211-b12

3. you may compile this code as follows: 
$javac JokeClient.java
or
$javac *.java
second is usefull if you are compiling many files

4.If you wish to run this particular piece of hardware open a terminal and after comilation type:
$java JokeClient [ipaddr] [ipaddr]

5. To run the Client you only require this file. To utilize its functionality you must connecto to a Server
that has the JokeServer software running. Please view JokeServer.java for further instructions on
running this piece of software.

To administrate the JokeSever please reference the JokeClientAdmin.java software.

6. Notes:

This is the JokeClient

Version 28.9.19

-----------------------------------------------*/

/*
including input-output libraries to print information to command line
the networking libraries are obviously for the networking part of this assignment (sockets and such)
*/
import java.io.*;
import java.net.*;

public class JokeClient{
    //this is the most important piece of data in the code. It entails the information to correctly randomize the jokes. This is the so called cookie.
    private static String cookie = "";
    /**
     * In this general function we are preparing the cookie for the next Joke or Proverb cycle.
     * The cookies saves information on which jokes have been told so after the cycle is done
     * the "counter" for the jokes are set to 0.
     * @param whatToClean This parameter indicates wether we are cleaning the joke cookies or proverb cookies
     */
    private static void polishCookie(String whatToClean){
        String temp = "";
        String [] jokes = {"JA","JB","JC","JD"}; //the following two lines are the templates for the jokes that are available on the server
        String [] prov = {"PA","PB","PC","PD"};
        String[] cleaning = new String[4];
        if(whatToClean == "PROVERB"){ //determingin what needs to be cleaned
            cleaning = jokes;
        }
        else{
            cleaning = prov;
        }
        
        for(String i: cleaning){ // removing excess information from the cookie sothat the process works correctly
            if(cookie.contains(i)){
                temp+=i;
            }
        }

        cookie = temp;
    }

    /**
     * This is where the magic of the JokeClient happens. I have decided to have a very light
     * Client apart from the "cookiePolishing" from the function above. All this function really
     * does is send a request with the JOKE/PROVERB cycle information so that the server knows
     * which Jokes/Provers it may send back. It then prints the information to the output and closes
     * the socket opened in the process. This function is also capable or exception handling, even
     * if it does print the exceptions to the output.
     * @param server this parameter is the address of the JokeServer. It is important to have this value correct
     * otherwise you cannot connect to the server
     * @param port this is the typical portnumber for the server. Either 4545 or 4546 for the secondary server.
     * @param name This is the name of the user that is searching for jokes.
     */
    static void requestJoke(String server,int port, String name){
        // here we initalize the used parametrs.
        Socket sock;
        BufferedReader from;
        PrintStream to;
        String incoming;
        try{//here we open the connection to the JokeServer
            sock = new Socket(server,port);
            from = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            to = new PrintStream(sock.getOutputStream());

            to.println(cookie); // send the aforementioned cookie information to the server for processing
            to.flush();

            int i = 0;
            while(i<4){ // processing incoming information from the jokeserver and ensure correct output to the screen
                incoming = from.readLine();
                if(incoming!=null){
                    if(incoming.equals("JOKE CYCLE COMPLETED")){ 
                        polishCookie("JOKE"); // making sure the JOKE Cycle can be completed agian sothat the server returns Jokes on the next request
                        System.out.println(incoming);
                    }
                    else if(incoming.equals("PROVERB CYCLE COMPLETED")){
                        polishCookie("PROVERB");// here it is the same as for the JOKE cycle we ensure that the server sends Proverbs and prepare the cookie accordingly
                        System.out.println(incoming);
                    }
                    else if(incoming.indexOf("J")==0 || incoming.indexOf("P")==0 || incoming.indexOf("<")==0){
                        System.out.print(incoming+name);
                        cookie += incoming;
                    }
                    else{
                        System.out.println(incoming);
                    }
                }
                i++;
            }
            sock.close(); //the socket must be closed
        }
        catch(IOException exc){
            System.out.println("Socket Error.");
            exc.printStackTrace();
        }
    }

    /**
     * This is the main function of the JokeClient that will run upon starting this program.
     * It consists of 3 main parts:
     * 
     * 1. Establish the connections that can be made to JokeServers for retrieving the jokes and provers
     * this includes ip addresses or web addresses and port numbers depending on whether
     * the server is of type primary or secondary. Finally it informs the user which servers
     * are available for connecting.
     * 
     * 2.Initializes a Buffer and and requests username.
     * 
     * 3. Starts a loop waiting for an input. Entering "s" results in the look switching server
     * that is from primary to secondary or vice versa. "quit" shuts down the software.
     * Typing the enter key or any other input will trigger the code to start the requestJoke
     * function. In short this is where the client connects to the Server to request a Random Joke.
     * (see function requestJoke for more details)
     * @param args this function accepts two arguments. They are to be ip-addresses or website URLs.
     * The first argument is for the primary server working at port 4545 and the second argument is for
     * the secondary server working at port 4546
     */
    public static void main(String args[]){

        //initializing default variables and default entries for the primary and secondary servers.

        int primaryPort = 4545;
        String primaryServer = "localhost";
        int secondaryPort = 4546;
        String secondaryServer = "localhost";

        int connectToPort;
        String connectToServer;

        //for arguements entered into terminal asserting primary and secondary servers and assinging values to variables
        if(args.length < 1){ // for no arguments assign default as primary and secondary server
            connectToServer = primaryServer;
            connectToPort = primaryPort;
        }
        else if(args.length < 2){ // for one argument assign this argument as primary server and secondary server as the default
            primaryServer = args[0];
            connectToServer = primaryServer;
            connectToPort = primaryPort;    
        }
        else if (args.length < 3){ // should two arguments be enterd use first argument for primary server and second argument for secondary server
            primaryServer = args[0];
            secondaryServer = args[1];
            connectToServer = primaryServer;
            connectToPort = primaryPort;    
        }
        else{ //for the case that the user typed in more than 2 arguments inform user of usage of programm
            System.out.println("Usage: $java JokeClient [ipaddr] [ipaddr]\n\n Terminating Program\n");
            return;
        }


        /**
         * printing information for user:
         * How to quit the program and which servers we are connected to.
         * The servers the user entered as arguments will show up here otherwise
         * the default will be assigned and printed to output.
         */
        System.out.println("Werner's Joke Client is running!");
        System.out.println("type [quit] to quit program!");
        System.out.println("Server one: "+ primaryServer + ", port "+primaryPort);
        System.out.println("Server two: "+ secondaryServer + ", port "+secondaryPort+"\n");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));



        String type;
        try{ // catching exceptions that might occur
            System.out.print("Please enter username: "); // get username from the terminal
            String user = in.readLine();
            do{
                System.out.flush();
                type = in.readLine();
                if(type.indexOf("quit")<0){
                    if (type.equals("s")){
                        /**
                         * typing s will cause the Client to switch from the primary joke server to the secondary jokeserver and 
                         * allow a connection to a different IP/URL and port
                         */
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
                        //requesting Joke or proverb from Server for any other input even for the empty input
                    }
                }

            }while(type.indexOf("quit")<0);
            
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }
}