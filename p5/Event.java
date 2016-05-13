import java.util.LinkedList;


public class Event {
 public double eventTime=0;    // used differently for each type of event
 public Request request = new Request();  // temp request for each event object
 
 public Event() {
  
 }
 
 public void run(LinkedList<Event> schedule, int state, double time){
 }
}