
import java.io.*;


public class test{
    
    public static void main (String args[]){
        String username = "Werner";
        String[] joke = {"JA %s Why did the chicken cross the road? To get to the other side!","JB %s What is my name? I'm the funny bastard."};
        System.out.println(joke[0].format(username));
    }
}