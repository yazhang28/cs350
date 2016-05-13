import java.util.Scanner;  
import java.util.concurrent.Semaphore;  
import java.util.logging.Level;  
import java.util.logging.Logger;  

public class BusProblem {  
  
    private static Counter waiting = new Counter(0);  
    private static Semaphore mutex = new Semaphore(1);  
    private static Semaphore bus = new Semaphore(0);  
    private static Semaphore boarded = new Semaphore(0);  
  
    public static void main(String[] args) {  
        int n = 10;  
          System.out.println("number of riders allowed on a bus: " + n);
          int k = 6;
        // Demonstrate the behaviour of the Bus. this thread's run method will keep running  
        new Bus(n,waiting, mutex, bus, boarded).start();  
        // nnumber of riders to  demonstrate the behaviour of the riders pool   
        for (int i = 0; i < n; i++) {  
            new Rider(i, waiting, mutex, bus, boarded).start();  
        }
  
    }  
}  