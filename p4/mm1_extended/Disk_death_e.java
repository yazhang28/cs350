
public class Disk_death_e extends Event {
 Event new_birth_e;
 
 public Disk_death_e() {
  eventTime = SysNetwork.genNorm(SysNetwork.Disk_mean,SysNetwork.Disk_stdev);  // eventTime = Ts
  while (eventTime<=0)        // check that eventTime isn't negative 
   eventTime = SysNetwork.genNorm(SysNetwork.Disk_mean,SysNetwork.Disk_stdev); 
  request = SysNetwork.DiskRequests.remove();   // remove first request waiting in request list
  request.setTs(eventTime);       // Gives it its Ts
  SysNetwork.DiskRequests.addFirst(request);   // insert request back into list
 }
 
 public void setReqSerTime(double time){     // set time at which the request is scheduled
  request = SysNetwork.DiskRequests.remove();
  request.setSerTime(time);
  SysNetwork.DiskRequests.addFirst(request);
 }
 
 public void run(){
  
  // remove record of request from schedule
  if (SysNetwork.DiskRequests.size()>1){
   Controller.DiskState--;
  }
  request = SysNetwork.DiskRequests.removeFirst();
  
  // for debugging
  if (Controller.time>request.getEndTime())
   System.out.println("sim time less than arrival time");
  Controller.time = request.getEndTime();
  
  // determine where the request will go
  double probability = SysNetwork.genUnif(0, 1); // generate number to use as probability of request going to each destination
  if (probability <=0.5){
   new_birth_e = new Birth_e(Controller.time,request);
  } else {
   new_birth_e = new ToNet(request);
  }
  
  SysNetwork.insert_new_e(new_birth_e);
  // end determining what the next course of action of request is
  
  // check if other requests are pending in the queue
  if (SysNetwork.DiskRequests.size()>0) {
   Event new_death_e = new Disk_death_e();
   //Set new DeathEvent's service time to the max of previous request's end time and arrivalTime of new DeathEvent
   ((Disk_death_e) new_death_e).setReqSerTime(Math.max(request.getEndTime(), new_death_e.request.getArrTime())); 
   SysNetwork.insert_new_e(new_death_e);
  }
 }
}