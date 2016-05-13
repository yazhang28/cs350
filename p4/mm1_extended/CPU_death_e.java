
public class CPU_death_e extends Event {
 private Event new_death_e;
 private Event new_birth_e;
 
 public CPU_death_e(int core, int ReqIndex) {
  eventTime = SysNetwork.genUnif(SysNetwork.CPULow,SysNetwork.CPUHigh);  // eventTIme = Ts
  request = SysNetwork.CPURequests.remove(ReqIndex);  // remove first request waiting in request list
  request.setTs(eventTime);       // Gives it its Ts
  request.setCore(core);
  SysNetwork.CPURequests.add(ReqIndex, request);  // insert request back into list
 }
 
 public void setReqSerTime(double time, int ReqIndex){     // set time at which the request is scheduled
  request = SysNetwork.CPURequests.remove(ReqIndex);
  request.setSerTime(time);
  SysNetwork.CPURequests.add(ReqIndex, request);
 }
 
 public void run(){
  
  // remove record of request from schedule
  if (SysNetwork.CPURequests.size()>2){
   Controller.CPUState--;
  }
  // check that the correct request is removed from request schedule
  if (SysNetwork.CPURequests.getFirst().getCoreNum()==request.getCoreNum()) 
   request = SysNetwork.CPURequests.removeFirst();
  else 
   request = SysNetwork.CPURequests.remove(1);
  
  //  for debugging
  if (Controller.time>request.getEndTime())
   System.out.println("sim time less than arrival time");
  Controller.time = request.getEndTime();

  // check if there are other requests pending in queue
  if (SysNetwork.CPURequests.size()>=2) {  // if there are at least 2 requests in the schedule
   
   // create new DeathEvent using the current core
   if (SysNetwork.CPURequests.get(0).getCoreNum() == 0){
    new_death_e = new CPU_death_e(request.getCoreNum(), 0);
    ((CPU_death_e) new_death_e).setReqSerTime(Math.max(request.getEndTime(), new_death_e.request.getArrTime()), 0);
    
   } else {
    new_death_e = new CPU_death_e(request.getCoreNum(), 1);
    ((CPU_death_e) new_death_e).setReqSerTime(Math.max(request.getEndTime(), new_death_e.request.getArrTime()), 1);
   }   
   SysNetwork.insert_new_e(new_death_e);
  }
  
  // determine where the request will go
  double prob = SysNetwork.genUnif(0, 1); // generate number to use as probability of request going to each destination
  if (prob <= 0.5) {       // complete request with 0.5 chance
   if (Controller.time>SysNetwork.sim_time/2){
    Monitor_e.setTqTw(request.getTq(),request.getTs());
   }
  } else {
   if (0.5 < prob && prob <= 0.6){ // go to disk with 0.1 chance
    new_birth_e = new ToDisk(request);    // create "birth event" for disk
   } else {          // go to network with 0.4 chance
    new_birth_e = new ToNet(request);    // create "birth event" for network
   }
   SysNetwork.insert_new_e(new_birth_e);
  }
  // end determining what the next course of action of request is
  
 }
}