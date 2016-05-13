import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;


public class Controller {
 public static LinkedList<Event> schedule = new LinkedList<Event>();  // event list
 public static int state;   //state = w
 public static double time = 0;  // time of simulator
 public static PrintWriter writer = null;   // first file to write to w/ Request results
 public static PrintWriter writer2 = null;   // second file to write to w/ Final simulator results
 
 public static double tempSD;
 
 public static void main(String[] args) throws IOException {
  boolean logFlag = false;
  // initialization of the simulated world and calendar
  state =  MM1System.initState();
  System.out.println("State initiated to "+state+"\n");
  MM1System.initSchedule();
  
  Event event = new Event();
  writer = new PrintWriter(new FileWriter("Q3_test_Requests.txt"));
  writer2 = new PrintWriter(new FileWriter("Q3_test_FinalResults.txt"));
  
  while (time < MM1System.simulationTime){
   if (logFlag==false&&time>=MM1System.simulationTime/2){ // if the first monitor event hasn't been scheduled, && time>half the total sim time
    // half way through simulation start monitoring
    Event new_monitor_e = new Monitor_e(time);
    MM1System.insertNewEvent(new_monitor_e);
    logFlag=true; 
   }
   
   // retrieve next event from calendar
    event = MM1System.getNextEvent(schedule);
   // call function that needs to be executed to reflect occurrence of event
   if (event instanceof Birth_e) {
    ((Birth_e)event).run();
   }
   else if (event instanceof Death_e) {
    ((Death_e)event).run();
   }
   else if (event instanceof Monitor_e) {
    ((Monitor_e)event).run();
   }
   System.out.println("time = "+time+ "\t\t state = "+state+"\t\t size = "+schedule.size()+"\n");
  }
  // print results of simulation
  Monitor_e.genstats();
  writer.close();   // close files
  writer2.close();
 }
}