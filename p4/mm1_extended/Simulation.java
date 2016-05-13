import java.util.LinkedList;


public abstract class Simulation {

 public static int initState(){
  int state = 0;
  return state;
 }

 public static LinkedList<Event> initSchedule(){
  LinkedList<Event> schedule = new LinkedList<Event>();
  schedule.add(new Birth_e());
  return schedule;  
 }
 
 public static Event get_next_e(LinkedList<Event> sch) {
  Event event = sch.remove();
  return event;
 }
}