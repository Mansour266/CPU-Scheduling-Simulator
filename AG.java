import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AG {
    ArrayList<Process> readyList;
    ArrayList<Process> dieList;
    ArrayList<Process> waitList;
    int currentProcessIndex;

    public AG(Process[] processes) {
        readyList = new ArrayList<>();
        dieList = new ArrayList<>();
        waitList = new ArrayList<>();
        waitList.addAll(Arrays.asList(processes));
        checkArrivals(0);
    }

    public void checkArrivals(int time) {
        for (Process process : List.copyOf(waitList)) {
            if (process.arrivalTime == time) {
                process.agFactor = agFactor(process);
                readyList.add(process);
                waitList.remove(process);
                System.out.println("Process " + process.pid + " arrived at time " + time + " with AG factor " + process.agFactor);
            }
        }
    }

    private int rand() {
        return (int) ((Math.random() * 100) % 20);
    }

    private int agFactor(Process process) {
        int rf = rand();
        int factor = 10;
        if (rf < 10) {
            factor = rf;
        } else if (rf == 10) {
            factor = process.priority;
        }
        return (int) factor + process.arrivalTime + process.burstTime;
    }

    private int getIndexOfHighestAGFactor() {
        Process highestAG = readyList.get(0);
        for (Process process : readyList) {
            if (process.agFactor < highestAG.agFactor)
                highestAG = process;
        }
        return readyList.indexOf(highestAG);
    }

    private int calcMeanQuantum() {
        int sum = 0;
        for (Process process : readyList) {
            sum += process.quantum;
        }
        return sum / readyList.size();
    }

    private void updateQuantum(int index, int quantum) {
        Process temp = readyList.get(index);
        temp.quantum = quantum;
        readyList.set(index, temp);
    }

    private void updateBurstTime(int index, int burstTime) {
        Process temp = readyList.get(index);
        temp.burstTime = burstTime;
        readyList.set(index, temp);
    }

    private void putProcessInEndOfReadyList(Process process) {
        readyList.remove(process);
        readyList.add(process);
    }

    public void run() {
        int time = 0;
        int processQuantumCounter = 0;
        currentProcessIndex = getIndexOfHighestAGFactor();

        while (readyList.size() > 0) {
            System.out.println("Time " + time + ", Process " + readyList.get(currentProcessIndex).pid + " Burst Time: " + readyList.get(currentProcessIndex).burstTime);
            time++;
            processQuantumCounter++;
            updateBurstTime(currentProcessIndex, readyList.get(currentProcessIndex).burstTime - 1);
            int highestAGIndex = getIndexOfHighestAGFactor();
            checkArrivals(time);
            Process currentProcess = readyList.get(currentProcessIndex);
            // First the non-preemptive part
            // if quantum is over and process is not done
            if (currentProcess.burstTime > 0 && currentProcess.quantum - processQuantumCounter == 0) {
                int newQuantum = currentProcess.quantum + (int) Math.ceil(0.10 * calcMeanQuantum());
                updateQuantum(currentProcessIndex, newQuantum);
                putProcessInEndOfReadyList(currentProcess);
                currentProcessIndex = 0;
                processQuantumCounter = 0;
            }
            // if process is done
            else if (currentProcess.burstTime == 0) {
                updateQuantum(currentProcessIndex, 0);
                readyList.remove(currentProcessIndex);
                dieList.add(currentProcess);
                currentProcessIndex = 0;
                processQuantumCounter = 0;
            }
            // Now the preemptive part
            else if (processQuantumCounter >= Math.ceil((double) currentProcess.quantum / 2)) {
                // Preemptive
                if (currentProcess.agFactor < readyList.get(highestAGIndex).agFactor) {
                    // Give the current process the remaining quantum time of itself
                    int newQuantum = currentProcess.quantum - processQuantumCounter;
                    newQuantum += currentProcess.quantum;
                    updateQuantum(currentProcessIndex, newQuantum);
                    currentProcessIndex = highestAGIndex;
                    processQuantumCounter = 0;
                }
            }

        }
    }
}
