import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.Arrays;

class Tunnel extends Thread {
  private int cap = 4; // max number of trains in tunnel
  private int turn = 0; // turn: 0, leftbound trains have entrance into tunnel, turn: 1 have entrance into tunnel
  
  private Semaphore spaces = new Semaphore(cap);
  private Semaphore arrived = new Semaphore(0); // signaling train has left tunnel and arrived at destination
  private Semaphore mutex = new Semaphore(1); // changing turn
  private Semaphore mutex2 = new Semaphore(1); // updating waiting[]
  private static int[] waiting = {0,0}; //waiting[0] = leftbound trains waiting, waiting[1] = rightbound trains waiting
  
  private static Random rand = new Random();
  private int direction;
  private int id; 
  
  public Tunnel(int d, int i) {
    direction = d;
    id = i;
  }
  
  public void arrive(int direction, int id, String dest) { // leaving tunnel
    int other = 0; // other direction
    if (direction == 0) {
      other = 1;
    }
    
    if (turn == direction && waiting[other] >= 3) {
      wait(mutex);
      turn = other;    
      signal(mutex);
    }
    System.out.println(dest + " train " + id + " exits the tunnel");
    signal(spaces);
  }
  
  public void depart(int direction, int id, String dest) { // entering tunnel
    int other = 0;
    
    if (direction == 0) { // train traveling leftbound
      other = 1;
    }
    while((turn == other) && (waiting[direction] < 3)) {
      wait(mutex2);     
      waiting[direction]++;
      System.out.println(dest + " train " + id + " is waiting for tunnel");
    }
    wait(spaces);
    waiting[direction]--;       
    System.out.println(dest + " train " + id + "  entering tunnel");
    signal(mutex2);
  }
  
  
  // method returns a random number between 0 - 99 inclusive used as input for Thread.sleep()
  public int randInt() {
    Random r = new Random();
    int rn = r.nextInt(100);
    return rn;
  }
  
  // sleep in tunnel for random amount of time
  public void inTunnel(int id, int t, String dest) {    
    System.out.println(dest + " train " + id + " is in tunnel for " + t + " ms");    
    try {
      Thread.sleep(t);
    } catch (InterruptedException e) {}
  }
  
  public void run(){
    int i = id;
    String dest = "";
    if (direction == 0) { // train traveling leftbound
      dest = "leftbound";
    } else {
      dest = "rightbound";
    }
    depart(direction, i, dest);
    int time = randInt();
    inTunnel(i, time, dest);
    arrive(direction, i, dest);
  }
  
  public void wait(Semaphore x) {
    try {
      x.acquire();
    } catch (InterruptedException e) {}
  }
  
  public void signal(Semaphore x) {
    x.release();
  }
  
  public static void main(String[] args) {
    int n = 10; //number of trains per left and right bound
    
    Tunnel[] l = new Tunnel[n];  //list of leftbound trains
    Tunnel[] r = new Tunnel[n]; //list of rightbout trains
    
    for (int i = 0; i < n; i++) {
      l[i] = new Tunnel(0, i);
      r[i] = new Tunnel(1, i);
      l[i].start();
      r[i].start();
    }
  }
}