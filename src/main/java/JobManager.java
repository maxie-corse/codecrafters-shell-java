import java.util.ArrayList;
import java.util.List;

public class JobManager {
    private static final List<Job> jobs = new ArrayList<>();

    public static void addJob(Process process, String command) {
        int jobNumber = getNextJobNumber();

        jobs.add(new Job(jobNumber, process, command));
    }

    private static int getNextJobNumber() {

        int candidate = 1;

        while (true) {

            boolean used = false;

            for (Job job : jobs) {

                if (job.jobNumber == candidate) {
                    used = true;
                    break;
                }
            }

            if (!used) {
                return candidate;
            }

            candidate++;
        }
    }

    public static List<Job> getJobs() {
        return jobs;
    }

    public static void printJobs() {

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);

            char marker = ' ';

            if (i == jobs.size() - 1) {
                marker = '+';
            }
            else if (i == jobs.size() - 2) {
                marker = '-';
            }

            System.out.printf(
                "[%d]%c  %-24s%s%n",
                job.jobNumber,
                marker,
                "Running",
                job.command
            );
        }

        jobs.removeIf(job -> job.doneDisplayed);
    }

    public static boolean isDone(Job job) {
        return !job.process.isAlive();
    }

    public static void reapCompletedJobs() {

        for (int i = 0; i < jobs.size(); i++) {

            Job job = jobs.get(i);

            if (isDone(job)) {

                char marker = ' ';

                if (i == jobs.size() - 1) {
                    marker = '+';
                }
                else if (i == jobs.size() - 2) {
                    marker = '-';
                }

                System.out.printf(
                    "[%d]%c  %-24s%s%n",
                    job.jobNumber,
                    marker,
                    "Done",
                    job.command.replace(" &", "")
                );

                job.doneDisplayed = true;
            }
        }

        jobs.removeIf(job -> job.doneDisplayed);
    }

    public static void printAndReapJobs() {

        for (int i = 0; i < jobs.size(); i++) {

            Job job = jobs.get(i);

            char marker = ' ';

            if (i == jobs.size() - 1) {
                marker = '+';
            }
            else if (i == jobs.size() - 2) {
                marker = '-';
            }

            if (job.process.isAlive()) {

                System.out.printf(
                    "[%d]%c  %-24s%s%n",
                    job.jobNumber,
                    marker,
                    "Running",
                    job.command
                );
            }
            else {

                System.out.printf(
                    "[%d]%c  %-24s%s%n",
                    job.jobNumber,
                    marker,
                    "Done",
                    job.command.replace(" &", "")
                );

                job.doneDisplayed = true;
            }
        }

        jobs.removeIf(job -> job.doneDisplayed);
    }
}
