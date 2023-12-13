import org.w3c.dom.css.RGBColor;

public class Process {
    public int pid;
    public int arrivalTime;
    public int burstTime;
    public int priority;
    public RGBColor color;
    public String name;

    // For AG Scheduling
    public int quantum;
    public int agFactor;
}
