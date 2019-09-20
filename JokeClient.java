/*
18.09.2019
Werner Reineke-Ryskiewicz
CSC 435 - 701
Professor Clark Elliott
HW 2: JokeClient
*/

public class JokeClient{

    static String requestJoke(String server){
        
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
        
    }
}