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
}
