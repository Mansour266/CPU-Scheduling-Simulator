import org.w3c.dom.css.RGBColor;

import java.util.Scanner;
class AGScheduling{

}
public class CPU_Simulator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("_____________________________");
        System.out.println("Choose a scheduling method: ");
        System.out.println("_____________________________");
        System.out.println("1. Non-Preemptive Shortest Job First");
        System.out.println("2. Shortest Remaining Time First");
        System.out.println("3. Non-Preemptive Priority");
        System.out.println("4. AG Scheduling");
        System.out.println("_____________________________");
        System.out.print("Choice: ");
        int schMethod = scanner.nextInt();


        System.out.print("Enter number of processes: ");
        int numProcs = scanner.nextInt();

        Process[] processes = new Process[numProcs];
        for (int i = 0; i < numProcs; i++) {
            processes[i] = new Process();
            System.out.println("_____________________________");
            System.out.println("Process " + (i+1) + ": ");
            System.out.println("_____________________________");
            System.out.print("Process ID: ");
            processes[i].pid = scanner.nextInt();
            System.out.print("Arrival Time: ");
            processes[i].arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            processes[i].burstTime = scanner.nextInt();
            System.out.print("Priority: ");
            processes[i].priority = scanner.nextInt();

        }


    }
}
