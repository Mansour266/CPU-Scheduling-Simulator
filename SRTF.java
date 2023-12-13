import java.util.ArrayList;

public class SRTF {

    int totalWaitingTimes;
    int totalTurnAroundTimes;
    ArrayList<String> schedulerData;
    private final int REMAINING_TIME_WEIGHT = 2;
    private final int AGING_PRIORITY_WEIGHT = 3;


    class SRTFProcess{
        int pid;
        int arrivalTime;
        int burstTime;
        int priority;
//        RGBColor color;
        int remainingTime;
        int agePriorityLevel;

        SRTFProcess(int pid, int arrivalTime, int burstTime, int priority){
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
            this.remainingTime = burstTime;
            agePriorityLevel = 0;
        }
    }
    ArrayList<SRTFProcess> readyQueue;
    SRTF(ArrayList<Process> processes){
        readyQueue = new ArrayList<SRTFProcess>();
        for(Process p :processes){
            readyQueue.add(new SRTFProcess(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }
        totalWaitingTimes = 0;
        totalTurnAroundTimes = 0;
        schedulerData = new ArrayList<String>();
    }

    void addProcess(Process p){
        readyQueue.add(new SRTFProcess(p.pid, p.arrivalTime, p.burstTime, p.priority));
    }

    public void runSRTF(){
        int currentTime = 0;

        while(!readyQueue.isEmpty()){

            // increase the priority of all processes due to aging every 2 clock counts
            // to avoid overhead of increasing it every second
            if (currentTime%2==0){
                for(SRTFProcess p : readyQueue){
                    p.agePriorityLevel++;
                }
            }

            int currentProcessIndex = 0, weight = -1;

            int len = readyQueue.size();
            for (int i = 0; i < len; i++){
                if(readyQueue.get(i).remainingTime*REMAINING_TIME_WEIGHT
                        + readyQueue.get(i).agePriorityLevel*AGING_PRIORITY_WEIGHT > weight){
                    currentProcessIndex = i;
                }
            }

            readyQueue.get(currentProcessIndex).remainingTime--;

            SRTFProcess srtfProcess = readyQueue.get(currentProcessIndex);
            System.out.println("At t="+currentTime+ " - Process ID: " + srtfProcess.pid);

            if (srtfProcess.remainingTime == 0){
                // waiting time is currentTime+1-burstTime-arrivalTime
                // turnaround time is the currentTime+1-arrivalTime
                // average waiting time = waiting time of all processes / n
                // average turnaround time = turnaround time of all processes / n
                int waitingTime = currentTime+1-srtfProcess.burstTime-srtfProcess.arrivalTime;
                int turnAroundTime = currentTime+1-srtfProcess.arrivalTime;
                totalTurnAroundTimes+=turnAroundTime;
                totalWaitingTimes+=waitingTime;

                schedulerData.add("Process: " + srtfProcess.pid + " | Waiting Time: "
                        + waitingTime + " | Turn Around Time: " + turnAroundTime);

                readyQueue.remove(currentProcessIndex);
            }

            currentTime++;

        }

    }

    public void getSchedulingStatistics(){
        for (String s:schedulerData){
            System.out.println(s);
        }
    }

}
