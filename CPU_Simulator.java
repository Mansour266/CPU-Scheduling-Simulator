import java.util.*;

public class CPU_Simulator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=============================");
        System.out.print("Enter number of processes: ");
        int numProcs = scanner.nextInt();

        System.out.print("Enter the Round Robin Quantum: ");
        int quantum = scanner.nextInt();

        // System.out.print("Enter the switching context time: ");
        Process[] processes = new Process[numProcs];
        for (int i = 0; i < numProcs; i++) {
            processes[i] = new Process();
            System.out.println("=============================");
            System.out.println("Process " + (i+1) + ": ");
            System.out.println("==========");
            processes[i].pid = i+1;
            System.out.print("Burst Time: ");
            processes[i].burstTime = scanner.nextInt();
            System.out.print("Arrival Time: ");
            processes[i].arrivalTime = scanner.nextInt();
            System.out.print("Priority: ");
            processes[i].priority = scanner.nextInt();
            processes[i].quantum = quantum;

        }
        System.out.println("=============================");
        System.out.println("AG:");
        AG agScheduling = new AG(processes);
        agScheduling.run();
        System.out.println("=============================");
    }
}
// AG example in assignment: 4 4 17 0 4 6 3 9 10 4 3 4 29 8