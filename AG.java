import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AG {
    ArrayList<Process> readyList;
    ArrayList<Process> dieList;
    ArrayList<Process> waitList;
    HashMap<Integer, Integer> processesBurstTime;
    HashMap<Integer, Integer> waitingTimes;
    HashMap<Integer, Integer> turnaroundTimes;
    int currentProcessIndex;
    double avgWaitingTime;
    double avgTurnaroundTime;

    public AG(ArrayList<Process> processes) {
        this.processesBurstTime = new HashMap<>();
        readyList = new ArrayList<>();
        dieList = new ArrayList<>();
        waitList = new ArrayList<>();
        waitingTimes = new HashMap<>();
        turnaroundTimes = new HashMap<>();
        waitList.addAll(processes);
        for (Process process : processes)
            processesBurstTime.put(process.pid, process.burstTime);
        avgWaitingTime = 0;
        avgTurnaroundTime = 0;
    }

    public void checkArrivals(int time) {
        for (Process process : List.copyOf(waitList)) {
            if (process.arrivalTime == time) {
                process.agFactor = agFactor(process);
                readyList.add(process);
                waitList.remove(process);
            }
        }
    }

    private int rand() {
        return (int) ((Math.random() * 100) % 20);
    }

    private int agFactor(Process process) {
        int rf = rand();
        int factor = 10;
        if (rf < 10)
            factor = rf;
        else if (rf == 10)
            factor = process.priority;
        return (int) factor + process.arrivalTime + process.burstTime;
    }

    private int getIndexOfHighestAGFactor() {
        Process highestAG = readyList.get(0);
        for (Process process : readyList) {
            if (process.agFactor > highestAG.agFactor)
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

    private void printProcessStatistics() {
        System.out.println("====================================================================================================================");
        dieList.sort((p1, p2) -> p1.pid - p2.pid);
        for (Process process : dieList) {
            String processOutput = "Executing process = " + process.pid;
            processOutput += " | Arrival Time = " + process.arrivalTime;
            processOutput += " | Burst Time = " + processesBurstTime.get(process.pid);
            processOutput += " | Priority = " + process.priority;
            processOutput += " | Waiting Time = " + waitingTimes.get(process.pid);
            processOutput += " | Turnaround Time = " + turnaroundTimes.get(process.pid);
            System.out.println(processOutput);
        }
        System.out.println("====================================================================================================================");
        System.out.println("Average Waiting Time = " + avgWaitingTime);
        System.out.println("Average Turnaround Time = " + avgTurnaroundTime);
    }

    private void calcTurnaroundAndWaitingAvgTime() {
        for (Process process : dieList) {
            avgTurnaroundTime += turnaroundTimes.get(process.pid);
            avgWaitingTime += waitingTimes.get(process.pid);
        }
        avgTurnaroundTime /= dieList.size();
        avgWaitingTime /= dieList.size();
    }

    public void run() {
        int time = 0;
        int processQuantumCounter = 0;
        int currentProcessIndex = 0;

        while (readyList.size() > 0 || waitList.size() > 0) {
            checkArrivals(time);
            time++;
            if (readyList.isEmpty()) {
                System.out.println("At t = " + (time - 1) + " CPU IDLE");
                continue;
            } else {
                System.out
                        .println("At t = " + (time - 1) + " - Process ID = " + readyList.get(currentProcessIndex).pid);
            }
            processQuantumCounter++;
            int highestAGIndex = getIndexOfHighestAGFactor();

            updateBurstTime(currentProcessIndex, readyList.get(currentProcessIndex).burstTime - 1);

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
                turnaroundTimes.put(currentProcess.pid, time - currentProcess.arrivalTime + 1);
                waitingTimes.put(currentProcess.pid,
                        turnaroundTimes.get(currentProcess.pid) - processesBurstTime.get(currentProcess.pid));
                currentProcessIndex = 0;
                processQuantumCounter = 0;
            }
            // Now the preemptive part
            else if (processQuantumCounter >= Math.ceil((double) currentProcess.quantum / 2)) {
                // Preemptive
                if (currentProcess.agFactor < readyList.get(highestAGIndex).agFactor
                        && currentProcessIndex != highestAGIndex) {
                    // Give the current process the remaining quantum time of itself
                    int newQuantum = currentProcess.quantum - processQuantumCounter;
                    newQuantum += currentProcess.quantum;
                    updateQuantum(currentProcessIndex, newQuantum);
                    currentProcessIndex = highestAGIndex;
                    processQuantumCounter = 0;
                }
            }
        }
        calcTurnaroundAndWaitingAvgTime();
        printProcessStatistics();
    }

}
