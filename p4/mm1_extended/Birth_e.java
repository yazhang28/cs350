
public class Birth_e extends Event {
 private Event new_death_e;
 private boolean create_birth; 
 
 public Birth_e() {
  eventTime = SysNetwork.genExp(SysNetwork.lambda);  // eventTime = IAT
  request.setIAT(eventTime);  // give request the IAT time of Birth_e
  request.setArrTime(Controller.time+eventTime); // give request the arrivalTime of Birth_e
  create_birth = true;
 }
 
 public Birth_e(double arrivalTime, Request req){
  request = req;
  request.setArrTime(arrivalTime);
  create_birth = false;
 }
 
 public void run() {
  // add newly born request to the schedule of requests
  SysNetwork.CPURequests.add(this.request);
  
  // check if service is idle
  if (SysNetwork.CPURequests.size()==1){ //  if either server was idle upon its birth
   new_death_e = new CPU_death_e(1,0); 
   // service that request
   request = SysNetwork.CPURequests.remove();   
   //  for debugging
   if (Controller.time>this.request.getArrTime())  
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
   
   this.request.setSerTime(Controller.time); // set startTime to now since we're servicing it now
   
   // predict when service will finish and schedule death event
   SysNetwork.CPURequests.addFirst(request);
   SysNetwork.insert_new_e(new_death_e); // insert new DeathEvent into event schedule 
  } else if (SysNetwork.CPURequests.size()==2){
   if (SysNetwork.CPURequests.getFirst().getCoreNum()==1){
    new_death_e = new CPU_death_e(2,1);
   } else {
    new_death_e = new CPU_death_e(1,1);
   }
   
   request = SysNetwork.CPURequests.remove(1);   
   //  for debugging 
   if (Controller.time>this.request.getArrTime())  
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
   
   this.request.setSerTime(Controller.time); // set startTime to now since we're servicing it now
   
   SysNetwork.CPURequests.add(1, request);
   // Check where to put the new DeathEvent in schedule
   SysNetwork.insert_new_e(new_death_e); // insert new DeathEvent into event schedule 
   
  } else if (SysNetwork.CPURequests.size()>2){ // if there is more than one request in system
   Controller.CPUState++;  // increase w
   //  for debugging 
   if (Controller.time>this.request.getArrTime())
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
  }
  // stop check to see if service can be started instantly
  
  // schedule next birth event
  if (create_birth == true){
   Event new_birth_e = new Birth_e();
   SysNetwork.insert_new_e(new_birth_e);
  }
 }
}