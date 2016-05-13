
public class Monitor_e extends Event {
 private static int runCount = 0;
 private static double Tq = 0;
 private static double Tw = 0;

 private static double IO_total_tq = 0;
 private static double IO_total_tw = 0;
 private static double IO_slowdown = 0;
 private static double cpu_total_tq = 0;
 private static double cpu_total_tw = 0;
 private static double CPU_slowdown = 0;
 private static double total_q = 0;
 private static double total_w = 0;
 
 
 public Monitor_e(double nextTime){
  eventTime = MM1System.genExp(MM1System.IO_lambda);
  request.setArrTime(nextTime+eventTime);
 }
 
 public static void setTqTw(int type, double responseTime, double serviceTime){
  Tq = responseTime;
  Tw = Tq - serviceTime;

  if (type==1){
   cpu_total_tq += Tq;
   cpu_total_tw += Tw;
   
  } else {
   IO_total_tq += Tq;
   IO_total_tw += Tw;
  }
 }
 
 public void run(){
  runCount++;
  
  // print and log Tw, Tq, q, and w
  total_q += MM1System.requestSchedule.size();
  total_w += Controller.state;
  // schedule next monitor event
  Event new_monitor_e = new Monitor_e(this.request.getArrTime());
  MM1System.insertNewEvent(new_monitor_e);
 }
 
 public static void genstats(){
  CPU_slowdown = cpu_total_tq/(cpu_total_tq-cpu_total_tw);
  IO_slowdown = IO_total_tq/(IO_total_tq-IO_total_tw);
  Controller.writer2.write("Final Results of simulation: \n" //Write results to file
    + "\tIO_Tw: "+IO_total_tw/runCount+"\n"
    + "\tIO_Tq: "+IO_total_tq/runCount+"\n"
    + "\tIO_Slowdown: "+IO_slowdown
    + "\tCPU_Tw: "+cpu_total_tw/(runCount/2)+"\n"
    + "\tCPU_Tq: "+cpu_total_tq/(runCount/2)+"\n"
    + "\tCPU_Slowdown: "+CPU_slowdown
    + "\tw: "+total_w/runCount+"\n"
    + "\tq: "+total_q/runCount);
  System.out.println("Final Results of simulation: \n"  // output results
    + "\tIO_Tw: "+IO_total_tw/runCount+"\n"
    + "\tIO_Tq: "+IO_total_tq/runCount+"\n"
    + "\tIO_Slowdown: "+IO_slowdown+"\n"
    + "\tCPU_Tw: "+cpu_total_tw/(runCount/2)+"\n"
    + "\tCPU_Tq: "+cpu_total_tq/(runCount/2)+"\n"
    + "\tCPU_Slowdown: "+CPU_slowdown+"\n"
    + "\tw: "+total_w/runCount+"\n"
    + "\tq: "+total_q/runCount);
 }
 
}