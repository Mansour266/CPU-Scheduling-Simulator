import java.util.*;

public class SJF {
    // Define core variables for the algorithm.
    private static ArrayList<Process> processes;
    private static ArrayList<Process> executedProcesses;
    private static ArrayList<Integer> times;// Here we store the execution times of each process.
    private static int currentTime = 0;

    // Define variables needed for statistical analysis for the algorithm
    private static int waitingTimes = 0;
    private static int turnAroundTimes = 0;
    private static int time = 0;

    public SJF(ArrayList<Process> processes) { // Default constructor.
        SJF.processes = new ArrayList<Process>(processes);
        executedProcesses = new ArrayList<>();
        times = new ArrayList<>();
    }

    void runSJF() {
        // sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (!processes.isEmpty()) {
            Process shortestJob = findShortestJob(currentTime);

            if (shortestJob == null) {
                currentTime++;
                continue;
            }

            // Here we calculate the statistics of the algorithm.
            turnAroundTimes += currentTime - shortestJob.arrivalTime + shortestJob.burstTime;
            waitingTimes += currentTime - shortestJob.arrivalTime;

            // These variables are used for printing and calculations of scheduling process.
            executedProcesses.add(shortestJob);
            processes.remove(shortestJob);
            times.add(currentTime);

            currentTime += shortestJob.burstTime;
            currentTime++; // here we added 1 to simulate context switching

            // This loop prints the ongoing process in real time.
            while (time <= currentTime - 1) {
                if (time == currentTime - 1 && currentTime - 1 > 0) {
                    System.out.println("At t = " + time + " - Context Switching");
                } else {
                    System.out.println("At t = " + time + " - Process ID: " + shortestJob.pid);
                }

                time++;
            }
        }
        System.out.println("-------");

        for (int i = 0; i < executedProcesses.size(); ++i) {
            Process currentProcess = executedProcesses.get(i);
            System.out.println("Executing process: " + currentProcess.pid +
                    " | Arrival Time: " + currentProcess.arrivalTime +
                    " | Burst Time: " + currentProcess.burstTime +
                    " | Waiting Time: " + (times.get(i) - currentProcess.arrivalTime));
        }

        System.out.println("-------");

        // print statistical data of the scheduling algorithm.
        System.out.println("Average waiting time: " + (double) waitingTimes / executedProcesses.size());
        System.out.println("Average turnaround time: " + (double) turnAroundTimes / executedProcesses.size());
    }

    // This should find the shortest job within the allowed arrival time.
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
