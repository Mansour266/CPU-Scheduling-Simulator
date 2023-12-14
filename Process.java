
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


//public class Process {
//    public int pid;
//    public int arrivalTime;
//    public int burstTime;
//    public int priority;
//    public RGBColor color;
//    public String name;
//
//    // For AG Scheduling
//    public int quantum;
//    public int agFactor;
//}

