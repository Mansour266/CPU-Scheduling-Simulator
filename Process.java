
class Process{
    int pid;
    int arrivalTime;
    int burstTime;
    int priority;

    // for AG Scheduling
    public int quantum;
    public int agFactor;

    Process(int pid, int arrivalTime, int burstTime, int priority, int quantum){
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
    }
}

