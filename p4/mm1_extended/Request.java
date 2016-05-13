// "customers" of the system that are served by server
public class Request {
 private double IAT; // interarrival time
 private double Ts;
 private double arrivalTime;
 private double startTime;
 private double endTime;
 private double Tq;
 private int coreNum;
 
 // request holds all info
 public Request(){
  IAT = 0;
  arrivalTime = 0;
  Ts = 0;
  startTime = 0;
  endTime = 0;
  Tq = 0;
  coreNum = 0;
 }
 
 public void setArrTime(double time) {
  arrivalTime = time;
 }
 public void setTs(double time) {
  Ts = time;
 } 
 public void setIAT(double time) {
  IAT = time;
 } 
 public void setSerTime(double time) {
  startTime = time;
  endTime = startTime+Ts;
  Tq=Tq+Ts+startTime-arrivalTime;
 }
 public void setCore(int num) {
  coreNum = num;
 } 
 
 public double getArrTime(){
  return arrivalTime;
 }
 public double getTs(){
  return Ts;
 }
 public double getIAT(){
  return IAT;
 }
 public double getTq(){
  return Tq;
 }
 public double getStartTime(){
  return startTime;
 }
 public double getEndTime(){
  return endTime;
 }
 public int getCoreNum(){
  return coreNum;
 }

}