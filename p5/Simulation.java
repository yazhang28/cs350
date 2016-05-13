import java.util.LinkedList;
import java.util.Random;


public abstract class Simulation {
 static Random rand = new Random();
 
 public static int initState(){
  int state = 0;
  return state;
 }
 
 public static Event getNextEvent(LinkedList<Event> sch) {  // returns the next Event in the schedule
  Event event = sch.remove();
  return event;
 }

 // Function used to generate an exponential RV
 // Will be used for generating interarrival and service times
 public static double genExp(double lamb) {
  double uRV = rand.nextDouble();
  double eRV = -(Math.log(1-uRV))/lamb;
  return eRV;
 }
}