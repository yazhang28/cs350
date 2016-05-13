import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;


public class Controller {
 public static LinkedList<Event> schedule = new LinkedList<Event>();  // event list
 public static int CPUState;   // CPU state = CPU w
 public static int DiskState;  // Disk state = Disk w
 public static int NetState;   // Network state = Net w
 public static double time = 0;  // time of simulator
 public static PrintWriter writer = null;   // write to file w/ Request results
 public static PrintWriter writer2 = null;   // write to new file w/ Final simulator results
 
 public static void main(String[] args) throws IOException {
  boolean monitor_f = false;  // flag for using monitor event
  
  // initialization of the simulated world and calendar
  CPUState =  SysNetwork.initState();
  DiskState = SysNetwork.initState();
  NetState = SysNetwork.initState();
  schedule = SysNetwork.initSchedule();
  System.out.println("Initiated world.");
  // end initialization of the simulated world and calendar
  
  Event event = new Event();
  
  writer = new PrintWriter(new FileWriter("Q2_test_Requests.txt"));
  writer2 = new PrintWriter(new FileWriter("Q2_test_FinalResults.txt"));
  
  while (time < SysNetwork.sim_time){
   
   // start monitor event init
   if (monitor_f==false&&time>=SysNetwork.sim_time/2){ // if the first monitor event hasn't been scheduled, && time>half the total sim time
    // half way through simulation start monitoring
    Event new_monitor_e = new Monitor_e(time);
    SysNetwork.insert_new_e(new_monitor_e);
    monitor_f=true; //it's true that the first logEvent has been scheduled
   }
   // end monitor event Init
   
   // retrieve next event from calendar
   event = SysNetwork.get_next_e(schedule);
   // call function that needs to be executed to reflect occurrence of event
   if (event instanceof Birth_e) {
    ((Birth_e)event).run();
   } else if (event instanceof ToDisk) {
    ((ToDisk)event).run();
   } else if (event instanceof ToNet) {
    ((ToNet)event).run();
   } else if (event instanceof CPU_death_e) {
    ((CPU_death_e)event).run();
   } else if (event instanceof Disk_death_e) {
    ((Disk_death_e)event).run();
   } else if (event instanceof Net_death_e) {
    ((Net_death_e)event).run();
   } else if (event instanceof Monitor_e) {
    ((Monitor_e)event).run();
   }
   
  }
  // print results of simulation, etc.
  Monitor_e.genstats();
  writer.close();   
  writer2.close();
 }
}