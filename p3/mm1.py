"""
Yao Zhang, yazhang@bu.edu
CS 350, Spring 2016
Hw 3
mm1.py

compilation instructions in anaconda:
compile and call main() to start simulation

compilation instructions in putty on csa2:
python3 mm1.py
python3
from mm1 import main
main()

"""

import math
import random

# global variables
# input variables
ts = 0.015
lamb = 50
t_sim = 200

# controller keeps tracks of events and their event times
controller = []

# requests that cant be served because of busy server enter the queue
queue = []

# current time
t_current = 0

server_empty = True

# stat variables
total_q = 0
total_w = 0
monitor_event_count = 0
total_tq = 0
total_tw = 0

# keeps tracks of all requests 
all_r = []

# generates random exp var
def randgen(mean):
    # random exp
    rand = -(mean*math.log(1-random.random()))
    return rand
    
# takes current time and service time as imputs, creates a request and adds it to all_requests
# request: (birth_t, tw, tq, list type)
def add_r(birth_t, wait_t, response_t, l_type):
    # response time = service time
    r = (birth_t, wait_t, response_t, l_type)
    if (l_type == 'all_request'):
        all_r.append(r)
    else:
        queue.append(r)
    
# takes event and adds to controller based on timestamp
# event: (time, type)
def add_e(e):
    index = 0
    e_time, e_type = e
    
    for i in range(0, len(controller)):
        if e_time < controller[i][0]:
            index = i;
            controller.insert(index,e)
            return
    controller.append(e)

# removes event from controller and returns it
def get_e():
    global controller
    
    e = controller[0]
    controller = controller[1:]
    return e

# remove request from queue and returns it
def get_r(l_type):
    global queue, all_r
    if (l_type == 'queue'):
        r = queue[0]
        queue = queue[1:]
        return r
    else:
        r = all_r[0]
        all_r = all_r[1:]
        return r

# checks if there are more events in controller
def more_e():
    return len(controller) > 0

# processes event
def proc_e(e):
    global server_empty, ts, controller, t_current, monitor_event_count, total_w, total_q
    e_time, e_type = e
    
    if (e_type == 'birth'):
        if (server_empty == True):
            print('request entered, server idle')
            
            # server is no longer empty
            server_empty = False
            
            # schedule death request
            service_t = randgen(ts)
            # time of death event
            death_e_t = service_t + t_current
            # create death event
            death_e = (death_e_t, 'death')
            # add death event to controller
            add_e(death_e)
            
            # keep track of request info
            add_r(t_current, 0, service_t, 'all_request')
        else:
            print('request entered, server busy')
            
            # add request to queue
            # default waiting time & response time = 0
            add_r(e_time, 0, 0, 'queue')
            
        # schedule next birth event
        next_birth = randgen(1/lamb)
        # time of birth event
        birth_e_t = next_birth + t_current
        # create birth event
        birth_e = (birth_e_t, 'birth')
        # add birth event to controller
        add_e(birth_e)
            
    elif (e_type == 'death'):
        if (len(queue) != 0):
            print('server serving next request')
            
            # get next request to be served
            r = get_r('queue')
            
            # schedule request death
            service_t = randgen(ts)
            # time of death event
            death_e_t = service_t + t_current
            # create death event
            death_e = (death_e_t, 'death')
            # add death event to controller
            add_e(death_e)
            
            # keep track of request info
            r_birth_t, r_wait_t, r_response_t, l_type = r
            r_wait_t = t_current - r_birth_t
            r_response_t = service_t
            add_r(r_birth_t, r_wait_t, r_response_t, 'all_request')
        else:
            print('server empty')
            
            # no requests to serve
            # indicate that server is empty
            server_empty = True
            
    else: # monitor
        # count current q and w
        print('monitoring event')
        
        monitor_event_count += 1
        total_w += len(queue)
        if (server_empty == False):
            total_q += (len(queue) + 1)
            
        # schedule next monitor event
        monitor_e_t = t_current + randgen(2/lamb)
        monitor_e = (monitor_e_t, 'monitor')
        add_e(monitor_e)

def genstats():
    global all_r, total_tq, total_tw, total_q, total_w, monitor_event_count 
    
    # calculate Tq and Tw    
    size = len(all_r)
    
    while (len(all_r) != 0):
        r = get_r('all_request')
        r_birth_t, r_wait_t, r_response_t, l_type = r
        
        total_tq += r_response_t;
        total_tw += r_wait_t
    
    # print results
    print()
    print('for lambda: ' + str(lamb) + ' Ts: ' + str(ts) + ' sim time: ' + str(t_sim))
    print('Tq is '+ str(total_tq/size))
    print('Tw is '+ str(total_tw/size))    
    print('q is '+ str(total_q/monitor_event_count))    
    print('w is '+ str(total_w/monitor_event_count))
    
def main():
    global t_current
    
    # add initial birth and monitor events
    birth_e_t = randgen(1/lamb)
    birth_e = (birth_e_t, 'birth')
    add_e(birth_e)

    # monitoring starts after time unit 100 and done at double the rate of lamb
    new_lamb = 2*lamb
    monitor_e_t = 100 + randgen(1/new_lamb)
    monitor_e = (monitor_e_t, 'monitor')
    add_e(monitor_e)
    print('initial controller: ' + str(controller))

    while (t_current < t_sim):
        # get next event and update time
        e = get_e()
        e_time, e_type = e
        t_current = e_time
        proc_e(e)
    
    # generating stats
    genstats()
