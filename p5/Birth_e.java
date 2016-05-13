
public class Birth_e extends Event {
 
 public Birth_e(int type) {
  switch (type){
  case 1:
   eventTime = MM1System.genExp(MM1System.CPU_lambda);  //eventTime = IAT
   request.setTs(MM1System.genExp(1/MM1System.CPU_Ts));  //Gives request the Ts
   break;
  case 2:
   eventTime = MM1System.genExp(MM1System.IO_lambda);  // eventTime = IAT
   request.setTs(MM1System.genExp(1/MM1System.IO_Ts));  // give request the Ts
   break;
  }
  request.setReqType(type);  // give request its type
  request.setIAT(eventTime);  // give request the IAT time of birth event
  request.setArrTime(Controller.time+eventTime); // give request the arrivalTime of birth event
 }
 
 public void run() {
  // for debugging
  if (Controller.time>this.request.getArrTime())  
   System.out.println("sim time less than arrival time");
  Controller.time = this.request.getArrTime();
  // add a newly born request to the schedule of requests
  MM1System.insertNewRequest(request);
  //if new request is the only one in the system 
  //  ie server was idle upon its birth,
  if ((MM1System.requestSchedule.size()==1)){ 
   Event newDeath_e = new Death_e(); 
   // start service for that request 
   request = MM1System.requestSchedule.remove();
   
   // for debugging
   if (Controller.time>this.request.getArrTime())  
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
   
   this.request.setSerTime(Controller.time); // set startTime to now since servicing it now
   // predict when service is done and schedule death event
   MM1System.requestSchedule.addFirst(request);
   MM1System.insertNewEvent(newDeath_e);
  }
  else if (MM1System.requestSchedule.size()>1){ // if there is more than 1 request in system
   Controller.state++;  // increase w
   // for debugging
   if (Controller.time>this.request.getArrTime())
    System.out.println("sim time less than arrival time");
   Controller.time = this.request.getArrTime();
  }
  //Schedule the next birth event
  Event new_birth_e = new Birth_e(request.getReqType());
  MM1System.insertNewEvent(new_birth_e);
 }
}