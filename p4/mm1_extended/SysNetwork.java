import java.util.LinkedList;
import java.util.Random;

public class SysNetwork extends Simulation {
 static double lambda = 40;
 static double CPULow = 0.001;
 static double CPUHigh = 0.039;
 static double Disk_mean = 0.100;
 static double Disk_stdev = 0.030;
 static double NetTs = 0.025;
 static Random rand = new Random();
 public static double sim_time = 1000;
 
 public static LinkedList<Request> CPURequests = new LinkedList<Request>();  // CPU queue
 public static LinkedList<Request> DiskRequests = new LinkedList<Request>(); // Disk queue
 public static LinkedList<Request> NetRequests = new LinkedList<Request>();  // Network queue
 
 private static Event check_e = new Event();
 private static int size;

 // function used to generate an exponential RV
 public static double genExp(double lamb) {
  double uRV = rand.nextDouble();
  double eRV = -(Math.log(1-uRV))/lamb;
  return eRV;
 }
 
 // function used to generate uniform RV between low and high 
 public static double genUnif(double low, double high) {
  double uRV = rand.nextDouble();
  uRV = ((high-low)*uRV)+low;
  return uRV;
 }
 
 // function used to generate normal distribution with mean and std dev
 public static double genNorm(double mean, double stdDev) {
  double nRV = rand.nextGaussian(); // normalRV from N(0,1)
  nRV = stdDev*nRV+mean; // linear trans to (mean,std dev)
  return nRV;
 }
 
 // function for all events to use to insert new event into schedule
 public static void insert_new_e(Event new_e){
  size = Controller.schedule.size();
  double eventReqTime;  // time of execution of event, used to compare to other events
  if ((new_e instanceof Birth_e) || (new_e instanceof Monitor_e) || 
    (new_e instanceof ToNet) || (new_e instanceof ToDisk))
   eventReqTime = new_e.request.getArrTime();  // use arrivalTime to compare if new event is a "Birth" event
  else 
   eventReqTime = new_e.request.getEndTime();  // use endTime to compare if new event is a "Death" event
  if (size==0){
   Controller.schedule.add(new_e);
  } else{
   for (int i = 0; i<size;i++) {
    check_e = Controller.schedule.get(i);
    if (check_e instanceof Birth_e) {
     if (eventReqTime<check_e.request.getArrTime()){ // if new_e arrives before monitor event
      Controller.schedule.add(i,new_e); // insert new event before the Birth_e
      break;
     }
    } else if (check_e instanceof ToDisk) {    // compare endTime of cpu death event and arrivalTime of new event
     if (eventReqTime<check_e.request.getArrTime()){ // if new_e arrives before monitor event
      Controller.schedule.add(i,new_e); // insert new event before the ToDisk
      break;
     }
    } else if (check_e instanceof ToNet) {    //Compares endTime of cpu death event and arrivalTime of new event
     if (eventReqTime<check_e.request.getArrTime()){ //if new event arrives before monitor event
      Controller.schedule.add(i,new_e); //Put new_e before the ToNet
      break;
     }
    } else if (check_e instanceof CPU_death_e) {    //Compares endTime of cpu death event and arrivalTime of new_e
     if (eventReqTime<check_e.request.getEndTime()){ //if arrivalTime<endTime
      Controller.schedule.add(i,new_e); //Put new event before the cpu death event
      break;
     }
    } else if (check_e instanceof Disk_death_e) {  //Compares endTime of disk death event and arrivalTime of new_e
     if (eventReqTime<check_e.request.getEndTime()){ //if arrivalTime<endTime
      Controller.schedule.add(i,new_e); //Put new_e before the disk death event
      break;
     }
    } else if (check_e instanceof Net_death_e) {  //Compares endTime of net death event and arrivalTime of new event
     if (eventReqTime<check_e.request.getEndTime()){ //if arrivalTime<endTime
      Controller.schedule.add(i,new_e); //Put new_e before the net death event
      break;
     }
    }else if (check_e instanceof Monitor_e) { //Compare arrivalTimes
     if (eventReqTime<check_e.request.getArrTime()){ //if new event arrives before monitor event
      Controller.schedule.add(i,new_e); //Put new event before the monitor event
      break;
     }
    }
    if (i==size-1){  // insert new event at the end
     Controller.schedule.add(new_e);
    }
   }
  }
 }
}