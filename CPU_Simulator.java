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

        ArrayList<Process> processes = new ArrayList<Process>();
        for (int i = 0; i < numProcs; i++) {

            System.out.println("=============================");
            System.out.println("Process ID: ");
            int id = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();

            processes.add(new Process(id, arrivalTime, burstTime, priority));

//            processes[i].quantum = quantum;

        }
//        System.out.println("=============================");
//        System.out.println("AG:");
//        AG agScheduling = new AG(processes);
//        agScheduling.run();
//        System.out.println("=============================");
    }
}
// AG example in assignment: 4 4 17 0 4 6 3 9 10 4 3 4 29 8