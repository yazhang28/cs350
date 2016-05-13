
public class Monitor_e extends Event {
 private static int runCount = 0;
 private static double Tq = 0;
 private static double Tw = 0;
 private static double total_tq = 0;  //E[Tq]
 private static double total_tw = 0;
 private static double total_q_cpu = 0; //E[q]
 private static double total_q_disk = 0;
 private static double total_q_net = 0;
 private static double total_w_cpu = 0;
 private static double total_w_disk = 0;
 private static double total_w_net = 0;
 private static double total_q_sq = 0; //E[q^2]
 private static double total_tq_sq = 0; //E[Tq^2]
 private static double stdev_q = 0;
 private static double stdev_tq = 0;
 private static int i = 0;
 
 public Monitor_e(double nextTime){
  eventTime = SysNetwork.genExp(SysNetwork.lambda);
  request.setArrTime(nextTime+eventTime);
 }
 
 public static void setTqTw(double responseTime, double serviceTime){
  Tq = responseTime;
  Tw = Tq - serviceTime;
  total_tq += Tq;
  total_tw += Tw;
  total_tq_sq += Math.pow(Tq, 2);
 }
 
 public void run(){
  runCount++;

  total_w_cpu += Controller.CPUState;
  total_q_cpu += SysNetwork.CPURequests.size();
  total_q_sq += Math.pow(SysNetwork.CPURequests.size(), 2);
  total_w_disk += Controller.DiskState;
  total_q_disk += SysNetwork.DiskRequests.size();
  total_w_net += Controller.NetState;
  total_q_net += SysNetwork.NetRequests.size();
  // schedule next monitor event
  Event new_monitor_e = new Monitor_e(this.request.getArrTime());
  SysNetwork.insert_new_e(new_monitor_e);
  
  // write to file the # of requests waiting in CPU queue
  
  if (Controller.time>=100+i){
   Controller.writer.write((Controller.time-100)+"\t"+Controller.CPUState+"\n");
   i++;
  }
 }
 
 public static double getE(double stdDev, int interval){
  double Z_val = 1.96; // Z = 1.96 for 95th interval
  double Z_val2 = 2.32; // Z = 2.32 for 98th interval
  if (interval == 95) {
    double E = Z_val*(stdDev/Math.sqrt(runCount));
    System.out.println("95 Confidence interval E = " + E);
    return E;
  }
  else {
    double E = Z_val2*(stdDev/Math.sqrt(runCount));
    System.out.println("98 Confidence interval E = " + E);
    return E;
  }
 }
 
 public static void genstats(){
  stdev_q = Math.sqrt(total_q_sq - Math.pow(total_q_cpu/runCount, 2));
  stdev_tq = Math.sqrt(total_tq_sq - Math.pow(total_tq/runCount, 2));
  
  // for 95th interval
  System.out.print("for q: ");
  double E_Q = getE(stdev_q, 95);
  System.out.print("for tq: ");
  double E_Tq = getE(stdev_tq, 95);
  
  // for 98th interval
  System.out.print("for q: ");
  double E_Q_2 = getE(stdev_q, 98);
  System.out.print("for tq: ");
  double E_Tq_2 = getE(stdev_tq, 98);
  
  Controller.writer2.write("Final Results of simulation: \n" // write results to file
    + "\tTw: "+total_tw/runCount+"\n"
    + "\tTq: "+total_tq/runCount+"\n"
    + "\tw_cpu: "+total_w_cpu/runCount+"\n"
    + "\tq_cpu: "+total_q_cpu/runCount+"\n"
    + "\tw_disk: "+total_w_disk/runCount+"\n"
    + "\tq_disk: "+total_q_disk/runCount+"\n"
    +"\tw_net: "+total_w_net/runCount+"\n"
    + "\tq_net: "+total_q_net/runCount+"\n"
    + "\tConfidence level of q (95 percentile): ["+total_q_cpu/runCount+"-"+E_Q
    + ", "+total_q_cpu/runCount+"+"+E_Q+"]\n"
   + "\tConfidence level of Tq (95th percentile): ["+total_tq/runCount+"-"+E_Tq
    + ", "+total_tq/runCount+"+"+E_Tq+"]\n"
    + ",\tConfidence level of q (98 percentile): ["+total_q_cpu/runCount+"-"+E_Q_2
    + ", "+total_q_cpu/runCount+"+"+E_Q_2+"]\n"                             
    + "\tConfidence level of Tq (98th percentile): ["+total_tq/runCount+"-"+E_Tq_2
    + ", "+total_tq/runCount+"+"+E_Tq_2+"]");
  System.out.println("Final Results of simulation: \n"  // output results
    + "\tTw: "+total_tw/runCount+"\n"
    + "\tTq: "+total_tq/runCount+"\n"
    + "\tw_cpu: "+total_w_cpu/runCount+"\n"
    + "\tq_cpu: "+total_q_cpu/runCount+"\n"
    + "\tw_disk: "+total_w_disk/runCount+"\n"
    + "\tq_disk: "+total_q_disk/runCount+"\n"
    +"\tw_net: "+total_w_net/runCount+"\n"
    + "\tq_net: "+total_q_net/runCount+"\n"
    + "\tConfidence level of q (95 percentile): ["+total_q_cpu/runCount+"-"+E_Q
    + ", "+total_q_cpu/runCount+"+"+E_Q+"]\n"
   + "\tConfidence level of Tq (95th percentile): ["+total_tq/runCount+"-"+E_Tq
    + ", "+total_tq/runCount+"+"+E_Tq+"]\n"
    + ",\tConfidence level of q (98 percentile): ["+total_q_cpu/runCount+"-"+E_Q_2
    + ", "+total_q_cpu/runCount+"+"+E_Q_2+"]\n"                       
    + "\tConfidence level of Tq (98th percentile): ["+total_tq/runCount+"-"+E_Tq_2
    + ", "+total_tq/runCount+"+"+E_Tq_2+"]");
 }
 
}