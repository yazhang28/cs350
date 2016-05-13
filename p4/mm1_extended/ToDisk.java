
public class ToDisk extends Event {
 private Event new_death_e;
 
 public ToDisk(Request req) {
  request = req;
  request.setArrTime(Controller.time); // gives request the arrivalTime to Disk
  request.setCore(0);
 }
 
 public void run() {
  
  // add newly born request to the schedule of requests
  SysNetwork.DiskRequests.add(this.request);
  
  // check if service is idle
  if (SysNetwork.DiskRequests.size()==1){ //  if either server was idle upon its birth
   new_death_e = new Disk_death_e(); 
   
   // service that request 
   request = SysNetwork.DiskRequests.remove();   
   // for debugging
   if (Controller.time>this.request.getArrTime())  
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
   
   this.request.setSerTime(Controller.time); // set startTime to now since we're servicing it now
   
   // predict when service will finish and schedule death event
   SysNetwork.DiskRequests.addFirst(request);
   
   SysNetwork.insert_new_e(new_death_e); // insert new DeathEvent into event schedule 
  } else if (SysNetwork.DiskRequests.size()>1){ // if there is more than one request in system
   Controller.DiskState++;  // increase w
   // for debugging
   if (Controller.time>this.request.getArrTime())
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
  }
  // stop check to see if service can be started instantly
 }
}