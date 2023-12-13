import java.util.*;
public class SJF {
    private static Process[] processes;

    public SJF(Process[] processes) {
        this.processes = processes;
    }

    void run(){
        // sort processes by burst time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.burstTime));

        int currentTime = 0;

        for (Process process : processes) {
            if (process.arrivalTime > currentTime) {
                currentTime = process.arrivalTime;
            }

            System.out.println("Executing process: " + process.pid +  " | Arrival Time: " + process.arrivalTime +
                    " | Burst Time: " + process.burstTime + " | Waiting Time: " + (currentTime - process.arrivalTime));

            currentTime += process.burstTime;
        }
    }
}
