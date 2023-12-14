import java.util.*;
public class SJF {
    //Define core variables for the algorithm.
    private static Process[] processes;
    private static List<Process> executedProcesses;
    private static List<Integer> times;//Here we store the execution times of each process.
    private static int currentTime = 0;

    //Define variables needed for statistical analysis for the algorithm
    private static int waitingTimes = 0;
    private static int turnAroundTimes = 0;
    private static int time = 0;

    public SJF(Process[] processes) { //Default constructor.
        this.processes = processes;
        this.executedProcesses = new ArrayList<>();
        this.times = new ArrayList<>();
    }

    void runSJF(){
        // sort processes by arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        while (processes.length > 0) {
            Process shortestJob = findShortestJob(currentTime);

            if (shortestJob == null) {
                currentTime++;
                continue;
            }

            //Here we calculate the statistics of the algorithm.
            turnAroundTimes += currentTime  - shortestJob.arrivalTime + shortestJob.burstTime;
            waitingTimes += currentTime - shortestJob.arrivalTime;

            currentTime += shortestJob.burstTime;
            currentTime++; //here we added 1 to simulate context switching

            //These variables are used for printing and calculations of scheduling process.
            executedProcesses.add(shortestJob);
            processes = removeProcess(shortestJob);
            times.add(currentTime);

            //This loop prints the ongoing process in real time.
            while (time <= currentTime - 1) {
                if(time == currentTime - 1 && currentTime - 1 > 0){
                    System.out.println("At t=" + time + " - Context Switching");
                }
                else{
                    System.out.println("At t=" + time + " - Process ID: " + shortestJob.pid);
                }

                time++;
            }
        }
        System.out.println("-------");

        for (int i = 0;i < executedProcesses.size(); ++i){
            Process currentProcess = executedProcesses.get(i);
            System.out.println("Executing process: " + currentProcess.pid +
                    " | Arrival Time: " + currentProcess.arrivalTime +
                    " | Burst Time: " + currentProcess.burstTime +
                    " | Waiting Time: " + (times.get(i) - currentProcess.arrivalTime));
        }

        System.out.println("-------");

        //print statistical data of the scheduling algorithm.
        System.out.println("Average waiting time: " + (double)waitingTimes / executedProcesses.size());
        System.out.println("Average turnaround time: " + (double)turnAroundTimes / executedProcesses.size());
    }

    //This method removes the process after execution
    private Process[] removeProcess(Process shortestJob) {
        Process[] newProcesses = new Process[processes.length - 1];
        int i = 0;
        for (Process process : processes) {
            if (process != shortestJob) {
                newProcesses[i++] = process;
            }
        }
        return newProcesses;
    }

    //This should find the shortest job within the allowed arrival time.
    private Process findShortestJob(int currentTime) {
        Process shortestJob = null;
        for (Process process : processes) {
            if (process.arrivalTime <= currentTime) {
                if (shortestJob == null || process.burstTime < shortestJob.burstTime) {
                    shortestJob = process;
                }
            }
        }
        return shortestJob;
    }
}
