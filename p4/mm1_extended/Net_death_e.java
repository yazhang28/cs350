
public class Net_death_e extends Event {
 Event new_birth_e;
 
 public Net_death_e() {
  eventTime = SysNetwork.NetTs;     //eventTime  = Ts
  //eventTime =SysNetwork.genExp(1/0.025);    ///TESTWITHHW3 TESTWITHHW3 TESTWITHHW3
  request = SysNetwork.NetRequests.remove();  // remove first request waiting in request list
  request.setTs(eventTime);      // give it its Ts
  SysNetwork.NetRequests.addFirst(request);  // insert request back into list
 }
 
 public void setReqSerTime(double time){    // set time at which the request is scheduled
  request = SysNetwork.NetRequests.remove();
  request.setSerTime(time);
  SysNetwork.NetRequests.addFirst(request);
 }
 
 public void run(){  
  // remove record of request from schedule
  if (SysNetwork.NetRequests.size()>1){
   Controller.NetState--;
  }
  request = SysNetwork.NetRequests.removeFirst();

  // for debugging
  if (Controller.time>request.getEndTime())
   System.out.println("sim time less than arrival time");
  Controller.time = request.getEndTime();
  // end debugging
  
  // determine where the request will go
  // network always goes back to CPU
  new_birth_e = new Birth_e(Controller.time, request);
  SysNetwork.insert_new_e(new_birth_e);
  // end determining what the next course of action of request is
  
  // check if other requests are pending in the queue
  if (SysNetwork.NetRequests.size()>0) {
   Event new_death_e = new Net_death_e();
   
   // set new DeathEvent's service time to the max of previous request's end time and arrivalTime of new DeathEvent
   ((Net_death_e) new_death_e).setReqSerTime(Math.max(request.getEndTime(), new_death_e.request.getArrTime())); 
   SysNetwork.insert_new_e(new_death_e);
  }
 }
}