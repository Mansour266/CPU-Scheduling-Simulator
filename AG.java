import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AG {
    ArrayList<Process> readyList;
    ArrayList<Process> dieList;
    ArrayList<Process> waitList;
    int currentProcess;

    public AG(Process[] processes){
        readyList = new ArrayList<>();
        dieList = new ArrayList<>();
        waitList = new ArrayList<>();
        waitList.addAll(Arrays.asList(processes));
        checkArrivals(0);
    }

    public void checkArrivals(int time){
        for (Process process: List.copyOf(waitList)){
            if (process.arrivalTime == time){
                process.agFactor = agFactor(process);
                readyList.add(process);
                waitList.remove(process);
            }
        }
    }
    private int rand(){
        return (int) ((Math.random() * 100) % 20);
    }
    private int agFactor(Process process){
        int rf = rand();
        int factor = 10;
        if (rf < 10){
            factor = rf;
        }
        else if (rf == 10){
            factor = process.priority;
        }
        return (int) factor + process.arrivalTime + process.burstTime;
    }

    private int getHighestPriority(){
        Process highestAG = readyList.get(0);
        for (Process process: readyList){
            if (process.agFactor < highestAG.agFactor){
                highestAG = process;
            }
        }
        return readyList.indexOf(highestAG);
    }
    private int calcMeanQuantum(){
        int sum = 0;
        for (Process process : readyList){
            sum += process.quantum;
        }
        return (int) (sum / readyList.size());
    }
    private void updateQuantum(int index, int quantum){
        Process temp = readyList.get(index);
        temp.quantum = quantum;
        readyList.set(index, temp);
    }
    private void updateBurstTime(int index, int burstTime){
        Process temp = readyList.get(index);
        temp.burstTime = burstTime;
        readyList.set(index, temp);
    }
    public void run(){
        int time = 0;
        int currentQuantum = 0;
        currentProcess = getHighestPriority();

        while (readyList.size() > 0){
            System.out.println("Time " + time + ", Process " + readyList.get(currentProcess).pid);
            checkArrivals(time);
            time++;
            currentQuantum++;
            updateBurstTime(currentProcess, readyList.get(currentProcess).burstTime - 1);
            int highestAG = getHighestPriority();

            //First the non-preemptive part
            // if quantum is over and process is not done
            if (readyList.get(currentProcess).burstTime > 0 && readyList.get(currentProcess).quantum == 0){
                int tempQ = readyList.get(currentProcess).quantum + (int) Math.ceil(0.10 * calcMeanQuantum());
                updateQuantum(currentProcess, tempQ);
                currentProcess = highestAG;
            }
            // if process is done
            else if (readyList.get(currentProcess).burstTime == 0){
                updateQuantum(currentProcess, 0);
                readyList.remove(currentProcess);
                dieList.add(readyList.get(currentProcess));
            }

            // Now the preemptive part
            else
            if (currentQuantum >= Math.ceil((double) readyList.get(currentProcess).quantum / 2)){
                // Preemptive
                if (readyList.get(currentProcess).agFactor < readyList.get(highestAG).agFactor){
                    // Give the current process the remaining quantum time of itself
                    int tempQ = readyList.get(currentProcess).quantum - currentQuantum;
                    tempQ += readyList.get(currentProcess).quantum;
                    updateQuantum(currentProcess, tempQ);
                    currentProcess = highestAG;
                }
            }

        }
    }
}
