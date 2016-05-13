import java.util.concurrent.Semaphore;
import java.util.Random;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

class MyProcess extends Thread {
  private int id;
  private int problem = 3;
  /* 1 = 3a
   * 2 = 3b
   * 3 = 3c
   * 4 = 3d
   * 5 = 4
   */
  volatile static private boolean[] flag;
  volatile static private int turn = 0;
  
  // for gathering results
  private int[] count = new int[100]; // count of busy waits for individual trials
  private int sum = 0; // for finding avg busy waits
  private int sum2 = 0;
  
  volatile static private PrintWriter writer;
  volatile static private PrintWriter writer2;
  
  public MyProcess(int i) {
    id = i;
  }
  
  // method returns a random number between 0 - 20 inclusive used as input for Thread.sleep()
  public int randInt() {
    Random r = new Random();
    int rn = r.nextInt((20 - 0) + 1) + 0;
    return rn;
  }
  
  // catching exception Thread.sleep() throws
  public void sleeps() {
    try {
      Thread.sleep(randInt());
    } catch (InterruptedException e) {
      System.out.println("got interrupted");
    }
  }
  
  public void run() {
    int i = id==0 ? 0:1;
    int j = id==0 ? 1:0;
    
    switch (problem) {
      
      case 1: // problem 3a
        for (int k = 0; k < 5; k++) {
        System.out.println("Thread " + id + " is starting iteration " +  k);
        sleeps(); // sleep for a random amount of time (between 0 - 20 msec)
        System.out.println("We hold these truths to be self-evident, that all men are created equal,");
        sleeps();
        System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
        sleeps();
        System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");  
        sleeps();
        System.out.println("Thread " + id + " is done with iteration " + k);
      }
        break;
      case 2: // problem 3b
        for (int k = 0; k < 5; k++) {
        flag[i] = true; 
        while (flag[j]){
          if (turn == j) { 
            flag[i] = false;
            while (flag[j] == true) {};
            flag[i] = true;
          }
        }
        // critical section
        System.out.println("Thread " + id + " is starting iteration " +  k);
        sleeps(); // sleep for a random amount of time (between 0 - 20 msec)
        System.out.println("We hold these truths to be self-evident, that all men are created equal,");
        sleeps();
        System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
        sleeps();
        System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
        sleeps();
        System.out.println("Thread " + id + " is done with iteration " + k);
        turn = j;
        flag[i] = false; 
      }
        break;
      case 3: //problem 3c
        for (int k = 0; k < 5; k++) {
        flag[i] = true; 
        while (flag[j]){
          if (turn == j) { 
            flag[i] = false;
            while (flag[j] == true) {};
            flag[i] = true;
          }
        }
        // critical section
        System.out.println("Thread " + id + " is starting iteration " +  k);
        sleeps(); // sleep for a random amount of time (between 0 - 20 msec)
        System.out.println("We hold these truths to be self-evident, that all men are created equal,");
        sleeps();
        System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
        sleeps();
        System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
        sleeps();
        System.out.println("Thread " + id + " is done with iteration " + k);
        turn = j;
        flag[i] = false; 
      }
        break;
      case 4:
        for (int k = 0; k < 5; k++) {
        turn = j; 
        flag[i] = true;
        while (flag[j] && (turn == j)) {};           
        // critical section
        System.out.println("Thread " + id + " is starting iteration " +  k);
        sleeps(); // sleep for a random amount of time (between 0 - 20 msec)
        System.out.println("We hold these truths to be self-evident, that all men are created equal,");
        sleeps();
        System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
        sleeps();
        System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
        sleeps();
        System.out.println("Thread " + id + " is done with iteration " + k);
        flag[i] = false; 
      }
        break;
      case 5:
        for (int n = 0; n < 100; n++) { // doing 100 trials of 5 iterations
        for (int k = 0; k < 5; k++) {
          turn = j; 
          flag[i] = true;
          while (flag[j] && (turn == j)) {
            count[n]++ ; // count[5]: number of busy waits in trial 5
          }
          // critical section
          System.out.println("Thread " + id + " is starting iteration " +  k);
          sleeps(); // sleep for a random amount of time (between 0 - 20 msec)
          System.out.println("We hold these truths to be self-evident, that all men are created equal,");
          sleeps();
          System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
          sleeps();
          System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
          sleeps();
          System.out.println("Thread " + id + " is done with iteration " + k);
          flag[i] = false; 
        }
        if (id == 0) {
        sum += count[n];
        } else {
        sum2 += count[n];          
        }
        
        System.out.println("trial " + n + ": " + count[n] + " busy waits");
        if (id == 0) {
          writer.println("trail " + n + ": " + count[n] + " busy waits" + "\n");
        } else {
          writer2.println("trial " + n + ": " + count[n] + " busy waits" + "\n");
        }
      }
        if (id == 0) {        
          System.out.println("sum: " + sum);
          System.out.println("busy waits on avg from conducting 100 trials = " + sum/100);
          writer.println("sum: " + sum + "\n");
          writer.println("busy waits on avg from conducting 100 trials = " + sum/100 + "\n");
        } else {
          System.out.println("sum: " + sum2);
          System.out.println("busy waits on avg from conducting 100 trials = " + sum2/100);  
          writer2.println("sum: " + sum2 + "\n");         
          writer2.println("busy waits on avg from conducting 100 trials = " + sum2/100 + "\n");
        }
        
        if (id==0)
          writer.close();
        else
          writer2.close();
        break;
    }
  }
  
  public static void main(String[] args) throws IOException {
    writer = new PrintWriter("countResults.txt"); // first file to write to w/ busy count results
    writer2 = new PrintWriter("countResults2.txt"); // second file to write to w/ busy count results
    
    final int N = 2; // N is number of threads 
    MyProcess[] p = new MyProcess[N]; // array of threads 
    flag = new boolean[N];
    for (int i = 0; i < N; i++) {
      p[i] = new MyProcess(i);
      p[i].start();
      
    }
  }
}