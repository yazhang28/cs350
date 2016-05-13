import java.util.Scanner;  
import java.util.concurrent.Semaphore;  
import java.util.logging.Level;  
import java.util.logging.Logger;  

class Bus extends Thread {  
  
  private Counter waiting;  
  private Semaphore mutex;  
  private Semaphore bus;  
  private Semaphore boarded;  
  
  private int countNumberOfRidersWent;  
  
  public Bus(int countNumberOfRidersWent,Counter waiting, Semaphore mutex, Semaphore bus, Semaphore boarded) {  
    this.waiting = waiting;  
    this.mutex = mutex;  
    this.bus = bus;  
    this.boarded = boarded;  
    this.countNumberOfRidersWent=countNumberOfRidersWent;  
  }  
  
  @Override  
  public void run() {  
    
    while (true) {  
      try {  
        mutex.acquire();  // bus gets the mutex and holds it throughout the boarding process  
        System.out.println("\nBus locked the BusStop");  
      } catch (InterruptedException ex) {  
        Logger.getLogger(Bus.class.getName()).log(Level.SEVERE, null, ex);  
      }  
      int n = Math.min(waiting.getCount(), 50);  
      
      // remove the below line to get the infinite loop , and then remove block 'check'  
      countNumberOfRidersWent-=n;  
      System.out.println("Available Passengers = " + waiting.getCount() +"  and "+ n+ " will be boarded...");  
      for (int i = 0; i < n; i++) {  
        bus.release();  // signal bus is ready to get a passenger in  
        try {  
          boarded.acquire(); // rider has boarded  
        } catch (InterruptedException ex) {  
          Logger.getLogger(Bus.class.getName()).log(Level.SEVERE, null, ex);  
        }  
      }  
      //When all the riders have boarded, the bus updates waiting  
      waiting.setCount(Math.max(waiting.getCount() - 50, 0));  
      
      mutex.release(); // release the mutex  
      
      System.out.println("Bus depart...\n");  
      
      // block 'check'  
      if(countNumberOfRidersWent==0){  
        break;  
      }
    }
  }  
}  


class Rider extends Thread {  
  
  private int id;  
  private Counter waiting;  
  private Semaphore mutex;  
  private Semaphore bus;  
  private Semaphore boarded;  
  
  public Rider(int id, Counter waiting, Semaphore mutex, Semaphore bus, Semaphore boarded) {  
    this.waiting = waiting;  
    this.mutex = mutex;  
    this.bus = bus;  
    this.boarded = boarded;  
    this.id = id;  
  }  
  
  @Override  
  public void run() {  
    
    try {  
      mutex.acquire();  
    } catch (InterruptedException ex) {  
      Logger.getLogger(Rider.class.getName()).log(Level.SEVERE, null, ex);  
    }  
    waiting.incrementCount();  
    mutex.release();  
    try {  
      bus.acquire(); // waiting for the bus  
    } catch (InterruptedException ex) {  
      Logger.getLogger(Rider.class.getName()).log(Level.SEVERE, null, ex);  
    }  
    System.out.println("rider got in to the bus..");  
    boarded.release(); // got in to the bus  
    
  }  
}  

// 
// an object which shared among the threads. it is the Counter   
class Counter {  
  
  private int count;  
  
  public Counter(int count) {  
    this.count = count;  
  }  
  
  public int getCount() {  
    return count;  
  }  
  
  // reasign a value to the counter  
  public void setCount(int count) {  
    this.count = count;  
  }  
  
  // add 1 to the present value  
  public void incrementCount() {  
    this.count = ++count;  
  }  
}  