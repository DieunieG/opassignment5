import java.util.*;

class Process {
    int id;
    int burst;
    int arrival;

    // computed
    int startTime = -1;
    int completionTime;
    int waitingTime;
    int turnaroundTime;

    Process(int id, int burst, int arrival) {
        this.id = id;
        this.burst = burst;
        this.arrival = arrival;
    }
}

public class Main {
    public static void main(String[] args) {
        // Q2 data: P1=2, P2=1, P3=8, P4=4, P5=5, all arrival 0
        List<Process> base = Arrays.asList(
                new Process(1, 2, 0),
                new Process(2, 1, 0),
                new Process(3, 8, 0),
                new Process(4, 4, 0),
                new Process(5, 5, 0)
        );

        runFCFS(cloneList(base), "FCFS");
        runSJF(cloneList(base), "SJF");
    }

    private static List<Process> cloneList(List<Process> src) {
        List<Process> copy = new ArrayList<>();
        for (Process p : src) copy.add(new Process(p.id, p.burst, p.arrival));
        return copy;
    }

    private static void runFCFS(List<Process> procs, String title) {
        // order by arrival then id
        procs.sort(Comparator.<Process>comparingInt(p -> p.arrival)
                .thenComparingInt(p -> p.id));

        int time = 0;
        for (Process p : procs) {
            time = Math.max(time, p.arrival);
            p.startTime = time;
            time += p.burst;
            p.completionTime = time;
            p.turnaroundTime = p.completionTime - p.arrival;
            p.waitingTime = p.turnaroundTime - p.burst;
        }

        printResults(title, procs);
    }

    private static void runSJF(List<Process> procs, String title) {
        // nonpreemptive SJF, break ties by burst then id
        int n = procs.size();
        int time = 0, finished = 0;

        // since all arrivals are 0, we can simply sort by burst then id
        // but this also works generally with arrivals
        while (finished < n) {
            final int currentTime = time; // make a final snapshot
            Process next = procs.stream()
                    .filter(p -> p.startTime == -1 && p.arrival <= currentTime)
                    .min(Comparator.<Process>comparingInt(p -> p.burst)
                            .thenComparingInt(p -> p.id))
                    .orElse(null);


            if (next == null) {
                // CPU idle until the next arrival
                int earliestArrival = procs.stream()
                        .filter(p -> p.startTime == -1)
                        .mapToInt(p -> p.arrival)
                        .min().orElse(time);
                time = Math.max(time, earliestArrival);
                continue;
            }

            next.startTime = time;
            time += next.burst;
            next.completionTime = time;
            next.turnaroundTime = next.completionTime - next.arrival;
            next.waitingTime = next.turnaroundTime - next.burst;
            finished++;
        }

        // print in ID order to match the example style
        procs.sort(Comparator.comparingInt(p -> p.id));
        printResults(title, procs);
    }

    private static void printResults(String title, List<Process> procs) {
        System.out.println("----------------- " + title + " -----------------");
        System.out.println("Process ID | Waiting Time | Turnaround Time");
        double totalWait = 0, totalTurn = 0;

        // keep a stable order for readability: by id
        procs.sort(Comparator.comparingInt(p -> p.id));
        for (Process p : procs) {
            System.out.printf("%9d | %12d | %15d%n",
                    p.id, p.waitingTime, p.turnaroundTime);
            totalWait += p.waitingTime;
            totalTurn += p.turnaroundTime;
        }

        System.out.printf("Average Waiting Time: %.2f%n", totalWait / procs.size());
        System.out.printf("Average Turnaround Time: %.2f%n%n", totalTurn / procs.size());
    }
}
