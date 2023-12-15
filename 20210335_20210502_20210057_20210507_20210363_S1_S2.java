import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

class Process {
    int pid;
    int arrivalTime;
    int burstTime;
    int priority;

    // For AG Scheduling
    public int quantum;
    public int agFactor;

    Process(int pid, int arrivalTime, int burstTime, int priority, int quantum) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
    }
}

class SJF {
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

class SRTFScheduler {
    int totalWaitingTimes;
    int totalTurnAroundTimes;
    ArrayList<String> schedulerData;
    int numOfProcesses; // num of processes in input processes
    int completedProcesses;
    private final double REMAINING_TIME_WEIGHT = 2;
    private final double AGING_PRIORITY_WEIGHT = 1;

    class SRTFProcess {
        int pid;
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int agePriorityLevel;

        SRTFProcess(int pid, int arrivalTime, int burstTime) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
            this.agePriorityLevel = 0;

        }
    }

    ArrayList<SRTFProcess> readyQueue;
    ArrayList<Process> inputProcesses;

    SRTFScheduler(ArrayList<Process> processes) {
        numOfProcesses = processes.size();
        readyQueue = new ArrayList<SRTFProcess>();
        inputProcesses = new ArrayList<Process>(processes);
        totalTurnAroundTimes = 0;
        totalWaitingTimes = 0;
        completedProcesses = 0;
        schedulerData = new ArrayList<String>();
    }

    void addSRTFProcess(SRTFProcess p) {
        readyQueue.add(p);
    }

    void runSRTF() {
        int currentTime = 0;

        while (completedProcesses < numOfProcesses) {

            /*
             * increase age priority level for all processes in ready queue every
             * 2 seconds (aging) to solve the problem of starvation
             */
            if (!readyQueue.isEmpty() && currentTime % 2 == 0) {
                for (SRTFProcess p : readyQueue) {
                    p.agePriorityLevel++;
                }
            }

            Iterator<Process> iterator = inputProcesses.iterator();
            while (iterator.hasNext()) {
                Process p = iterator.next();
                if (currentTime >= p.arrivalTime) {
                    readyQueue.add(new SRTFProcess(p.pid, p.arrivalTime, p.burstTime));
                    iterator.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                System.out.println("At t=" + currentTime + " CPU IDLE");
                currentTime++;
            } else {

                int currentProcessIndex = -1;
                double weight = -1;
                int len = readyQueue.size();
                for (int i = 0; i < len; i++) {
                    double currentWeight = ((double) 1 / readyQueue.get(i).remainingTime) * REMAINING_TIME_WEIGHT
                            + readyQueue.get(i).agePriorityLevel * AGING_PRIORITY_WEIGHT;
                    if (currentWeight > weight) {
                        currentProcessIndex = i;
                        weight = currentWeight;
                    }

                }

                readyQueue.get(currentProcessIndex).remainingTime--;

                SRTFProcess srtfProcess = readyQueue.get(currentProcessIndex);
                System.out.println("At t=" + currentTime + " - Process ID: " + srtfProcess.pid);

                if (srtfProcess.remainingTime == 0) {
                    int waitingTime = currentTime + 1 - srtfProcess.burstTime - srtfProcess.arrivalTime;
                    int turnAroundTime = currentTime + 1 - srtfProcess.arrivalTime;

                    totalTurnAroundTimes += turnAroundTime;
                    totalWaitingTimes += waitingTime;

                    schedulerData.add("Executing process: " + srtfProcess.pid + " | Waiting Time: "
                            + waitingTime + " | Turnaround Time: " + turnAroundTime);

                    readyQueue.remove(currentProcessIndex);
                    completedProcesses++;

                }

                currentTime++;
            }

        }

        System.out.println("-------");
        for (String s : schedulerData) {
            System.out.println(s);
        }

        double averageWaitingTime = (double) totalWaitingTimes / numOfProcesses;
        double averageTurnaroundTime = (double) totalTurnAroundTimes / numOfProcesses;

        System.out.println("-------");
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("-------");
    }

}

class PriorityScheduler {
    private ArrayList<Process> processes;
    private HashMap<Process, Integer> executedProcesses = new HashMap<Process, Integer>();

    // Priority queue to sort the processes according to their priority and arrival
    // time.
    PriorityQueue<Process> readyQueue = new PriorityQueue<Process>(new Comparator<Process>() {
        @Override
        public int compare(Process p1, Process p2) {
            // Compare based on priority, and if priorities are equal, compare based on
            // arrival time.
            return Integer.compare(p1.priority, p2.priority) == 0
                    ? Integer.compare(p1.arrivalTime, p2.arrivalTime)
                    : Integer.compare(p2.priority, p1.priority);
        }
    });

    // Constructor to sort the processes according to their arrival time.
    public PriorityScheduler(ArrayList<Process> processes) {
        this.processes = new ArrayList<Process>(processes);
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

        // This is to check if there's any process still executing or if there are any
        // in the ready queue so it doesn't skip them.
        while (processes.size() > 0 || !readyQueue.isEmpty() || currentProcess != null) {

            // Solving starvation by increasing lower processes' priority every 5 seconds
            // (Aging).
            // Put it here so that if a new process is added after, it's priority doesn't
            // increase right away, and should stay 5 seconds first.
            if (time % 5 == 0 && !readyQueue.isEmpty()) {
                int highestPriority = readyQueue.peek().priority;

                for (Process process : readyQueue) {
                    if (process.priority < highestPriority) {
                        process.priority++;
                    }
                }
            }

            // Loading processes into the ready queue according to the current time and
            // their arrival time.
            if (!processes.isEmpty()) {

                // Had to implement this cause it gives an error if it loops over the ArrayList
                // after clearing it.
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
            if (currentProcess != null && currentProcess.burstTime == time - execTime) {
                executedProcesses.put(currentProcess, execTime);
                currentProcess = null;
            }

            // Making sure the current process is empty first before assigning it to another
            // process (Non-preemptive).
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                execTime = time;
            }

            // Checks if there is a process to be executed or if the CPU should stay IDLE.
            if (currentProcess == null) {
                System.out.println("At t = " + time + " - CPU IDLE");
            } else {
                System.out.println("At t = " + time + " - Process ID: " + currentProcess.pid + " - Priority: "
                        + currentProcess.priority);
            }

            time++;
        }

        System.out.println("-------");
        double averageTurnaroundTime = 0, averageWaitingTime = 0, processCount = 0;

        // Print statistics about each process
        for (Map.Entry<Process, Integer> process : executedProcesses.entrySet()) {
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
    }
}

class AG {
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
        System.out.println("-------");
        dieList.sort((p1, p2) -> p1.pid - p2.pid);
        for (Process process : dieList) {
            String processOutput = "Executing process: " + process.pid;
            processOutput += " | Arrival Time: " + process.arrivalTime;
            processOutput += " | Burst Time: " + processesBurstTime.get(process.pid);
            processOutput += " | Priority: " + process.priority;
            processOutput += " | Waiting Time: " + waitingTimes.get(process.pid);
            processOutput += " | Turnaround Time: " + turnaroundTimes.get(process.pid);
            System.out.println(processOutput);
        }
        System.out.println("-------");
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
                turnaroundTimes.put(currentProcess.pid, time - currentProcess.arrivalTime);
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

class CPU_Simulator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=============================");
        System.out.print("Enter number of processes: ");
        int numProcs = scanner.nextInt();

        System.out.print("Enter the Round Robin Quantum: ");
        int quantum = scanner.nextInt();

        System.out.print("Enter the switching context time: ");
        int contextSwitch = scanner.nextInt();

        ArrayList<Process> processes = new ArrayList<Process>();
        for (int i = 0; i < numProcs; i++) {

            System.out.println("=============================");

            System.out.print("Process ID: ");
            int id = scanner.nextInt();

            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            System.out.print("Priority: ");
            int priority = scanner.nextInt();

            processes.add(new Process(id, arrivalTime, burstTime, priority, quantum));
        }

        // SJF (Pb1)
        System.out.println("=============================");
        System.out.println("--SJF SCHEDULER START--");
        SJF sjf = new SJF(processes);
        sjf.runSJF();
        System.out.println("--SJF SCHEDULER END--");
        System.out.println("=============================");

        System.out.println();

        // SRTF (Pb2)
        System.out.println("=============================");
        System.out.println("--SRTF SCHEDULER START--");
        SRTFScheduler srtfScheduler = new SRTFScheduler(processes);
        srtfScheduler.runSRTF();
        System.out.println("--SRTF SCHEDULER END--");
        System.out.println("=============================");

        System.out.println();

        // Priority Scheduler (Pb3)
        System.out.println("=============================");
        System.out.println("--PRIORITY SCHEDULER START--");
        PriorityScheduler priorityScheduler = new PriorityScheduler(processes);
        priorityScheduler.run();
        System.out.println("--PRIORITY SCHEDULER END--");
        System.out.println("=============================");

        System.out.println();

        // AG Scheduler (Pb4)
        System.out.println("=============================");
        System.out.println("--AG SCHEDULER START--");
        AG ag = new AG(processes);
        ag.run();
        System.out.println("--AG SCHEDULER END--");
        System.out.println("=============================");
        System.out.println();

    }
}
