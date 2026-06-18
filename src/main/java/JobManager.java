import java.util.ArrayList;
import java.util.List;

public class JobManager {
    private static final List<Job> jobs = new ArrayList<>();

    private static int nextJobNumber = 1;

    public static void addJob(Process process, String command) {
        jobs.add(new Job(nextJobNumber++, process, command));
    }

    public static List<Job> getJobs() {
        return jobs;
    }

    public static void printJobs() {
        int last = jobs.size() - 1;
        int secondLast = jobs.size() - 2;

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);

            char marker = ' ';

            if (i == jobs.size() - 1) {
                marker = '+';
            }
            else if (i == jobs.size() - 2) {
                marker = '-';
            }

            System.out.printf("[%d]%c  %-24s%s%n", job.jobNumber, marker, "Running", job.command);
        }
    }
}
