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
public class JokeClient{

    static String requestJoke(String server){
        return "";
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

        String connectTo;
        int defaultPort = 4545;
        int secondaryPort = 4546;

        if(args.length<1){
            connectTo = "localhost";

        }
        else{
            connectTo = args[0];
        }

        System.out.println("Werner's Joke Client is running!");
        System.out.println("You are currently connected to: "+connectTo+" on port: "+port)
        
    }
}