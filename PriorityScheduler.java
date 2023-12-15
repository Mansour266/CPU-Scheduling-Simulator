import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class PriorityScheduler {

    // Steps to do
    // ------------------    
    // Take a list of processes
    // According to their arrival time i will put them in the priority queue
    // As the time increases i will check if the burst time of the current process is over
    // If it is then it is removed and the next process with the highest priority is executed
    // If during the burst time another process is added to the queue then it will be sorted according to its priority
    // If 2 processes have the same priority then the one with the lower arrival time will be executed first

    
    // Possible Bugs:
    // --------------
    // If a process is finished, another one cant start at the same time (FIXED)
    // Check if aging works to prevent starvation
    // There is a problem where the priorities aren't sorted correctly
    
    // Need to run more tests, otherwise currently fine
    
    
    int time;
    private ArrayList<Process> processes;
    private HashMap<Process, Integer> executedProcesses = new HashMap<Process, Integer>();

    // Priority queue to sort the processes according to their priority and arrival time
    PriorityQueue<Process> readyQueue = new PriorityQueue<Process>(new Comparator<Process>() {
        @Override
        public int compare(Process p1, Process p2) {
            // Compare based on priority, and if priorities are equal, compare based on arrival time
            return Integer.compare(p1.priority, p2.priority) == 0
                    ? Integer.compare(p1.arrivalTime, p2.arrivalTime)
                    : Integer.compare(p1.priority, p2.priority);
        }
    });
    
    
    // Constructor to sort the processes according to their arrival time
    public PriorityScheduler(ArrayList<Process> processes) {
        this.processes = processes;
        processes.sort(new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return Integer.compare(p1.arrivalTime, p2.arrivalTime);
            }
        });
    }

    public void run() {
        time = 0;
        int execTime = 0;
        Process currentProcess = null;
        System.out.println();
        
        while (processes.size() > 0 || !readyQueue.isEmpty() || currentProcess != null) {
            
            // Solving starvation by increasing lower processes' priority every 5 seconds 
            if (time % 5 == 0 && !readyQueue.isEmpty()) {
                int highestPriority = readyQueue.peek().priority;
                
                for(Process process : readyQueue){
                    if (process.priority < highestPriority) {
                        process.priority++;
                    }
                }
            }
            
            // Loading processes into the ready queue according to the current time and their arrival time
            if (!processes.isEmpty()) {
                
                // Had to implement this cause it gives an error if it loops over the ArrayList after clearing it
                Iterator<Process> iterator = processes.iterator();
                while (iterator.hasNext()) {
                    Process process = iterator.next();
                    if (process.arrivalTime == time) {
                        readyQueue.add(process);
                        iterator.remove();  // Use iterator's remove method to avoid ConcurrentModificationException
                    }
                }
            }
            
            // Putting the executed process into a hashmap along with the time it finished
            if (currentProcess != null  && currentProcess.burstTime == time - execTime) {
                executedProcesses.put(currentProcess, execTime);
                currentProcess = null;
            }

            // Making sure the current process is empty first before assigning it to another process (Non-preemptive)
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                execTime = time;
            }
            

            if (currentProcess == null) {
                System.out.println("At t = " + time + " - CPU IDLE");
            }
            else{
                System.out.println("At t = " + time + " - Process ID: " + currentProcess.pid + " - Priority: " + currentProcess.priority);
            }
            
            time++;
        }
        
        System.out.println("------");
        
        for (Map.Entry<Process, Integer> process : executedProcesses.entrySet()){
            System.out.println("Executing process: " + process.getKey().pid +
            " | Arrival Time: " + process.getKey().arrivalTime +
            " | Burst Time: " + process.getKey().burstTime +
            " | Priority: " + process.getKey().priority +
            " | Waiting Time: " + (process.getValue() - process.getKey().arrivalTime));
        }
        System.out.println("------");
    }
}

// Output:
// Print the execution order of processes.
// Display waiting time and turnaround time for each process.
// Calculate and print the average waiting time and average turnaround time.

// Preemptive means that the CPU can be taken away from a process without its cooperation and with an intention to give the CPU to another process.

// Waiting time: the amount of time a process spends waiting in the ready queue.
// It is calculated as the difference between the total time a process spends in the system and its burst time.
// Waiting Time = Turnaround Time - Burst Time

// Turnaround time: the amount of time a process spends in the system.
// It is calculated as the difference between the completion time and the arrival time.
// Turnaround Time = Completion Time - Arrival Time

// Completion time: the time at which a process completes its execution.
// It is calculated as the sum of the arrival time and the burst time.
// Completion Time = Arrival Time + Burst Time

// Process Arrival Time: the time at which the process enters the ready queue and becomes available for execution.
// Process Burst Time: the amount of time a process requires on the CPU to complete its execution.
// Process Priority Number: a value assigned to the process based on its importance or urgency.

// Starvation: it can happen if processes with lower priority numbers are continuously preempted or delayed in favor of higher-priority processes.
// To prevent starvation, various techniques can be employed, such as aging, which involves gradually increasing the priority of a process over time. 
// This ensures that even low-priority processes eventually get a chance to execute.

// Round Robin Scheduling: a preemptive scheduling algorithm that assigns a fixed time unit, known as time quantum, to each process in the ready queue.
// If the process completes its execution within the assigned time quantum, it is removed from the ready queue.
// If the process does not complete its execution within the assigned time quantum, it is preempted and added to the tail of the ready queue.

// Shortest Job First Scheduling: a non-preemptive scheduling algorithm that assigns the CPU to the process with the shortest burst time.
// Shortest Remaining Time First Scheduling: a preemptive scheduling algorithm that assigns the CPU to the process with the shortest remaining burst time.

// Context Switching: the process of saving the context of one process and loading the context of another process.
// Switching Context Time: the time required to save the context of one process and load the context of another process.