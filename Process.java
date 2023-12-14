class Process{
    int pid;
    int arrivalTime;
    int burstTime;
    int priority;
//        RGBColor color;

    Process(int pid, int arrivalTime, int burstTime, int priority){
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}