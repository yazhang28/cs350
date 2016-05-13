"""
Yao Zhang, yazhang@bu.edu
CS 350, Spring 2016
Hw 4
mm1k.py

compilation instructions in anaconda:
compile and call main('mm1k') or main('md1') or main('mm1')

compilation instructions in putty on csa2:
python3 mm1.py
python3
from mm1 import main
main('mm1k',  50, 0.02, 200) # to run simulation on mm1k system with lambda=50, Ts = 0.02 ms, simulation time=200 ms
main('md1',  50, 0.02, 200) # to run simulation on md1 system with lambda=50, Ts = 0.02 ms, simulation time=200 ms
main('mm1',  50, 0.02, 200) # to run simulation on md1 system with lambda=50, Ts = 0.02 ms, simulation time=200 ms
"""

import math
import random

# global variables
# input variables
ts = 0
lamb = 0
t_sim = 0
K = 3
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

# array containing qs, tqs for calculating stdev & confidence 
qs = [] 
tqs = []
rejects = 0

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
def proc_e(e, system):
    global server_empty, ts, controller, t_current, monitor_event_count, total_w, total_q, rejects
    e_time, e_type = e
    
    if (e_type == 'birth'):
        if (server_empty == True):
            print('request entered, server idle')
            
            # server is no longer empty
            server_empty = False
            
            # schedule death request
            if system == 'md1':
                service_t = ts
            else:
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
            
            '''            
            for system = MM1K or MD1: if queue size = K reject incoming packet
            for system = MM1 add request to queue
            default waiting time & response time = 0
            '''
            
            if system == 'mm1':
                add_r(e_time, 0, 0, 'queue')
            else:
                # MM1K or MD1 for K = 3
                if len(queue) <= 3:
                    add_r(e_time, 0, 0, 'queue')
                else:
                    print('buffer is full, rejecting packet')
                    rejects += 1
                
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
            if system == 'md1':
                service_t = ts
            else:
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
            #changed
            qs.append(len(queue) + 1)
            
        # schedule next monitor event
        monitor_e_t = t_current + randgen(2/lamb)
        monitor_e = (monitor_e_t, 'monitor')
        add_e(monitor_e)

def genstats(system):
    global all_r, total_tq, total_tw, total_q, total_w, monitor_event_count, rejects
    
    # calculate Tq and Tw    
    size = len(all_r)
    while (len(all_r) != 0):
        r = get_r('all_request')
        r_birth_t, r_wait_t, r_response_t, l_type = r
        
        total_tq += r_response_t;
        tqs.append(r_response_t)
        total_tw += r_wait_t

    tq_avg = total_tq/size
    tw_avg = total_tw/size
    q_avg = total_q/monitor_event_count
    w_avg = total_w/monitor_event_count

    total_std_q = 0
    total_std_tq = 0

    for i in range(0, len(tqs)):
        total_std_tq += (tq_avg - tqs[i])**2
        
    for i in range(0, len(qs)):
        total_std_q += (q_avg - qs[i])**2        
    
    # stdev tq
    stdev_tq = math.sqrt(total_std_tq/len(tqs))
    # stev q
    stdev_q = math.sqrt(total_std_q/len(qs))
    
    Z = 1.96 # for a 96% confidence interval    
    
    # error tq
    e_tq = Z*(stdev_tq/math.sqrt(len(tqs)))
    # error q
    e_q = Z*(stdev_q/math.sqrt(len(qs)))
    
    # confidence interval tq
    ci_tq = [tq_avg - e_tq, tq_avg + e_tq]
    # confidence interval q
    ci_q = [q_avg - e_q, q_avg + e_q]

    # print results
    print()
    if system == 'mm1k':
        print('MM1K: for lambda = ' + str(lamb) + ', Ts = ' + str(ts) + ', sim time = ' + str(t_sim) + ', K = ' + str(K))
    elif system == 'md1':
        print('MD1: for lambda = ' + str(lamb) + ', Ts = ' + str(ts) + ', sim time = ' + str(t_sim) + ', K = ' + str(K))
    else:
        print('MM1: for lambda = ' + str(lamb) + ', Ts = ' + str(ts) + ', sim time = ' + str(t_sim))
        
    print('Tq is '+ str(tq_avg))
    print('Tw is '+ str(tw_avg))    
    print('q is '+ str(q_avg))    
    print('w is '+ str(w_avg))
    
    print('confidence interval for q is '+ str(ci_q))
    print('confidence interval for Tq is '+ str(ci_tq))

    # rejection rate
    reject_rate = rejects/size   
    print('reject rate: ' + str(reject_rate))
    print()
    
def expected(system):
    if system == 'mm1k':
        print('analytical MM1k: lambda = ' + str(lamb) + ', Ts = ' + str(ts))
                
        q = 0
        pr_reject = 0
        rho = lamb*ts
        
        if rho == 1:
            print('p is 1')
            q = K/2
            pr_reject = 1/(1+K)
        else:
            print('p = ' + str(rho))
            
            left = rho/(1-rho)
            top = (K+1)*((rho)**(K+1))
            bottom = 1-(rho**(K+1))
            right = top/bottom
            
            q = left-right
    
            top2 = (1-rho)*(rho**K)
            bottom2 = (1-(rho**(K+1)))    
            
            pr_reject = top2/bottom2
            
        lamb_p = lamb*(1-pr_reject)
        tq = q/lamb_p
        
        print('tq: ' + str(tq))
        print('q: ' + str(q))
        print('reject probability: ' + str(pr_reject))
    else: 
        return

# input system type ('mm1k', 'md1', 'mm1'), lambda, Ts (ms), simulation time (ms)
def main(system, in_lamb, in_ts, in_t_sim):
    global t_current,ts,lamb,t_sim
    
    # set global variables to input values
    ts = in_ts
    lamb = in_lamb
    t_sim = in_t_sim
    
    # add initial birth and monitor events
    birth_e_t = randgen(1/lamb)
    birth_e = (birth_e_t, 'birth')
    add_e(birth_e)

    # monitoring starts after time unit 100 and done at double the rate of lamb
    new_lamb = 2*lamb
    monitor_e_t = 100 + randgen(1/new_lamb)
    monitor_e = (monitor_e_t, 'monitor')
    add_e(monitor_e)

    while (t_current < t_sim):
        # get next event and update time
        e = get_e()
        e_time, e_type = e
        t_current = e_time
        #changed
        proc_e(e, system)
    
    # generating stats
    genstats(system)
    expected(system)
    reset()

# resets all global variables in case simulation is run another time
def reset():
    global queue,t_current,server_empty,total_q,total_w,monitor_event_count,total_tq,total_tw,qs,tqs,rejects,all_r
    queue = []
    t_current = 0
    server_empty = True
    total_q = 0
    total_w = 0
    monitor_event_count = 0
    total_tq = 0
    total_tw = 0
    qs = [] 
    tqs = []
    rejects = 0
    all_r = []