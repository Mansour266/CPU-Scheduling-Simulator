
import java.util.ArrayList;
import java.util.Iterator;

public class SRTFScheduler {
    int totalWaitingTimes;
    int totalTurnAroundTimes;
    ArrayList<String> schedulerData;
    int numOfProcesses; // num of processes in input processes
    int completedProcesses;
    private final double REMAINING_TIME_WEIGHT = 1;
    private final double AGING_PRIORITY_WEIGHT = 0;

    class SRTFProcess{
        int pid;
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int agePriorityLevel;

        SRTFProcess(int pid, int arrivalTime, int burstTime){
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
            this.agePriorityLevel = 0;

        }
    }

    ArrayList<SRTFProcess> readyQueue;
    ArrayList<Process> inputProcesses;
    SRTFScheduler(ArrayList<Process> processes){
        numOfProcesses = processes.size();
        readyQueue = new ArrayList<SRTFProcess>();
        inputProcesses = processes;
        totalTurnAroundTimes = 0;
        totalWaitingTimes = 0;
        completedProcesses = 0;
        schedulerData = new ArrayList<String>();
    }

    void addSRTFProcess(SRTFProcess p){
        readyQueue.add(p);
    }


    void runSRTF(){
        int currentTime = 0;

        while (completedProcesses<numOfProcesses){

            /* increase age priority level for all processes in ready queue every
            *  2 seconds (aging) to solve the problem of starvation */
            if (!readyQueue.isEmpty() && currentTime%2==0){
                for(SRTFProcess p : readyQueue){
                    p.agePriorityLevel++;
                }
            }


            Iterator<Process> iterator = inputProcesses.iterator();
            while (iterator.hasNext()){
                Process p = iterator.next();
                if (currentTime>=p.arrivalTime){
                    readyQueue.add(new SRTFProcess(p.pid, p.arrivalTime, p.burstTime));
                    iterator.remove();
                }
            }


            if (readyQueue.isEmpty()){
                System.out.println("At t=" + currentTime + " CPU IDLE");
                currentTime++;
            } else {

                int currentProcessIndex = -1;
                double weight = -1;
                int len = readyQueue.size();
                for (int i = 0; i < len; i++){
                    double currentWeight = ((double)1/readyQueue.get(i).remainingTime) * REMAINING_TIME_WEIGHT
                            + readyQueue.get(i).agePriorityLevel * AGING_PRIORITY_WEIGHT;
                    if (currentWeight > weight){
                        currentProcessIndex = i;
                        weight = currentWeight;
                    }

                }

                readyQueue.get(currentProcessIndex).remainingTime--;

                SRTFProcess srtfProcess = readyQueue.get(currentProcessIndex);
                System.out.println("At t=" + currentTime + " - Process ID: " + srtfProcess.pid);

                if (srtfProcess.remainingTime==0){
                    int waitingTime = currentTime+1-srtfProcess.burstTime-srtfProcess.arrivalTime;
                    int turnAroundTime = currentTime+1-srtfProcess.arrivalTime;

                    totalTurnAroundTimes+=turnAroundTime;
                    totalWaitingTimes+=waitingTime;

                    schedulerData.add("Process: " + srtfProcess.pid + " | Waiting Time: "
                            + waitingTime + " | Turnaround Time: " + turnAroundTime);

                    readyQueue.remove(currentProcessIndex);
                    completedProcesses++;

                }

                currentTime++;
            }

        }

        System.out.println("-------");
        for (String s: schedulerData){
            System.out.println(s);
        }


        double averageWaitingTime = (double)totalWaitingTimes/numOfProcesses;
        double averageTurnaroundTime = (double)totalTurnAroundTimes/numOfProcesses;

        System.out.println("-------");
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("-------");
        System.out.println("END OF SRTF SCHEDULER");
        System.out.println("-------");
    }

}
