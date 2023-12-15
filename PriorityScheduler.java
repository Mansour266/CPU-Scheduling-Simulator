import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class PriorityScheduler {
    private ArrayList<Process> processes;
    private HashMap<Process, Integer> executedProcesses = new HashMap<Process, Integer>();

    // Priority queue to sort the processes according to their priority and arrival time.
    PriorityQueue<Process> readyQueue = new PriorityQueue<Process>(new Comparator<Process>() {
        @Override
        public int compare(Process p1, Process p2) {
            // Compare based on priority, and if priorities are equal, compare based on arrival time.
            return Integer.compare(p1.priority, p2.priority) == 0
                    ? Integer.compare(p1.arrivalTime, p2.arrivalTime)
                    : Integer.compare(p2.priority, p1.priority);
        }
    });
    
    // Constructor to sort the processes according to their arrival time.
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
        int time = 0;
        int execTime = 0;
        Process currentProcess = null;
        System.out.println();
        
        // This is to check if there's any process still executing or if there are any in the ready queue so it doesn't skip them.
        while (processes.size() > 0 || !readyQueue.isEmpty() || currentProcess != null) {
            
            // Solving starvation by increasing lower processes' priority every 5 seconds (Aging).
            // Put it here so that if a new process is added after, it's priority doesn't increase right away, and should stay 5 seconds first.
            if (time % 5 == 0 && !readyQueue.isEmpty()) {
                int highestPriority = readyQueue.peek().priority;
                
                for(Process process : readyQueue){
                    if (process.priority < highestPriority) {
                        process.priority++;
                    }
                }
            }
            
            // Loading processes into the ready queue according to the current time and their arrival time.
            if (!processes.isEmpty()) {
                
                // Had to implement this cause it gives an error if it loops over the ArrayList after clearing it.
                Iterator<Process> iterator = processes.iterator();
                while (iterator.hasNext()) {
                    Process process = iterator.next();
                    if (process.arrivalTime == time) {
                        readyQueue.add(process);
                        iterator.remove();
                    }
                }
            }
            
            // Putting the executed process into a hashmap along with the time it finished.
            if (currentProcess != null  && currentProcess.burstTime == time - execTime) {
                executedProcesses.put(currentProcess, execTime);
                currentProcess = null;
            }

            // Making sure the current process is empty first before assigning it to another process (Non-preemptive).
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                execTime = time;
            }

            // Checks if there is a process to be executed or if the CPU should stay IDLE.
            if (currentProcess == null) {
                System.out.println("At t = " + time + " - CPU IDLE");
            }
            else{
                System.out.println("At t = " + time + " - Process ID: " + currentProcess.pid + " - Priority: " + currentProcess.priority);
            }
            
            time++;
        }
        
        System.out.println("-------");
        double averageTurnaroundTime = 0, averageWaitingTime = 0, processCount = 0;

        // Print statistics about each process
        for (Map.Entry<Process, Integer> process : executedProcesses.entrySet()){
            int waitingTime = process.getValue() - process.getKey().arrivalTime;
            int turnaroundTime = waitingTime + process.getKey().burstTime;

            System.out.println("Executing process: " + process.getKey().pid +
            " | Arrival Time: " + process.getKey().arrivalTime +
            " | Burst Time: " + process.getKey().burstTime +
            " | Priority: " + process.getKey().priority +
            " | Waiting Time: " + waitingTime +
            " | Turnaround Time: " + turnaroundTime);

            averageTurnaroundTime += turnaroundTime;
            averageWaitingTime += waitingTime;
            processCount++;
        }

        averageTurnaroundTime /= processCount;
        averageWaitingTime /= processCount;

        // Print average waiting and turnaround times.
        System.out.println("-------");
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("-------");
        System.out.println("END OF PRIORITY SCHEDULER");
        System.out.println("-------");
    }
}