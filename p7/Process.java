import java.util.concurrent.Semaphore;
import java.util.Random;

class Process extends Thread {
  
  private static int count = 0; // counter
  private static int m = 5; // number of processes
  volatile static Semaphore B[]; // binary semaphore
  volatile static int[] R;  // array storing priority of each process
  volatile static int[] time;  // array storing the amount of time each process is spending in CS
  
  private int id;
  private static Random rand = new Random();
  
  private  static int problem = 1; // for binary semaphore (problems 3b, 3c)
  // 4 for N-batched semaphore (problem 4)
  private static int problem2 = 2; // for 3b
  // 3 for 3c
  
  public Process(int i) {
    id = i;
  }
  
  public void run() {
    // uncomment for 4, comment for 3b, 3c
    switch(problem) {
      case 1: // problem 3b, 3c
        for (int n = 0; n < 10; n++) { //requesting cs 10 times 
        newWait(id);
        inCS(time[id], id); // entering cs
        newSignal(id);
      }
        break;
      case 4: // problem 4
        for (int n = 0; n < 10; n++) { //requesting cs 10 times 
        if (id == 0) {  // delay for process 0 when requesting cs
          try {
            Thread.sleep(50);
          } catch(InterruptedException e) {
          }
          
          newWait(id);
          inCS(time[id], id); //entering cs
          newSignal(id);
        }
        else {
          newWait(id);
          inCS(time[id], id); // entering cs
          newSignal(id);
        } 
      }
    }
  }
  
  // method returns a random number between 0 - 99 inclusive used as input for Thread.sleep()
  public int randInt() {
    Random r = new Random();
    int rn = r.nextInt(100);
    return rn;
  }
  
  // sleep in CS for random amount of time
  public void inCS(int t, int id) {    
    System.out.println("P" + id + " is in the CS for " + t + " ms");    
    try {
      Thread.sleep(t);
    } catch(InterruptedException e) {
    }
  }
  
  public void newWait(int i) {
    time[i] = randInt();
    System.out.println("P" + i + " is requesting CS for " + time[i] + " ms");    
    R[i] = i+1;
    count++;
    if (count > 1) {
      try {
        B[i].acquire();
      }
      catch (InterruptedException e) {
      }
    }
  }
  
  public void newSignal(int i) {
    R[i] = 0;   
    time[i] = 0;
    count--;
    
    int biggest = 0; // biggest stores largest R[i] value
    int min = 0; // min stores smallest time spent in CS
    int r = 0; // r stores id of which to release    
    
    switch(problem2) {
      case 2: // problem 3b          
        if (count > 0) {
        for (int k = 0; k < m; k++) {
          if ((time[k] != 0) && (R[k] != 0)) {
            if (R[k] >= biggest) { 
              r = k;        
              biggest = R[k];
            }
          }
        }
      }
        break;
      case 3: // problem 3c
        if (count > 0) {
        for (int k = 0; k < m; k++) {
          if ((time[k] != 0) && (R[k] != 0)) {              
            if (time[k] <= min) { 
              r = k;
              min = time[k]; 
            }
          }
        }
      }
    }
    B[r].release();
    System.out.println("P" + r + " is exiting the CS");  
  }
  
  public static void main(String[] args) {
    Process[] p = new Process[m];
    time = new int[m];
    R = new int[m];
    B = new Semaphore[m];
    
    switch(problem) {
      case 1:        
        for (int i = 0; i < m; i++) {
        B[i] = new Semaphore(1,false);
        p[i] = new Process(i);
        p[i].start();
      }
        break;
      case 4:
        for (int i = 0; i < m; i++) {          
        B[i] = new Semaphore(3, false); // upper bound N = 3
        p[i] = new Process(i);
        p[i].start();          
      }
    }
  }
}