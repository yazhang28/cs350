import java.util.concurrent.Semaphore;
import java.util.*;

public class Train extends Thread{
  
  private int id;
  public static volatile int C =4; //number of trains allowed on the stretch of tunnel
  public static volatile int X =2; //fairness variable to limit too many trains traveling in one direction
  public static volatile int N =20; //total number of trains 
  public static volatile int K = 1; // number of repetitions a train moves north/south
  
  public static volatile Semaphore congestionLock = new Semaphore(C);
  public static volatile Semaphore fairnessLock = new Semaphore(1);
  public static volatile Semaphore turn = new Semaphore(1);
  public static volatile Semaphore lock = new Semaphore(1);
  public static volatile Semaphore z = new Semaphore(X);
  
  public static volatile Semaphore southFairnessLock = new Semaphore(0);
  public static volatile Semaphore northFairnessLock = new Semaphore(0);
  
  public static volatile int southPassed = 0;// number of trains which passed heading south
  public static volatile Semaphore passedMutex = new Semaphore(1);
  public static volatile int northPassed = 0;//number of trains which passed heading north
  public static volatile Semaphore northPassedMutex = new Semaphore(1);
  
  
  public static volatile Semaphore southCountMutex = new Semaphore(1);
  public static volatile Semaphore northCountMutex = new Semaphore(1);
  public static volatile int nT = 0; //counts the number of trains that have been allowed to go north
  public static volatile int sT = 0; //counts the number of trains that have been allowed to go south
  
  private final Random rand = new Random();
  
  public Train(int i){
    this.id = i;
  }
  
  public void run(){
    for(int i =0; i<K; i++){
      
      
      //check if trains are allowed to travel north
      wait(turn);
      wait(northCountMutex);
      //train arrives on the stretch wants to travel north
      //System.out.println("northbound train wants to enter tunnel");
      nT++;
      
      if(nT ==1){ //if this is the first train to travel north, it must wait for the lock to travel north
        //System.out.println("North trains waiting for their lock");
        wait(lock);
        //System.out.println("North trains have aquired their lock");
      }
      signal(northCountMutex);
      signal(turn);
      wait(congestionLock);
      //travel north on the stretch
      System.out.println("northbound train in tunnel");
      //System.out.print("N");
      
      signal(congestionLock);
      wait(northCountMutex);
      nT--;
      if(nT ==0){
        signal(lock); //if this is the last train to travel north, signal the lock for trains to travel south
        //System.out.println("North trains are releasing the South lock");
      }
      //exits on the North side
      //System.out.print("e");
      System.out.println("train exiting north entrance");
      signal(northCountMutex);
      
      //sleep
      delay(30);
      
      //check if trains are allowed to travel south
      wait(southCountMutex);
      //train arrives on the stretch wants to travel south
      System.out.println("southbound train wants to enter tunnel");
      sT++;
      if(sT ==1){ //if first train to travel south, it must wait for the lock to travel south
        //System.out.println("South trains waiting for their lock");
        wait(turn);
        //System.out.println("South trains have aquired their lock");
      }
      signal(southCountMutex);
      wait(lock);
      
      wait(congestionLock);
      //traveling south on the stretch
      //System.out.print("S");
      System.out.println("southbound train in tunnel");
      signal(congestionLock);
      signal(lock);
      wait(southCountMutex);
      sT--;
      if(sT ==0){ //if last train to travel south, signal the lock for trains to travel north
        signal(turn); 
        //System.out.println("South trains are releasing the North lock");
      }
      //exits South side
      //System.out.print("e");
      System.out.println("train exiting south entrance");
      signal(southCountMutex);
      
      //sleep
      delay(30);
    }
    
  }
  
  public void signal(Semaphore s){
    s.release();
  }
  
  
  public void wait(Semaphore s){
    try{
      s.acquire();
    }
    catch(InterruptedException e){
    }
  }
  
  private void delay(int value)
  {
    try 
    {
      sleep(rand.nextInt(value));
    } 
    catch (InterruptedException e) { }
  }
  
   public static void main(String[] args){
  Train[] trains = new Train[Train.N];
  for(int i =0; i<Train.N; i++){
   trains[i] = new Train(i);
   trains[i].start();
  }
 }
}