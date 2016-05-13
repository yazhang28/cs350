public class ToNet extends Event {
 private Event new_death_e;
 
 public ToNet(Request req) {
  request = req;
  request.setArrTime(Controller.time); // gives request the arrivalTime to Net
  request.setCore(0);
 }
 
 public void run() {
  
  // add newly born request to the schedule of requests
  SysNetwork.NetRequests.add(this.request);
  
  // check if service is idle
  if (SysNetwork.NetRequests.size()==1){ //  if server was idle upon its birth
   new_death_e = new Net_death_e();
   
   // service that request 
   request = SysNetwork.NetRequests.remove();   
   // for debugging
   if (Controller.time>this.request.getArrTime())  
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
   // end debugging
   this.request.setSerTime(Controller.time); // set startTime to now since we're servicing it now
   // predict when service is complete
   SysNetwork.NetRequests.addFirst(request);
   
   // schedule death event
   SysNetwork.insert_new_e(new_death_e); // insert new DeathEvent into event schedule 
  } else if (SysNetwork.NetRequests.size()>1){ // if there is more than one request in system
   Controller.NetState++;  // increase w
   Controller.time = this.request.getArrTime();
  }
  // stop check to see if service can be started instantly
 }
}